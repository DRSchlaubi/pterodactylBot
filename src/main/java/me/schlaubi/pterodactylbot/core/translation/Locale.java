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

package me.schlaubi.pterodactylbot.core.translation;

import org.apache.log4j.Logger;

import java.util.ResourceBundle;

public class Locale {

    private final Logger logger = Logger.getLogger(Locale.class);

    private final TranslationManager manager;
    private final java.util.Locale locale;
    private final ResourceBundle resourceBundle;
    private final String languageName;

    public Locale(TranslationManager manager, java.util.Locale locale, String languageName) {
        this.manager = manager;
        this.locale = locale;
        this.resourceBundle = ResourceBundle.getBundle(String.format("translation_%s_%s", locale.getLanguage(), locale.getCountry()), locale);
        this.languageName = languageName;
    }

    public java.util.Locale getLocale() {
        return locale;
    }

    public ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    public String getLanguageName() {
        return languageName;
    }

    public String translate(String key) {
        if (resourceBundle.containsKey(key))
            return resourceBundle.getString(key);
        else {
            logger.warn(String.format("[LOCALE] Key %s was not found for language %s", key, locale.getLanguage()));
            return manager.getDefaultLocale().translate(key);
        }
    }
}
