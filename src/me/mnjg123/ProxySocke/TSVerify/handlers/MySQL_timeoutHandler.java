/**
 * @author mnjg123
 *
 * Copyright 4 Apr 2020 mnjg123 ( including mnjg123.de, mnjg123.eu, devdb.eu ) to Present
 * All Rights Reserved.
 */
package me.mnjg123.ProxySocke.TSVerify.handlers;

import java.util.concurrent.TimeUnit;

import me.mnjg123.ProxySocke.TSVerify.utils.DatabaseUtils;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * @author mnjg123
 *
 */
public class MySQL_timeoutHandler implements Runnable{

	private final Plugin plugin;
	private final DatabaseUtils dbUtils;
	public MySQL_timeoutHandler(Plugin plugin, DatabaseUtils dbUtils) {
		this.plugin = plugin;
		this.dbUtils = dbUtils;
		plugin.getProxy().getScheduler().runAsync(plugin, this);
	}

	@Override
	public void run() {
		plugin.getProxy().getScheduler().schedule(plugin, new Runnable() {
			
			@Override
			public void run() {
				dbUtils.letAlive(plugin);
			}
		}, 5L, 5L, TimeUnit.MINUTES);
	}
	

	

}
