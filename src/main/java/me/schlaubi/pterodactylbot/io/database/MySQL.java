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

package me.schlaubi.pterodactylbot.io.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import me.schlaubi.pterodactylbot.PterodactylBot;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySQL {

    private final Logger logger = Logger.getLogger(MySQL.class);

    private Connection connection;
    private final List<MySQLDatabase> defaults;

    public MySQL() {
        logger.info("[MYSQL] Trying to establish MySQL connection");
        defaults = new ArrayList<>();
        HikariConfig config = new HikariConfig();
        JSONObject configuration = PterodactylBot.getInstance().getConfiguration().getJSONObject("database");
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s", configuration.getString("host"), configuration.getInt("port"), configuration.getString("name")));
        config.setUsername(configuration.getString("user"));
        config.setPassword(configuration.getString("password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("serverTimezone", "Europe/Berlin");
        config.addDataSourceProperty("useSSL", "false");
        try {
            connection = new HikariDataSource(config).getConnection();
        } catch (SQLException | HikariPool.PoolInitializationException e) {
            logger.error("[MYSQL] Error while connecting to MySQL server", e);
        }
        if (connection != null)
            logger.info("[MYSQL] Successfully connected to the database");
    }

    public void createMySQLDatabases() {
        defaults.forEach(mySQLDatabase -> {
            try {
                connection.prepareStatement(mySQLDatabase.getCreateStatement()).execute();
            } catch (SQLException e) {
                logger.error("[MYSQL] Error while creating default databases", e);

            }
        });

    }

    public void addDefaultDatabase(MySQLDatabase mySQLDatabase) {
        defaults.add(mySQLDatabase);
    }

    public interface MySQLDatabase {

        String getCreateStatement();

    }

    public Connection getConnection() {
        return connection;
    }
}
