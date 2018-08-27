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

package me.schlaubi.pterodactylbot.listener;

import me.schlaubi.commandcord.command.event.impl.JDACommandEvent;
import me.schlaubi.commandcord.command.event.impl.JavacordCommandEvent;
import me.schlaubi.commandcord.event.EventAdapter;
import me.schlaubi.commandcord.event.events.CommandExecutedEvent;
import me.schlaubi.commandcord.event.events.CommandFailedEvent;
import me.schlaubi.commandcord.event.events.NoPermissionEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import org.apache.log4j.Logger;

import java.awt.*;

public class CommandListener extends EventAdapter {

    private final Logger logger = Logger.getLogger(CommandListener.class);

    @Override
    public void onPermissionViolation(NoPermissionEvent event) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Error! No Permisssions!")
                .setDescription(":no_entry_sign: You are not allowed to execute this command!")
                .setColor(new Color(219, 18, 0));
        JDACommandEvent commandEvent = ((JDACommandEvent) event.getCommandEvent());
        if (commandEvent.getGuild().getSelfMember().hasPermission(commandEvent.getTextChannel(), Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS))
            ((JDACommandEvent) event.getCommandEvent()).getTextChannel().sendMessage(builder.build()).queue();
    }

    @Override
    public void onCommandFail(CommandFailedEvent event) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Error! An internal error occured!")
                .setDescription(String.format(":no_entry_sign: We're sorry, but an internal error occured\n```%s```", event.getThrowable().getClass().getCanonicalName() + ": " + event.getThrowable().getMessage()))
                .setColor(new Color(219, 18, 0));
        JDACommandEvent commandEvent = ((JDACommandEvent) event.getCommandEvent());
        if (commandEvent.getGuild().getSelfMember().hasPermission(commandEvent.getTextChannel(), Permission.MESSAGE_WRITE, Permission.MESSAGE_EMBED_LINKS))
            ((JDACommandEvent) event.getCommandEvent()).getTextChannel().sendMessage(builder.build()).queue();
        logger.error("[COMMAND] An unkown error occured while parsing command", event.getThrowable());
    }

    @Override
    public void onCommandExecution(CommandExecutedEvent event) {
        JDACommandEvent commandEvent = ((JDACommandEvent) event.getCommandEvent());
        logger.info(String.format("[COMMAND] %s » %s#%s in #%s | %s (%s)", event.getCommand().getAliases()[0], commandEvent.getAuthor().getName(), commandEvent.getAuthor().getDiscriminator(), commandEvent.getTextChannel().getName(), commandEvent.getGuild().getName(), commandEvent.getGuild().getId()));
    }
}
