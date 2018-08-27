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

package me.schlaubi.pterodactylbot.commands.settings;

import me.schlaubi.commandcord.command.CommandType;
import me.schlaubi.commandcord.command.event.impl.JDACommandEvent;
import me.schlaubi.commandcord.command.permission.Permissions;
import me.schlaubi.commandcord.command.result.Result;
import me.schlaubi.pterodactylbot.PterodactylBot;
import me.schlaubi.pterodactylbot.command.Command;
import me.schlaubi.pterodactylbot.util.EmbedUtil;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        super(new String[] {"prefix"}, CommandType.SETTINGS, Permissions.level(1), "Sets the prefix for your guild", "[prefix]");
    }

    @Override
    public Result run(String[] args, JDACommandEvent event) throws Exception {
        if (args.length == 0)
            return send(EmbedUtil.info(translate("prefix.actualprefix.title", event.getAuthor()),String.format(translate("prefix.actualprefix.description",event.getAuthor()), PterodactylBot.getInstance().getInformationProvider().getPrefix(event.getGuild().getId()))));
        String prefix = args[0];
        getGuild(event.getGuild()).setPrefix(prefix);
        return send(EmbedUtil.success(translate("prefix.set.title", event.getAuthor()), String.format(translate("prefix.set.description", event.getAuthor()), prefix)));
    }
}
