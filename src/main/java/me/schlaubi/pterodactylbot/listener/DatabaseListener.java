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

package me.schlaubi.pterodactylbot.listener;

import me.schlaubi.pterodactylbot.PterodactylBot;
import me.schlaubi.pterodactylbot.core.entity.DatabaseGuild;
import me.schlaubi.pterodactylbot.core.entity.DatabaseUser;
import me.schlaubi.pterodactylbot.io.database.MySQL;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class DatabaseListener extends ListenerAdapter {

    private final Logger logger = Logger.getLogger(DatabaseListener.class);

    private MySQL mySQL = PterodactylBot.getInstance().getMySQL();

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        try {
            PterodactylBot.getInstance().getGuildCache().add(event.getGuild().getIdLong(), new DatabaseGuild(event.getGuild().getIdLong(), "p!"));
            logger.info(String.format("[DB] Added guild %s to database", event.getGuild().getId()));
        } catch (Exception e) {
            logger.warn("[DATABASE] Unable to add guild to database", e);
        }
        new Thread(() -> event.getGuild().getMembers().forEach(member -> insertUser(member.getUser().getId())), "GuildJoinThread").start();
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("DELETE FROM guilds WHERE guildId = ?");
            ps.setString(1, event.getGuild().getId());
            ps.execute();
            logger.info(String.format("[DB] Deleted guild %s to database", event.getGuild().getId()));
        } catch (SQLException e) {
            logger.warn("[DATABASE] Unable to delete guild to database", e);
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        insertUser(event.getUser().getId());
    }

    private void insertUser(String userId) {
        try {
            PreparedStatement existsStatement = mySQL.getConnection().prepareStatement("SELECT * FROM users WHERE userId = ?");
            existsStatement.setString(1, userId);
            ResultSet rs = existsStatement.executeQuery();
            if (!rs.next()) {
                PterodactylBot.getInstance().getUserCache().add(Long.valueOf(userId), new DatabaseUser(Long.parseLong(userId), new Locale("en", "US")));
                logger.info(String.format("[DB] Added user %s to database", userId));
            }
        }  catch (Exception e) {
            logger.warn("[DATABASE] Unable to add user to database", e);
        }
    }
}
