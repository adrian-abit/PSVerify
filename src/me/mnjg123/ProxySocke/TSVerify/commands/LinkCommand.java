/**
 * @author mnjg123
 *
 * @license Licensed to ProxySocke.NET and their Management, unauthorized replication is prohibited!
 *
 * Copyright 28 Mar 2020 mnjg123 ( including mnjg123.de, mnjg123.eu, devdb.eu ), ProxySocke to Present
 * All Rights Reserved.
 */
package me.mnjg123.ProxySocke.TSVerify.commands;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import me.mnjg123.ProxySocke.TSVerify.cache.SharedCache;
import me.mnjg123.ProxySocke.TSVerify.handlers.MessageHandler;
import me.mnjg123.ProxySocke.TSVerify.teamspeak.TeamSpeakHandler;
import me.mnjg123.ProxySocke.TSVerify.utils.DatabaseUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;

/**
 * @author mnjg123
 *
 */
public class LinkCommand extends Command implements TabExecutor{


	private final Plugin plugin;
	private final DatabaseUtils databaseUtils;
	private final TeamSpeakHandler tsHandler;
	private final SharedCache sharedCache;
	private final MessageHandler messageHandler;
	private final String prefix;
	
	public LinkCommand(Plugin plugin, DatabaseUtils databaseUtils, TeamSpeakHandler tsHandler, SharedCache sharedCache, MessageHandler messageHandler) {
		super("link");
		
		this.messageHandler = messageHandler;
		prefix = messageHandler.getMessage("prefix");
		this.sharedCache = sharedCache;
		this.tsHandler = tsHandler;
		this.databaseUtils = databaseUtils;
		this.plugin = plugin;
		
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

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			
			if(sender instanceof ProxiedPlayer) {
				
				ProxiedPlayer player = (ProxiedPlayer) sender;
				if(getSharedCache().hasCooldown(player.getUniqueId().toString())) {
					player.sendMessage(getMessage("cooldown"));
					return;
				}
				getSharedCache().addCooldown(player.getUniqueId().toString());
				getDatabaseUtils().isVerified(player.getUniqueId().toString(), getPlugin(), result ->{
					if(result != null) {
						if(player.hasPermission(result)) {
							player.sendMessage(getMessage("verified"));
						} else {
							InetSocketAddress isa = (InetSocketAddress) player.getSocketAddress();
							String ip = isa.getAddress().getHostAddress();
							int online = getTsHandler().isOnlineIP(ip);
							if(online != 1) {
								player.sendMessage(getMessage("instanzen"));
							} else if (online == 0){
								player.sendMessage(getMessage("offline"));
							} else {
								player.sendMessage(getMessage("update"));
								getDatabaseUtils().unlinkPlayerbyUUID(player.getUniqueId().toString(), plugin, getTsHandler());
								
								String permission = "";
								for(String perm : getSharedCache().getPerms()) {
									if(player.hasPermission(perm)) {
										permission = perm;
									}
								}
								
								int id = getSharedCache().getID(permission);
								getDatabaseUtils().linkPlayerbyUUID(ip, id, player.getUniqueId().toString(), permission, player.getName(), plugin, getTsHandler());
								player.sendMessage(getMessage("updatesuccess"));
								
							}
						}
					} else {
						InetSocketAddress isa = (InetSocketAddress) player.getSocketAddress();
						String ip = isa.getAddress().getHostAddress();
						int online = getTsHandler().isOnlineIP(ip);
						
						if(online == 0) {
							player.sendMessage(getMessage("offline"));
						} else if(online != 1){
							player.sendMessage(getMessage("instanzen"));
						} else {
							
							player.sendMessage(getMessage("verify"));
							
							String permission = "";
							for(String perm : getSharedCache().getPerms()) {
								if(player.hasPermission(perm)) {
									permission = perm;
								}
							}
							
							int id = getSharedCache().getID(permission);
							getDatabaseUtils().linkPlayerbyUUID(ip, id, player.getUniqueId().toString(), permission, player.getName(), plugin, getTsHandler());
							player.sendMessage(getMessage("verifysuccess"));
							
						}
						
					}
				});
				
			} else {
				sender.sendMessage(getMessage("consoleerror"));
			}
			
		} else {
			if(sender.hasPermission("PSVerify.admin")) {
				
				if(args.length == 1) {
					
					String name = args[0];
					
					if(getPlugin().getProxy().getPlayer(name) != null) {
						ProxiedPlayer player = getPlugin().getProxy().getPlayer(name);

						getDatabaseUtils().isVerified(player.getUniqueId().toString(), plugin, result ->{
							
							if(result != null) {
								if(player.hasPermission(result)) {
								sender.sendMessage(getMessage("admin-verified"));
								} else {
									InetSocketAddress isa = (InetSocketAddress) player.getSocketAddress();
									String ip = isa.getAddress().getHostAddress();
									int online = getTsHandler().isOnlineIP(ip);
									if(online != 1) {
										sender.sendMessage(getMessage("admin-instanzen"));
									} else if (online == 0){
										sender.sendMessage(getMessage("admin-offline"));
									} else {
										player.sendMessage(getMessage("update"));
										sender.sendMessage(getMessage("admin-update"));
										getDatabaseUtils().unlinkPlayerbyUUID(player.getUniqueId().toString(), plugin, getTsHandler());
										
										String permission = "";
										for(String perm : getSharedCache().getPerms()) {
											if(player.hasPermission(perm)) {
												permission = perm;
											}
										}
										
										int id = getSharedCache().getID(permission);
										getDatabaseUtils().linkPlayerbyUUID(ip, id, player.getUniqueId().toString(), permission, player.getName(), plugin, getTsHandler());
										player.sendMessage(getMessage("updatesuccess"));
										sender.sendMessage(getMessage("admin-updatesuccess"));
										
									}
								}
							} else {
								InetSocketAddress isa = (InetSocketAddress) player.getSocketAddress();
								String ip = isa.getAddress().getHostAddress();
								int online = getTsHandler().isOnlineIP(ip);
								
								if(online == 0) {
									sender.sendMessage(getMessage("admin-offlinets"));
								} else if (online != 1){
									sender.sendMessage(getMessage("admin-instanzen"));
								} else {
									player.sendMessage(getMessage("verify"));
									sender.sendMessage(getMessage("admin-verify"));
									
									String permission = "";
									for(String perm : getSharedCache().getPerms()) {
										if(player.hasPermission(perm)) {
											permission = perm;
										}
									}
									
									int id = getSharedCache().getID(permission);
									getDatabaseUtils().linkPlayerbyUUID(ip, id, player.getUniqueId().toString(), permission, player.getName(), plugin, getTsHandler());
									player.sendMessage(getMessage("verifysuccess"));
									sender.sendMessage(getMessage("admin-verifysuccess"));
								}
							}
							
						});
						
						
						
						
						
					} else {
						sender.sendMessage(getMessage("admin-offline"));
					}
					
					
					
				} else {
					sender.sendMessage(getMessage("admin-usage"));
				}
				
			} else {
				sender.sendMessage(getMessage("usagenoadmin"));
			}
		}
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		
		ArrayList<String> subcommands = new ArrayList<String>();
		List<String> matches = new ArrayList<>();
		
		if(sender.hasPermission("PSVerify.admin")) {
			if(args.length == 1) {
				for(ProxiedPlayer player : getPlugin().getProxy().getPlayers()) {
					subcommands.add(player.getName());
				}
				
				matches = subcommands.stream().filter(entry -> entry.startsWith(args[0])).collect(Collectors.toList());
			}
		}
		
		return matches;
	}
	
	

}
