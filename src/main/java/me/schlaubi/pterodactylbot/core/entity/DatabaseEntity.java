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

import java.sql.Connection;

public abstract class DatabaseEntity {

    public Long entityId;

    public DatabaseEntity(Long entityId) throws Exception {
        this.entityId = entityId;
    }

    public abstract void updateInDatabase() throws Exception;

    protected Connection getConnection() {
        return PterodactylBot.getInstance().getMySQL().getConnection();
    }

}
