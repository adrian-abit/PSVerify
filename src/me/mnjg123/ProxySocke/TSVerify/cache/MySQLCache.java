/**
 * @author mnjg123
 *
 * @license Licensed to ProxySocke.NET and their Management, unauthorized replication is prohibited!
 *
 * Copyright 28 Mar 2020 mnjg123 ( including mnjg123.de, mnjg123.eu, devdb.eu ), ProxySocke to Present
 * All Rights Reserved.
 */
package me.mnjg123.ProxySocke.TSVerify.cache;

/**
 * @author mnjg123
 */
public class MySQLCache {
	
	private String host;
	private Integer port;
	private String database;
	private String username;
	private String password;
	
	/**
	 * @param {@link String} the database to set
	 */
	public void setDatabase(String database) {
		this.database = database;
	}
	
	/**
	 * @param {@link String} the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * @param {@link String} the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * @param {@link Integer} the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}
	
	/**
	 * @param {@link String} the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * @return {@link String} the database
	 */
	public String getDatabase() {
		return database;
	}
	
	/**
	 * @return {@link String} the host
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * @return {@link String} the password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * @return {@link Integer} the port
	 */
	public Integer getPort() {
		return port;
	}
	
	/**
	 * @return {@link String} the username
	 */
	public String getUsername() {
		return username;
	}

}
