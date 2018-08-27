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

package me.schlaubi.pterodactylbot.core.caching;

import me.schlaubi.pterodactylbot.core.entity.DatabaseEntity;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class Cache<T extends DatabaseEntity> {

    private final Logger logger = Logger.getLogger(Cache.class);

    private Map<Long, T> cacheMap = new HashMap<>();
    private Class<T> clazz;

    public Cache(Class<T> clazz) {
        this.clazz = clazz;
    }

    public T getEntity(Long entityId) {
        T out = null;
        if (cacheMap.containsKey(entityId))
            out = cacheMap.get(entityId);
        else {
            try {
                out = clazz.getDeclaredConstructor(Long.class).newInstance(entityId);
            } catch (Exception e) {
                logger.error("[CACHE] An error occurred while getting entity from cache", e);
            }
        }
        return out;
    }

    public void update(T instance) {
        cacheMap.replace(instance.entityId, instance);
        try {
            instance.updateInDatabase();
        } catch (Exception e) {
            logger.error("[CACHE] An error occurred while updating entity in cache", e);
        }
    }

    public void add(Long entityId, T instance) {
        cacheMap.put(entityId, instance);
    }
}
