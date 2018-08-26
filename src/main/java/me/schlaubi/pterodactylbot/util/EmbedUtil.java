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

package me.schlaubi.pterodactylbot.util;

import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

public class EmbedUtil {

    public static EmbedBuilder success(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":white_check_mark: " + title).setColor(Color.GREEN);
    }

    public static EmbedBuilder error(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":x: " + title).setColor(Color.RED);
    }

    public static EmbedBuilder info(String title, String description) {
        return new EmbedBuilder().setDescription(description).setTitle(":information_source: " + title).setColor(Color.BLUE);
    }


}
