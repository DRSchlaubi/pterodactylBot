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

package me.schlaubi.pterodactylbot.core;

import me.schlaubi.pterodactylbot.PterodactylBot;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import org.apache.log4j.Logger;
import org.json.JSONArray;

import java.util.Timer;
import java.util.TimerTask;

public class GameAnimator extends TimerTask {

    private final Logger logger = Logger.getLogger(GameAnimator.class);
    private Timer timer = new Timer();
    private ShardManager shardManager;
    private int count = 0;
    private JSONArray games = PterodactylBot.getInstance().getConfiguration().getJSONArray("games");

    public GameAnimator(ShardManager shardManager) {
        this.shardManager = shardManager;
        shardManager.setStatus(OnlineStatus.ONLINE);
    }

    @Override
    public void run() {
        if (count > games.length() - 1)
            count = 0;
        shardManager.setGame(parseGame(games.getString(count)));
        count++;
    }

    public synchronized void start() {
        logger.info("[GAMEANIMATOR] Starting game-animator ...");
        timer.scheduleAtFixedRate(this, 0L, 30000L);
    }

    public void stop() {
        timer.cancel();
    }

    private Game parseGame(String expression) {
        expression = expression.replace("%servers%", String.valueOf(shardManager.getGuilds().size() + 1)).replace("%version%", PterodactylBot.VERSION).replace("%default_prefix%", PterodactylBot.getInstance().getConfiguration().getJSONObject("settings").getString("default_prefix"));
        if (expression.startsWith("p: "))
            return Game.playing(expression.replaceFirst("p: ", ""));
        else if (expression.startsWith("w: "))
            return Game.watching(expression.replaceFirst("w: ", ""));
        else
            return Game.listening(expression.replaceFirst("l: ", ""));
    }

}
