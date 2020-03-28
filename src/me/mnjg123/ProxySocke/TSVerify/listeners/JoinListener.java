/**
 * @author mnjg123
 *
 * @license Licensed to ProxySocke.NET and their Management, unauthorized replication is prohibited!
 *
 * Copyright 28 Mar 2020 mnjg123 ( including mnjg123.de, mnjg123.eu, devdb.eu ), ProxySocke to Present
 * All Rights Reserved.
 */
package me.mnjg123.ProxySocke.TSVerify.listeners;

import me.mnjg123.ProxySocke.TSVerify.cache.SharedCache;
import me.mnjg123.ProxySocke.TSVerify.handlers.MessageHandler;
import me.mnjg123.ProxySocke.TSVerify.teamspeak.TeamSpeakHandler;
import me.mnjg123.ProxySocke.TSVerify.utils.DatabaseUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.event.EventHandler;

/**
 * @author mnjg123
 *
 */
public class JoinListener implements Listener {

	private final Plugin plugin;
	private final DatabaseUtils databaseUtils;
	private final TeamSpeakHandler tsHandler;
	private final MessageHandler messageHandler;
	private final String prefix;
	private final SharedCache sharedCache;

	public JoinListener(Plugin plugin, DatabaseUtils databaseUtils, TeamSpeakHandler tsHandler,
			MessageHandler messageHandler, SharedCache sharedCache) {

		this.plugin = plugin;
		this.databaseUtils = databaseUtils;
		this.tsHandler = tsHandler;
		this.sharedCache = sharedCache;
		this.messageHandler = messageHandler;
		prefix = messageHandler.getMessage("prefix");
	}

	private BaseComponent[] getMessage(String message) {
		return TextComponent.fromLegacyText(prefix + getMessageHandler().getMessage(message));
	}

	/**
	 * @return {@link MessageHandler} the messageHandler
	 */
	private MessageHandler getMessageHandler() {
		return messageHandler;
	}

	/**
	 * @return {@link SharedCache} the sharedCache
	 */
	private SharedCache getSharedCache() {
		return sharedCache;
	}

	/**
	 * @return {@link TeamSpeakHandler} the tsHandler
	 */
	private TeamSpeakHandler getTsHandler() {
		return tsHandler;
	}

	/**
	 * @return {@link DatabaseUtils} the databaseUtils
	 */
	private DatabaseUtils getDatabaseUtils() {
		return databaseUtils;
	}

	/**
	 * @return {@link Plugin} the plugin
	 */
	private Plugin getPlugin() {
		return plugin;
	}

	@EventHandler
	public void onServerConnect(ServerConnectEvent e) {
		ProxiedPlayer player = e.getPlayer();

		getDatabaseUtils().isVerified(player.getUniqueId().toString(), getPlugin(), result -> {
			if (result != null) {
				if (!player.hasPermission(result)) {
					String ip = ((InitialHandler) player.getPendingConnection()).getHandshake().getHost();
					int online = getTsHandler().isOnlineIP(ip);
					if (online == 1) { 
						player.sendMessage(getMessage("update"));
						getDatabaseUtils().unlinkPlayerbyUUID(player.getUniqueId().toString(), getPlugin(), getTsHandler());

						String permission = "";
						for (String perm : getSharedCache().getPerms()) {
							if (player.hasPermission(perm)) {
								permission = perm;
							}
						}

						int id = getSharedCache().getID(permission);
						getDatabaseUtils().linkPlayerbyUUID(ip, id, player.getUniqueId().toString(), permission, player.getName(), getPlugin(), 
								getTsHandler());
						player.sendMessage(getMessage("updatesuccess"));

					} else {
						player.sendMessage(getMessage("update"));
						getDatabaseUtils().getUID(player.getUniqueId().toString(), getPlugin(), uid ->{
							getDatabaseUtils().unlinkPlayerbyUUID(player.getUniqueId().toString(), getPlugin(), getTsHandler());
							
							String permission = "";
							for (String perm : getSharedCache().getPerms()) {
								if (player.hasPermission(perm)) {
									permission = perm;
								}
							}
							
							int id = getSharedCache().getID(permission);
							getDatabaseUtils().linkPlayerbyUID(uid, id, player.getUniqueId().toString(), permission, getPlugin(), player.getName(),
									getTsHandler());
							player.sendMessage(getMessage("updatesuccess"));
							
						});
					}
				}
			}

		});

	}
}
