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
import java.util.Locale;

public class DatabaseUser extends DatabaseEntity {

    private Locale locale;

    public DatabaseUser(Long userId) throws Exception {
        super(userId);
        PreparedStatement ps = getConnection().prepareStatement("SELECT * FROM users WHERE userId= ?");
        ps.setLong(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next())
            this.locale = java.util.Locale.forLanguageTag(rs.getString("language").replace("_", "-"));
    }

    public DatabaseUser(Long userId, Locale locale) throws Exception {
        super(userId);
        PreparedStatement ps = getConnection().prepareStatement("INSERT INTO users (userId, language) VALUES (?,?)");
        ps.setLong(1, entityId);
        ps.setString(2, locale.toLanguageTag().replace("-", "_"));
        ps.execute();
        this.locale = locale;
    }

    @Override
    public void updateInDatabase() throws Exception {
        PreparedStatement ps = getConnection().prepareStatement("UPDATE users SET language = ?");
        ps.setString(1, locale.toLanguageTag().replace("-", "_"));
        ps.setLong(1, entityId);
        ps.execute();
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        PterodactylBot.getInstance().getUserCache().update(this);
    }
}
