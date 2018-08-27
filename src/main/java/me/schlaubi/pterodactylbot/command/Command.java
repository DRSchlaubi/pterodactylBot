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

import me.schlaubi.commandcord.command.CommandType;
import me.schlaubi.commandcord.command.handlers.impl.jda.JDACommand;
import me.schlaubi.commandcord.command.permission.Permissions;
import me.schlaubi.commandcord.command.result.Result;
import me.schlaubi.commandcord.command.result.impl.JDAResult;
import me.schlaubi.pterodactylbot.PterodactylBot;
import me.schlaubi.pterodactylbot.core.entity.DatabaseGuild;
import me.schlaubi.pterodactylbot.core.entity.DatabaseUser;
import me.schlaubi.pterodactylbot.io.database.MySQL;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;

public abstract class Command extends JDACommand {


    public Command(String[] aliases, CommandType commandType, Permissions permissions, String description, String usage) {
        super(aliases, commandType, permissions, description, usage);
    }

    protected String translate(String key, User user) {
        return PterodactylBot.getInstance().getTranslationManager().getLocaleByUser(user.getId()).translate(key);
    }

    protected Result sendHelp() {
        return new JDAResult(new EmbedBuilder().setColor(Color.CYAN).setTitle("**" + getAliases()[0] + "** - Usage").setDescription(getUsageMessage()));
    }

    protected MySQL getMySQL() {
        return PterodactylBot.getInstance().getMySQL();
    }

    protected DatabaseGuild getGuild(Guild guild) {
        return PterodactylBot.getInstance().getGuildCache().getEntity(guild.getIdLong());
    }

    protected DatabaseUser getUser(User user) {
        return PterodactylBot.getInstance().getUserCache().getEntity(user.getIdLong());
    }

}
