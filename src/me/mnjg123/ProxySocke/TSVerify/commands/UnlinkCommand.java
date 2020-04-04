/**
 * @author mnjg123
 *
 * @license Licensed to ProxySocke.NET and their Management, unauthorized replication is prohibited!
 *
 * Copyright 28 Mar 2020 mnjg123 ( including mnjg123.de, mnjg123.eu, devdb.eu ), ProxySocke to Present
 * All Rights Reserved.
 */
package me.mnjg123.ProxySocke.TSVerify.commands;

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
public class UnlinkCommand extends Command implements TabExecutor{

	private final Plugin plugin;
	private final String prefix;
	private final MessageHandler messageHandler;
	private final DatabaseUtils databaseUtils;
	private final TeamSpeakHandler tsHandler;
	private final SharedCache sharedCache;
	
	public UnlinkCommand(MessageHandler messageHandler, DatabaseUtils databaseUtils, Plugin plugin, TeamSpeakHandler tsHandler, SharedCache sharedCache) {
		
		super("unlink");
		
		this.sharedCache = sharedCache;
		this.plugin = plugin;
		this.tsHandler = tsHandler;
		this.databaseUtils = databaseUtils;
		this.messageHandler = messageHandler;
		prefix = messageHandler.getMessage("prefix");
		
	}
	
	
	/**
	 * @return {@link SharedCache} the sharedCache
	 */
	private SharedCache getSharedCache() {
		return sharedCache;
	}
	
	private BaseComponent[] getMessage(String message) {
		return TextComponent.fromLegacyText(prefix + getMessageHandler().getMessage(message));
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
	
	
	/**
	 * @return {@link MessageHandler} the messageHandler
	 */
	private MessageHandler getMessageHandler() {
		return messageHandler;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length == 0) {
			if(!(sender instanceof ProxiedPlayer)) {
				sender.sendMessage(getMessage("consoleerror"));
			} else {
				
				ProxiedPlayer player = (ProxiedPlayer) sender;
				if(getSharedCache().hasCooldown(player.getUniqueId().toString())) {
					player.sendMessage(getMessage("cooldown"));
					return;
				}
				getSharedCache().addCooldown(player.getUniqueId().toString());
				
				getDatabaseUtils().isVerified(player.getUniqueId().toString(), plugin, result ->{
					
					if(result != null) {
						sender.sendMessage(getMessage("unlink"));
						getDatabaseUtils().unlinkPlayerbyUUID(player.getUniqueId().toString(), getPlugin(), getTsHandler());
						sender.sendMessage(getMessage("unlinksuccess"));
					} else {
						sender.sendMessage(getMessage("notverified"));
					}
					
				});
				
				
			}
		} else if (args.length == 1) {
			if(sender.hasPermission("PSVerify.admin")) {
				String name = args[0];
				
				if (getPlugin().getProxy().getPlayer(name) != null){
					ProxiedPlayer player = getPlugin().getProxy().getPlayer(name);
					
					getDatabaseUtils().isVerified(player.getUniqueId().toString(), plugin, result ->{
						
						if(result != null) { 
							player.sendMessage(getMessage("unlink"));
							sender.sendMessage(getMessage("admin-unlink"));
							getDatabaseUtils().unlinkPlayerbyUUID(player.getUniqueId().toString(), getPlugin(), getTsHandler());
							player.sendMessage(getMessage("unlinksuccess"));
							sender.sendMessage(getMessage("admin-unlinksuccess"));
						} else {
							sender.sendMessage(getMessage("admin-notverified"));
						}
					});
					
				} else {
					sender.sendMessage(getMessage("admin-offline"));
				}
			} else {
				sender.sendMessage(getMessage("usagenoadmin-unlink"));
			}
		} else {
			if(sender.hasPermission("PSVerify.admin")) {
				sender.sendMessage(getMessage("usageadmin-unlink"));
			} else {
				sender.sendMessage(getMessage("usagenoadmin-unlink"));
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
