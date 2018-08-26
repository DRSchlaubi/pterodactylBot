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

package me.schlaubi.pterodactylbot.command;

import me.schlaubi.commandcord.command.handlers.impl.jda.JDASubCommand;
import me.schlaubi.commandcord.command.permission.Permissions;
import me.schlaubi.pterodactylbot.PterodactylBot;
import net.dv8tion.jda.core.entities.User;

public abstract class SubCommand extends JDASubCommand {

    public SubCommand(String[] aliases, Permissions permissions, String description, String usage) {
        super(aliases, permissions, description, usage);
    }

    private String tranlate(String key, User user) {
        return PterodactylBot.getInstance().getTranslationManager().getLocaleByUser(user.getId()).translate(key);
    }

}
