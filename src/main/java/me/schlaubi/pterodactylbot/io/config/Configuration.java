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

package me.schlaubi.pterodactylbot.io.config;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

public class Configuration extends JSONObject {

    private final Logger logger = Logger.getLogger(Configuration.class);

    private final File configFile;
    private final Map<String, Object> defaults;

    public Configuration(String configFile) {
        this(new File(configFile));
    }

    private Configuration(File configFile) {
        this.configFile = configFile;
        this.defaults = new HashMap<>();

    }

    public Configuration init() {
        String content = null;
        try {
            if (configFile.exists())
                content = new BufferedReader(new FileReader(configFile)).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            logger.error("[CONFIGLOADER] Error while loading config", e);
        }
        if (content == null || content.equals("")) {
            logger.warn("[CONFIGURATION] No config found creating default config");
            generateDefaultConfig();
        } else {
            JSONObject object = checkObject(new JSONObject(content));
            object.toMap().forEach((key, value) -> put(key, (value instanceof Map ? (new JSONObject(((Map) value))) : (new JSONArray(listToString(((List<?>) value)))))));
        }
        save();
        logger.info("[CONFIGURATION] Initialized config");
        return this;
    }

    private JSONObject checkObject(JSONObject object) {
        defaults.forEach((key, value) -> {
            if (!object.has(key))
                object.put(key, value);
        });
        return object;
    }


    private void save() {
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(configFile))) {
            bufferedWriter.write(toString(2));
        } catch (IOException e) {
            logger.error("[CONFIGLOADER] Error while saving config", e);
        }
    }

    private void generateDefaultConfig() {
        defaults.forEach(this::put);
    }

    public void addDefault(String key, Object object) {
        if (!(object instanceof JSONObject) && !(object instanceof JSONArray))
            throw new IllegalArgumentException("You can only add JSONObjects or JSONArrays as default");
        defaults.put(key, object);
    }

    private String listToString(List<?> list) {
        StringBuilder builder = new StringBuilder()
                .append("[");
        list.forEach(element -> builder.append("\"").append(element.toString()).append("\"").append(","));
        builder.append("]");
        if (builder.toString().contains(","))
            builder.replace(builder.lastIndexOf(","), builder.lastIndexOf(",") + 1, "");
        return builder.toString();
    }

}
