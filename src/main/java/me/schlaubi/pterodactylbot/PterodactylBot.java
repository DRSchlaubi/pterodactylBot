/*
 * PterodactylBot - An open-source Discord integration for Pterodactyl
 * Copyright (C) 2018  Michael Rittmeister
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package me.schlaubi.pterodactylbot;

import me.schlaubi.commandcord.core.APIWrapper;
import me.schlaubi.commandcord.core.CommandManager;
import me.schlaubi.commandcord.core.CommandManagerBuilder;
import me.schlaubi.commandcord.listeners.jda.JDAListener;
import me.schlaubi.commandcord.util.helpcommands.JDAHelpCommand;
import me.schlaubi.pterodactylbot.commands.settings.PrefixCommand;
import me.schlaubi.pterodactylbot.core.GameAnimator;
import me.schlaubi.pterodactylbot.core.InformationProvider;
import me.schlaubi.pterodactylbot.core.translation.TranslationManager;
import me.schlaubi.pterodactylbot.io.FileManager;
import me.schlaubi.pterodactylbot.io.config.Configuration;
import me.schlaubi.pterodactylbot.io.database.MySQL;
import me.schlaubi.pterodactylbot.listener.CommandListener;
import me.schlaubi.pterodactylbot.listener.DatabaseListener;
import me.schlaubi.pterodactylbot.listener.SelfMentionListener;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PterodactylBot {

    private final Logger logger = Logger.getLogger(PterodactylBot.class);
    public static final String VERSION = "1.0.0";

    private static PterodactylBot instance;
    private final MySQL mySQL;
    private final Configuration configuration;
    private final OkHttpClient httpClient;
    private final GameAnimator gameAnimator;
    private CommandManager commandManager;
    private final int MAX_SHARD_COUNT = 5;
    private ShardManager shardManager;
    private final TranslationManager translationManager;
    private InformationProvider informationProvider;

    public static void main(String[] args) {
        if (instance != null)
            throw new RuntimeException("PterodactylBot is already initialized in this VM");
        new PterodactylBot();
    }

    private PterodactylBot() {
        instance = this;
        //Create HTTP client
        httpClient = new OkHttpClient();
        //Create files
        new FileManager();
        //Init logger
        initLogger();
        logger.info("[LOGGER] Logger initialized");
        //Init config
        configuration = initConfig();
        //Init translations
        translationManager = new TranslationManager();
        //Connect to MySQL
        mySQL = new MySQL();
        //Create default databases
        createDefaultDatabases();
        //Init JDA
        initDiscordBot();
        //Init game animator
        gameAnimator = new GameAnimator(shardManager);
        gameAnimator.start();
    }

    private void initDiscordBot() {
        DefaultShardManagerBuilder builder = new DefaultShardManagerBuilder()
                .setToken(configuration.getJSONObject("bot").getString("token"))
                .setShardsTotal(retriveShardCount())
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .addEventListeners(
                        new JDAListener(),
                        new SelfMentionListener(),
                        new DatabaseListener()
                        )
                .setGame(Game.playing("Starting ..."));
        try {
            shardManager = builder.build();
        } catch (Exception e) {
            logger.error("[JDA] An error occured while starting bot", e);
        }

        informationProvider = new InformationProvider();
        commandManager = new CommandManagerBuilder(APIWrapper.JDA)
                .setApi(shardManager)
                .setDefaultPrefix(configuration.getJSONObject("settings").getString("default_prefix"))
                .deleteCommandMessages(7)
                .enableBlacklist(true)
                .setBlacklistProvider(informationProvider)
                .authorIsAdmin(true)
                .enableTyping(true)
                .enableGuildPrefixes(true)
                .setPrefixProvider(informationProvider)
                .setPermissionProvider(informationProvider)
                .build();
        commandManager.registerCommands(
                new JDAHelpCommand(),
                new PrefixCommand()
        );
        commandManager.getEventManager().registerListener(new CommandListener());
    }

    private void createDefaultDatabases() {
        mySQL.addDefaultDatabase(() -> "CREATE TABLE IF NOT EXISTS guilds\n" +
                "(\n" +
                "    id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    guildId BIGINT,\n" +
                "    prefix VARCHAR(50),\n" +
                "    blacklistedChannels TEXT\n" +
                ");");
        mySQL.addDefaultDatabase(() -> "CREATE TABLE IF NOT EXISTS users\n" +
                "(\n" +
                "    id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    userId BIGINT,\n" +
                "    language VARCHAR(50)\n" +
                ");");
        mySQL.addDefaultDatabase(() -> "CREATE TABLE IF NOT EXISTS permissions\n" +
                "(\n" +
                "    id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                "    guildId BIGINT,\n" +
                "    userId BIGINT,\n" +
                "    permissionLevel int\n" +
                ");");
        mySQL.createMySQLDatabases();
    }

    private int retriveShardCount() {
        Request req = new Request.Builder()
                .url("https://discordapp.com/api/gateway/bot")
                .header("Authorization", configuration.getJSONObject("bot").getString("token"))
                .header("User-Agent", "PterdactylBot")
                .get()
                .build();
        try (Response response = httpClient.newCall(req).execute()){
            assert response.body() != null;
            Integer shardcount = new JSONObject(response.body().string()).getInt("shards");
            logger.info(String.format("[JDA] Starting with %d shards", shardcount));
            return shardcount;
        } catch (IOException | NullPointerException | JSONException e) {
            logger.error("[JDA] Error while retriving shard count using maximum count", e);
            return MAX_SHARD_COUNT;
        }
    }

    private void initLogger() {
        final ConsoleAppender consoleAppender = new ConsoleAppender();
        final PatternLayout consolePatternLayout = new PatternLayout("(%d{HH:mm:ss}) [Bot] [%p] | %m%n");
        consoleAppender.setLayout(consolePatternLayout);
        consoleAppender.activateOptions();
        Logger.getRootLogger().addAppender(consoleAppender);

        final PatternLayout filePatternLayout = new PatternLayout("(%d{dd.MM.yyyy HH:mm:ss}) [Bot] [%p] | %m%n");

        final FileAppender latestlogAppender = new FileAppender();
        latestlogAppender.setName("FileLogger");
        latestlogAppender.setFile("logs/latest.log");
        latestlogAppender.setLayout(filePatternLayout);
        latestlogAppender.activateOptions();
        Logger.getRootLogger().addAppender(latestlogAppender);

        final FileAppender dateFileLog = new FileAppender();
        dateFileLog.setName("FileLogger");
        dateFileLog.setFile(String.format("logs/%s.log", new SimpleDateFormat("dd_MM_yyyy-HH_mm").format(new Date())));
        dateFileLog.setLayout(filePatternLayout);
        dateFileLog.activateOptions();
        Logger.getRootLogger().addAppender(dateFileLog);
    }

    private Configuration initConfig() {
        logger.info("[CONFIGURATION] Initializing config");
        Configuration out = new Configuration("config/config.json");
        final JSONObject botObject = new JSONObject();
        botObject.put("token", "nicetoken");
        out.addDefault("bot", botObject);
        final JSONObject databaseObject = new JSONObject();
        databaseObject.put("host", "example.com");
        databaseObject.put("port", 3306);
        databaseObject.put("name", "pterodactylBot");
        databaseObject.put("user", "ptero");
        databaseObject.put("password", "SUPER SECURE PASSWORD");
        out.addDefault("database", databaseObject);
        out.addDefault("games", new JSONArray()
                .put("p: On %servers% servers")
                .put("l: To %default_prefix%")
                .put("w: PterodactylBot version %version% | Made by Schlaubi")
        );
        final JSONObject settingsObject = new JSONObject();
        settingsObject.put("default_prefix", "!");
        out.addDefault("settings", settingsObject);
        out.addDefault("owners", new JSONArray().put("264048760580079616"));
        return out.init();
    }

    public static PterodactylBot getInstance() {
        return instance;
    }

    public MySQL getMySQL() {
        return mySQL;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ShardManager getShardManager() {
        return shardManager;
    }

    public TranslationManager getTranslationManager() {
        return translationManager;
    }

    public InformationProvider getInformationProvider() {
        return informationProvider;
    }
}
