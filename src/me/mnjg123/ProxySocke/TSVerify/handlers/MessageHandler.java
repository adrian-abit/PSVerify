/**
 * @author mnjg123
 *
 * @license Licensed to ProxySocke.NET and their Management, unauthorized replication is prohibited!
 *
 * Copyright 28 Mar 2020 mnjg123 ( including mnjg123.de, mnjg123.eu, devdb.eu ), ProxySocke to Present
 * All Rights Reserved.
 */
package me.mnjg123.ProxySocke.TSVerify.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import me.mnjg123.ProxySocke.TSVerify.cache.TeamSpeakCache;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * @author mnjg123
 *
 */
public class MessageHandler {
	
	
	
	private File configurationFile;
	private Configuration configuration;
	
	
	private final TeamSpeakCache tsCache;
	private final Plugin plugin;

	public MessageHandler(Plugin plugin, TeamSpeakCache tsCache) {
		this.plugin = plugin;
		this.tsCache = tsCache;
	}
	
	/**
	 * @return {@link TeamSpeakCache} the tsCache
	 */
	public TeamSpeakCache getTsCache() {
		return tsCache;
	}
	
	/**
	 * @return {@link Plugin} the plugin
	 */
	public Plugin getPlugin() {
		return plugin;
	}
	
	public String getMessage(String message) {
		return ChatColor.translateAlternateColorCodes('&', configuration.getString(message));
	}
	
	 /**
	 * Loads the configuration and caches all important data.
	 */
 	public void loadConfiguration() {

		configurationFile = new File(getPlugin().getDataFolder(), "messages.yml");

		try {
			createFile(configurationFile);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try {
			
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configurationFile);

			getTsCache().setDescription(configuration.getString("ts-description"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param {@link File} 
	 * @throws {@link IOException}
	 *
	 * @return void
	 */
	private void createFile(File file) throws IOException {

		if (!file.exists()) {

			file.getParentFile().mkdirs();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(getPlugin().getResourceAsStream("messages.yml"), "UTF-8"));

			FileWriter fw = new FileWriter(file);
			PrintWriter pw = new PrintWriter(fw);
			

			String line;

			while ((line = br.readLine()) != null) {
				pw.println(line);
			}

			pw.close();

		}

	}

}
