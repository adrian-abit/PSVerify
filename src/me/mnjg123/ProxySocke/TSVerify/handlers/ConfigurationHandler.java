/**
 * @author mnjg123
 *
 * @license Licensed to ProxySocke.NET and their Management, unauthorized replication is prohibited!
 *
 * Copyright 27 Mar 2020 mnjg123 ( including mnjg123.de, mnjg123.eu, devdb.eu ), ProxySocke to Present
 * All Rights Reserved.
 */
package me.mnjg123.ProxySocke.TSVerify.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.List;

import me.mnjg123.ProxySocke.TSVerify.cache.MySQLCache;
import me.mnjg123.ProxySocke.TSVerify.cache.SharedCache;
import me.mnjg123.ProxySocke.TSVerify.cache.TeamSpeakCache;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * @author mnjg123
 *
 */

public class ConfigurationHandler {

	private final Plugin plugin;
	private final TeamSpeakCache tsCache;
	private final MySQLCache MySQLCache;
	private final SharedCache sharedCache;

	private File configurationFile;
	private Configuration configuration;

	public ConfigurationHandler(Plugin plugin, TeamSpeakCache tsCache, MySQLCache MySQLCache, SharedCache sharedCache) {

		this.MySQLCache = MySQLCache;
		this.sharedCache = sharedCache;
		this.tsCache = tsCache;
		this.plugin = plugin;

	}
	
	/**
	 * @return {@link SharedCache} the sharedCache
	 */
	public SharedCache getSharedCache() {
		return sharedCache;
	}
	
	/**
	 * @return {@link MySQLCache} the mySQLCache
	 */
	public MySQLCache getMySQLCache() {
		return MySQLCache;
	}
	
	/** 
	 * @return {@link TeamSpeakCache} TeamSpeakCache
	 */
	public TeamSpeakCache getTsCache() {
		return tsCache;
	}
	
	/** 
	 * @return Plugin
	 */
	private Plugin getPlugin() {
		return plugin;
	}

	 /**
	 * Loads the configuration and caches all important data.
	 */
	public void loadConfiguration() {

		configurationFile = new File(getPlugin().getDataFolder(), "config.yml");

		try {
			createFile(configurationFile);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		try {
			
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configurationFile);
			
			getTsCache().setHost(configuration.getString("TeamSpeak.host"));
			getTsCache().setBotname(configuration.getString("TeamSpeak.name"));
			getTsCache().setPassword(configuration.getString("TeamSpeak.password"));
			getTsCache().setPort(configuration.getInt("TeamSpeak.port"));
			getTsCache().setUsername(configuration.getString("TeamSpeak.username"));
			
			getMySQLCache().setHost(configuration.getString("MySQL.host"));
			getMySQLCache().setDatabase(configuration.getString("MySQL.database"));
			getMySQLCache().setPort(configuration.getInt("MySQL.port"));
			getMySQLCache().setUsername(configuration.getString("MySQL.username"));
			getMySQLCache().setPassword(configuration.getString("MySQL.password"));
			
			List<String> ranks = configuration.getStringList("Verify.ranks");
			
			getSharedCache().setCooldownMillis(configuration.getInt("Verify.cooldown"));
			
			for(String curr : ranks) {
				String perm = curr.split(",")[0];
				int id = Integer.parseInt(curr.split(",")[1]);
				
				getSharedCache().addRank(perm, id);
				
			}

			
		

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
			
			BufferedReader br = new BufferedReader(new InputStreamReader(getPlugin().getResourceAsStream("config.yml"), StandardCharsets.UTF_8));

			Writer fw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
			PrintWriter pw = new PrintWriter(fw);

			String line;

			while ((line = br.readLine()) != null) {
				pw.println(line);
			}

			pw.close();

		}

	}


}
