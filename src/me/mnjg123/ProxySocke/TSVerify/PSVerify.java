/**
 * @author mnjg123
 *
 * @license Licensed to ProxySocke.NET and their Management, unauthorized replication is prohibited!
 *
 * Copyright 27 Mar 2020 mnjg123 ( including mnjg123.de, mnjg123.eu, devdb.eu ), ProxySocke to Present
 * All Rights Reserved.
 */
package me.mnjg123.ProxySocke.TSVerify;

import java.sql.SQLException;

import me.mnjg123.ProxySocke.TSVerify.cache.MySQLCache;
import me.mnjg123.ProxySocke.TSVerify.cache.SharedCache;
import me.mnjg123.ProxySocke.TSVerify.cache.TeamSpeakCache;
import me.mnjg123.ProxySocke.TSVerify.commands.LinkCommand;
import me.mnjg123.ProxySocke.TSVerify.commands.UnlinkCommand;
import me.mnjg123.ProxySocke.TSVerify.handlers.ConfigurationHandler;
import me.mnjg123.ProxySocke.TSVerify.handlers.MessageHandler;
import me.mnjg123.ProxySocke.TSVerify.listeners.JoinListener;
import me.mnjg123.ProxySocke.TSVerify.teamspeak.TeamSpeakHandler;
import me.mnjg123.ProxySocke.TSVerify.utils.DatabaseUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * 
 * @author mnjg123
 *
 */
public class PSVerify extends Plugin {
	
	String[] asciiart = {" _____ _____ _____         _ ___     ", "|  _  |   __|  |  |___ ___|_|  _|_ _ ", "|   __|__   |  |  | -_|  _| |  _| | |", "|__|  |_____|\\___/|___|_| |_|_| |_  |", "                                |___|" }; 
	
	@Override
	public void onEnable() {
		
		sendAscii();
		
		sendMessage(ChatColor.LIGHT_PURPLE + "initialisation of Caches...");
		MySQLCache mySQLCache = new MySQLCache();
		SharedCache sharedCache = new SharedCache();
		TeamSpeakCache tsCache = new TeamSpeakCache();
		sendMessage(ChatColor.GREEN + "Caches done ✓");
		sendMessage(ChatColor.LIGHT_PURPLE + "reading Config and Message files...");
		ConfigurationHandler configHandler = new ConfigurationHandler(this, tsCache, mySQLCache, sharedCache);
		configHandler.loadConfiguration();
		MessageHandler messageHandler = new MessageHandler(this, tsCache);
		messageHandler.loadConfiguration();
		sendMessage(ChatColor.GREEN + "Configs done ✓");
		sendMessage(ChatColor.LIGHT_PURPLE + "connecting to Database...");
		DatabaseUtils databaseUtils = new DatabaseUtils(mySQLCache, sharedCache);
		try {
			databaseUtils.openConnection();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			sendMessage(ChatColor.RED + "Database fail ✘");
			return;
		}
		sendMessage(ChatColor.LIGHT_PURPLE + "creating Table...");
		databaseUtils.createTable();
		sendMessage(ChatColor.GREEN + "Database done ✓");
		sendMessage(ChatColor.LIGHT_PURPLE + "connecting to TeamSpeak³ Server...");
		TeamSpeakHandler tsHandler = new TeamSpeakHandler(tsCache, messageHandler);
		tsHandler.start();
		sendMessage(ChatColor.GREEN + "TeamSpeak done ✓");
		sendMessage(ChatColor.LIGHT_PURPLE + "registring Commands and Listeners...");
		this.getProxy().getPluginManager().registerCommand(this, new LinkCommand(this, databaseUtils, tsHandler, sharedCache, messageHandler));
		this.getProxy().getPluginManager().registerCommand(this, new UnlinkCommand(messageHandler, databaseUtils, this, tsHandler, sharedCache));
		this.getProxy().getPluginManager().registerListener(this, new JoinListener(this, databaseUtils, tsHandler, messageHandler, sharedCache));
		sendMessage(ChatColor.GREEN + "Bungeecord done ✓");
		
		sendMessage(ChatColor.GREEN + "PSVerify is ready!");
		
	}

	
	

	private void sendMessage(String message) {
		this.getProxy().getConsole().sendMessage(TextComponent.fromLegacyText(message));
	}

	private void sendAscii() {
		for(int i = 0; i < asciiart.length; i++) {
			sendMessage(asciiart[i]);
		}
		sendMessage(ChatColor.AQUA + "made by mnjg123 exclusively for ProxySocke.NET! " + ChatColor.DARK_RED + "redistribution is prohibited!");
		
	}
	
}
