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

import me.schlaubi.commandcord.command.BlackListProvider;
import me.schlaubi.commandcord.command.PrefixProvider;
import me.schlaubi.commandcord.command.permission.Member;
import me.schlaubi.commandcord.command.permission.PermissionProvider;
import me.schlaubi.pterodactylbot.PterodactylBot;
import me.schlaubi.pterodactylbot.io.database.MySQL;
import net.dv8tion.jda.bot.sharding.ShardManager;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InformationProvider implements PrefixProvider, BlackListProvider, PermissionProvider {

    private final Logger logger = Logger.getLogger(InformationProvider.class);
    private final MySQL mySQL = PterodactylBot.getInstance().getMySQL();
    private final ShardManager shardManager = PterodactylBot.getInstance().getShardManager();

    @Override
    public boolean isBlackListed(String textChannelId, String guildId) {
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT * FROM guilds WHERE guildId = ?");
            ps.setString(1, guildId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return  rs.getString("blacklistedChannels").contains(textChannelId);
        } catch (SQLException e) {
            logger.warn(String.format("[INFORMATIONPROVIDER] An error occured while retireving blacklisted channels for channel %s", textChannelId));
        }
        return false;
    }

    @Override
    public String getPrefix(String guildId) {
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT prefix FROM guilds WHERE guildId = ?");
            ps.setString(1, guildId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getString("prefix");
        } catch (SQLException e) {
            logger.warn(String.format("[INFORMATIONPROVIDER] An error occurred while fetching prefix für guild %s", guildId), e);
            return PterodactylBot.getInstance().getConfiguration().getJSONObject("settings").getString("prefix");
        }
        return PterodactylBot.getInstance().getConfiguration().getJSONObject("settings").getString("prefix");
    }

    @Override
    public boolean isGuildOwner(Member member) {
        return shardManager.getGuildById(member.getGuildId()).getMemberById(member.getUserId()).isOwner();
    }

    @Override
    public boolean isBotAuthor(Member member) {
        return PterodactylBot.getInstance().getConfiguration().getJSONArray("owners").toString().contains(member.getUserId());
    }

    @Override
    public boolean hasPermissionNode(Member member, String permissionNode) {
        return false;
    }

    @Override
    public boolean hasPermissionLevel(Member member, int permissionLevel) {
        try {
            PreparedStatement ps = mySQL.getConnection().prepareStatement("SELECT permissionLevel FROM permissions WHERE guildId = ? AND userId =?");
            ps.setString(1, member.getGuildId());
            ps.setString(2, member.getUserId());
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("permissionLevel") >= permissionLevel;
        } catch (SQLException e) {
            logger.warn(String.format("[INFORMATIONPROVIDER] An error ocurred while retriving permission level for user %s on guild %s", member.getUserId(), member.getUserId()));
        }
        return false;
    }
}
