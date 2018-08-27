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
import me.schlaubi.pterodactylbot.command.Command;
import me.schlaubi.pterodactylbot.command.SubCommand;
import me.schlaubi.pterodactylbot.core.entity.DatabaseGuild;
import me.schlaubi.pterodactylbot.util.EmbedUtil;

public class BlacklistCommand extends Command {

    public BlacklistCommand() {
        super(new String[] {"blacklist"}, CommandType.SETTINGS, Permissions.level(1), "Blacklists a channel from commands", "");
        registerSubCommand(new AddCommand());
        registerSubCommand(new RemoveCommand());
        registerSubCommand(new ListCommand());
    }

    @Override
    public Result run(String[] args, JDACommandEvent event) {
       if (args.length == 0)
           return sendHelp();
       return null;
    }

    private class AddCommand extends SubCommand {

        private AddCommand() {
            super(new String[] {"block", "add"}, Permissions.level(1), "Blacklists a channel from commands", "<#channel>");
        }

        @Override
        public Result run(String[] args, JDACommandEvent event) throws Exception {
            if (event.getMessage().getMentionedChannels().isEmpty())
                return sendHelp();
            DatabaseGuild guild = getGuild(event.getGuild());
            if (guild.isChannelBlacklisted(event.getMessage().getMentionedChannels().get(0).getId()))
                return send(EmbedUtil.error(translate("command.blacklist.add.alreadyblacklisted.title", event.getAuthor()), translate("command.blacklist.add.alreadyblacklisted.description", event.getAuthor())));
            guild.blacklistChannel(event.getMessage().getMentionedChannels().get(0).getId());
            return send(EmbedUtil.success(translate("command.blacklist.add.added.title", event.getAuthor()), translate("command.blacklist.add.added.description", event.getAuthor())));
        }
    }

    private class RemoveCommand extends SubCommand {

        private RemoveCommand() {
            super(new String[] {"remove", "unblock"}, Permissions.level(1), "Unblock a channel from command", "<#channel>");
        }

        @Override
        public Result run(String[] args, JDACommandEvent event) throws Exception {
            if (event.getMessage().getMentionedChannels().isEmpty())
                return sendHelp();
            DatabaseGuild guild = getGuild(event.getGuild());
            if (!guild.isChannelBlacklisted(event.getMessage().getMentionedChannels().get(0).getId()))
                return send(EmbedUtil.error(translate("command.blacklist.add.notblacklisted.title", event.getAuthor()), translate("command.blacklist.add.notblacklisted.description", event.getAuthor())));
            guild.unBlacklistChannel(event.getMessage().getMentionedChannels().get(0).getId());
            return send(EmbedUtil.success(translate("command.blacklist.add.removed.title", event.getAuthor()), translate("command.blacklist.add.removed.description", event.getAuthor())));
        }
    }

    private class ListCommand extends SubCommand {

        private ListCommand() {
            super(new String[] {"list"}, Permissions.level(1), "Lists all blacklisted channels", "");
        }

        @Override
        public Result run(String[] args, JDACommandEvent event) {
            DatabaseGuild guild = getGuild(event.getGuild());
            if (guild.getBlacklistedChannels().isEmpty())
                return send(EmbedUtil.error(translate("command.blacklist.list.nochannels.title", event.getAuthor()), translate("command.blacklist.list.nochannels.description", event.getAuthor())));
            StringBuilder stringBuilder = new StringBuilder();
            guild.getBlacklistedChannels().forEach(channelId -> {
                String name = event.getGuild().getTextChannelById(channelId).getName();
                if (name == null) {
                    try {
                        guild.unBlacklistChannel(channelId);
                    } catch (Exception ignored) { }
                } else
                    stringBuilder.append(name).append(",");
            });
            if (stringBuilder.toString().contains(","))
                stringBuilder.replace(stringBuilder.lastIndexOf(","), stringBuilder.lastIndexOf(",") + 1, "");
            return send(EmbedUtil.success(translate("command.blacklist.list.channels.title", event.getAuthor()), String.format(translate("command.blacklist.list.channels.description", event.getAuthor()), stringBuilder.toString())));
        }
    }

}
