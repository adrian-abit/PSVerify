/**
 * @author mnjg123
 *
 * @license Licensed to ProxySocke.NET and their Management, unauthorized replication is prohibited!
 *
 * Copyright 28 Mar 2020 mnjg123 ( including mnjg123.de, mnjg123.eu, devdb.eu ), ProxySocke to Present
 * All Rights Reserved.
 */
package me.mnjg123.ProxySocke.TSVerify.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

import me.mnjg123.ProxySocke.TSVerify.cache.MySQLCache;
import me.mnjg123.ProxySocke.TSVerify.cache.SharedCache;
import me.mnjg123.ProxySocke.TSVerify.teamspeak.TeamSpeakHandler;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * @author mnjg123
 *
 */
public class DatabaseUtils {

	private final MySQLCache mySQLCache;
	private final SharedCache sharedCache;
	private Connection connection;

	public DatabaseUtils(MySQLCache mySQLCache, SharedCache sharedCache) {

		this.sharedCache = sharedCache;
		this.mySQLCache = mySQLCache;
		
		
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
		return mySQLCache;
	}

	/**
	 * Opens connection with MySQL database.
	 */
	public void openConnection() throws SQLException, ClassNotFoundException {

		if (connection != null && !connection.isClosed())
			return;

		synchronized (this) {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
					"jdbc:mysql://" + getMySQLCache().getHost() + ":" + getMySQLCache().getPort() + "/"
							+ getMySQLCache().getDatabase(),
					getMySQLCache().getUsername(), getMySQLCache().getPassword());
		}

	}

	/**
	 * 
	 * Creates Table 
	 *
	 */
	public void createTable() {

		PreparedStatement st;

		try {
			st = connection.prepareStatement(
					"CREATE TABLE IF NOT EXISTS `verify` (`id` MEDIUMINT NOT NULL AUTO_INCREMENT, `uuid` VARCHAR(36) NOT NULL, `uid` VARCHAR(28) NOT NULL, `rank` VARCHAR(255) NOT NULL, `iconid` INT NOT NULL, PRIMARY KEY (`id`))");
			st.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * 
	 * @param uuid of the player
	 * @param Instance of the Plugin
	 * @param teamSpeakHandler
	 *
	 */
 	public void unlinkPlayerbyUUID(String uuid, Plugin plugin, TeamSpeakHandler teamSpeakHandler) {
		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
			
			@Override
			public void run() {
				
				PreparedStatement st;
				
				try {
					st = connection.prepareStatement("SELECT * FROM `verify` WHERE `uuid` = ?");
					st.setString(1, uuid);
					
					ResultSet rs = st.executeQuery();
					String uid = rs.getString("uid");
					
					int tsid = getSharedCache().getID(rs.getString("rank"));
					teamSpeakHandler.removeRank(tsid, uid, rs.getInt("iconid"));
					
					PreparedStatement st1;
					
					st1 = connection.prepareStatement("DELETE FROM `verify` WHERE `uuid` = ?");
					st1.setString(1, uuid);
					st.executeUpdate();
					
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
				
			}
		});
	}
 	
 		/**
 		 * 
 		 * @param ip of the Client and Player
 		 * @param id of the ServerGroup
 		 * @param uuid of the Player
 		 * @param rank of the Player
 		 * @param Instance of the Plugin
 		 * @param teamSpeakHandler
 		 *
 		 * Links the TS-Client with the Player
 		 */
 	   	public void linkPlayerbyUUID(String ip, int id, String uuid, String rank, Plugin plugin, TeamSpeakHandler teamSpeakHandler) {
 	 
 		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
			
			@Override
			public void run() {
			
			PreparedStatement st;
			
			String[] tsinfo = teamSpeakHandler.addRankToIP(ip, id, uuid);
			
			try {
				st = connection.prepareStatement("INSERT INTO `verify` (`uuid`, `uid`, `rank`, `iconid`) VALUES (?, ?, ?, ?)");
				st.setString(1, uuid);
				st.setString(2, tsinfo[0]);
				st.setString(3, rank);
				st.setInt(4, Integer.parseInt(tsinfo[1]));
				
				st.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			
			
				
			}
		});
 	}
 	   	
 		/**
 		 * 
 		 * @param ip of the Client and Player
 		 * @param id of the ServerGroup
 		 * @param uuid of the Player
 		 * @param rank of the Player
 		 * @param Instance of the Plugin
 		 * @param teamSpeakHandler
 		 *
 		 * Links the TS-Client with the Player
 		 */
 	   	public void linkPlayerbyUID(String uid, int id, String uuid, String rank, Plugin plugin, TeamSpeakHandler teamSpeakHandler) {
 	 
 		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
			
			@Override
			public void run() {
			
			PreparedStatement st;
			
			String[] tsinfo = teamSpeakHandler.addRankToUID(uid, id, uuid);
			
			try {
				st = connection.prepareStatement("INSERT INTO `verify` (`uuid`, `uid`, `rank`, `iconid`) VALUES (?, ?, ?, ?)");
				st.setString(1, uuid);
				st.setString(2, tsinfo[0]);
				st.setString(3, rank);
				st.setInt(4, Integer.parseInt(tsinfo[1]));
				
				st.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			
			
				
			}
		});
 	}

	/**
	 * 
	 * @param UUID of the Player
	 * @param Instance of Plugin
	 * @param consumer returns true if Database contains the Player
	 */
 	public void isVerified(String uuid, Plugin plugin, Consumer<String> consumer) {

		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {

			@Override
			public void run() {
				PreparedStatement st;
				String exists = null;

				try {
					st = connection.prepareStatement("SELECT * FROM `verify` WHERE `uuid` = ?");
					st.setString(1, uuid);
					ResultSet rs = st.executeQuery();
					if (rs.next())
						exists = rs.getString("rank");
				} catch (SQLException e) {
					e.printStackTrace();
				}
				consumer.accept(exists);
			}
		});

	}

	/**
	 * 
	 * @param UID of the TS3 Client
	 * @param Instance of the Plugin
	 * @param consumer returns true if Client is in the Database
	 */
	public void isVerifiedUID(String uid, Plugin plugin, Consumer<String> consumer) {

		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {

			@Override
			public void run() {

				PreparedStatement st;
				String exists = null;

				try {
					st = connection.prepareStatement("SELECT * FROM `verify` WHERE `uid` = ?");
					st.setString(1, uid);
					ResultSet rs = st.executeQuery();

					if (rs.next())
						exists = rs.getString("rank");

					consumer.accept(exists);
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		});

	}

	/**
	 * 
	 * @param UUID of the Player
	 * @param Instance of the Plugin
	 * @param consumer return the uid of the client
	 *
	 * @return void
	 */
	public void getUID(String uuid, Plugin plugin, Consumer<String> consumer) {
		plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
			
			@Override
			public void run() {
			
				PreparedStatement st;
				
				try {
					st = connection.prepareStatement("SELECT uid FROM `verify` WHERE `uuid` = ?");
					st.setString(1, uuid);
					
					ResultSet rs = st.executeQuery();
					consumer.accept(rs.getString("uid"));
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
				
				
			}
		});
		
	}
}
