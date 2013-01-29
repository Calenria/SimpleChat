/*
 * Copyright (C) 2012 Calenria <https://github.com/Calenria/> and contributors
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3.0 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package com.github.calenria.simplechat.listener;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.github.calenria.bungeetools.zBungeeTools;
import com.github.calenria.simplechat.Chatter;
import com.github.calenria.simplechat.SimpleChat;

/**
 * Eventlistener Klasse.
 * 
 * @author Calenria
 * 
 */
public class SimpleChatPluginListener implements PluginMessageListener {
    /**
     * Bukkit Logger.
     */
    @SuppressWarnings("unused")
    private static Logger log    = Logger.getLogger("Minecraft");
    /**
     * NextVote Plugin.
     */
    private SimpleChat    plugin = null;

    /**
     * Registriert die Eventhandler und erstellt die Datenbank falls nicht vorhanden.
     * 
     * @param nvPlugin
     *            NextVote Plugin
     */
    public SimpleChatPluginListener(final SimpleChat nvPlugin) {
        this.plugin = nvPlugin;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player sPlayer, byte[] byteMessage) {
        if (!channel.equals("SimpleChat"))
            return;
        String pluginMessage = new String(byteMessage);
        System.out.println(pluginMessage);

        StringTokenizer st = new StringTokenizer(pluginMessage, "@#@");
        String type = st.nextToken();

        if (type.equals("ping")) {
            zBungeeTools.updateCurrOnline();
            return;
        }

        if (type.equals("wp")) {
            String whisperPartner = st.nextToken();
            String chatter = st.nextToken();
            Chatter c = plugin.getChatter(chatter);
            plugin.removeChatter(chatter);
            c.setLastWhisperFrom(whisperPartner);
            plugin.setChatter(c);
            return;
        }

        String serverName = st.nextToken();
        System.out.println("Message From Servername: " + serverName);
        if (st.hasMoreTokens()) {
            String pluginChannel = st.nextToken();
            @SuppressWarnings("unused")
            String sender = st.nextToken();
            String message = st.nextToken();
            sendMessage(pluginChannel, message);
        }
    }

    private void sendMessage(String pluginChannel, String message) {
        if (!pluginChannel.equals("Global")) {
            Bukkit.broadcast(message, "simplechat." + pluginChannel.toLowerCase());
        } else {
            Player[] players = Bukkit.getOnlinePlayers();
            for (Player player : players) {
                if (player.hasPermission("simplechat." + pluginChannel.toLowerCase()) && !player.hasPermission("simplechat.gobal.off")) {
                    player.sendMessage(message);
                }
            }
        }

    }
}
