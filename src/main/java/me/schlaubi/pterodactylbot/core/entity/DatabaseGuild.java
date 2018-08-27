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

package me.schlaubi.pterodactylbot.core.entity;

import me.schlaubi.pterodactylbot.PterodactylBot;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseGuild extends DatabaseEntity {

    private Long guildId;
    private String prefix;
    private List<String> blacklistedChannels;

    public DatabaseGuild(Long guildId) throws Exception {
        super(guildId);
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM guilds WHERE guildId = ?");
        ps.setLong(1, guildId);
        this.blacklistedChannels = new ArrayList<>();
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            this.guildId = rs.getLong("guildId");
            this.prefix = rs.getString("prefix");
            if (!rs.getString("blacklistedChannels").equals(""))
                this.blacklistedChannels.addAll(Arrays.asList(rs.getString("blacklistedChannels").split(",")));
        }
    }

    @Override
    public void updateInDatabase() throws Exception {
        PreparedStatement ps = getConnection().prepareStatement("UPDATE guilds SET prefix = ?, blacklistedChannels = ?");
        ps.setString(1, prefix);
        ps.setString(2, Arrays.toString(blacklistedChannels.toArray()).replace("[", "").replace("]", ""));
        ps.execute();
    }

    public DatabaseGuild(Long entityId, String prefix) throws Exception {
        super(entityId);
        PreparedStatement ps = getConnection().prepareStatement("INSERT INTO guilds (guildId, prefix, blacklistedChannels) VALUES (?,?,?)");
        ps.setLong(1, entityId);
        ps.setString(2, prefix);
        ps.setString(3, "");
        ps.execute();
        this.prefix = prefix;
        this.blacklistedChannels = new ArrayList<>();
    }

    public Long getGuildId() {
        return guildId;
    }

    public String getPrefix() {
        return prefix;
    }

    public List<String> getBlacklistedChannels() {
        return blacklistedChannels;
    }

    public void setPrefix(String prefix) throws Exception {
        this.prefix = prefix;
        PterodactylBot.getInstance().getGuildCache().update(this);
    }

    public void blacklistChannel(String channelId) throws Exception {
        blacklistedChannels.add(channelId);
        PterodactylBot.getInstance().getGuildCache().update(this);
    }

    public void unBlacklistChannel(String channelId) throws Exception {
        blacklistedChannels.remove(channelId);
        PterodactylBot.getInstance().getGuildCache().update(this);
    }

    public boolean isChannelBlacklisted(String channelId) {
        return blacklistedChannels.contains(channelId);
    }
}
