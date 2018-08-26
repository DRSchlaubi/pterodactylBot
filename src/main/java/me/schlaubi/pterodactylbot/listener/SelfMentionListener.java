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

import me.schlaubi.pterodactylbot.PterodactylBot;
import me.schlaubi.pterodactylbot.core.translation.Locale;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SelfMentionListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMessage().getMentionedUsers().contains(event.getGuild().getSelfMember().getUser()) && event.getMessage().getContentDisplay().equals("@" + event.getGuild().getSelfMember().getEffectiveName())) {
            if (!event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_WRITE))
                return;
            Locale locale = PterodactylBot.getInstance().getTranslationManager().getLocaleByUser(event.getAuthor().getId());
            EmbedBuilder builder = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setTitle(locale.translate("selfmentionembed.title"))
                    .setDescription(String.format(locale.translate("selfmentionembed.description"), PterodactylBot.getInstance().getInformationProvider().getPrefix(event.getGuild().getId())))
                    .setFooter(String.format(locale.translate("selfmentionembed.footer"), Calendar.getInstance().get(Calendar.YEAR)), null);
            event.getChannel().sendTyping().queue();
            if (event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE))
                event.getMessage().delete().reason("Got mentioned").queue();
            event.getChannel().sendMessage(builder.build()).queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
        }
    }
}
