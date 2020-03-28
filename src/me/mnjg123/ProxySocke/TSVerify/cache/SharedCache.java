/**
 * @author mnjg123
 *
 * @license Licensed to ProxySocke.NET and their Management, unauthorized replication is prohibited!
 *
 * Copyright 28 Mar 2020 mnjg123 ( including mnjg123.de, mnjg123.eu, devdb.eu ), ProxySocke to Present
 * All Rights Reserved.
 */
package me.mnjg123.ProxySocke.TSVerify.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author mnjg123
 */
public class SharedCache {
	
	private Integer cooldownMillis = 10000;
	
	private Map<String, Integer> permtoid = new TreeMap<String, Integer>(String.CASE_INSENSITIVE_ORDER);
	private Map<Integer, String> idtoperm = new HashMap<Integer, String>();
	private Map<String, Long> cooldown = new HashMap<String, Long>();
	private List<String> perms = new ArrayList<String>();
	private List<Integer> ids = new ArrayList<Integer>();
	
	/**
	 * @param {@link Integer} the cooldownMillis to set
	 */
	public void setCooldownMillis(Integer cooldownMillis) {
		this.cooldownMillis = cooldownMillis;
	}
	
	/**
	 * @return {@link List<Integer>} the ids
	 */
	public List<Integer> getIds() {
		return ids;
	}
	
	/**
	 * 
	 * @param uuid of the Player
	 * @return true if the player has a cooldown
	 *
	 * @return {@link Boolean}
	 */
	public boolean hasCooldown(String uuid) {
		
		if(!cooldown.containsKey(uuid))
			return false;
		
		if(cooldown.get(uuid) > System.currentTimeMillis())
			return true;
		
		if(cooldown.get(uuid) < System.currentTimeMillis()) {
			cooldown.remove(uuid);
			return false;
		}
		
		return false;
		
	}
	

	/**
	 * @param uuid of the Player
	 * 
	 * Adds Cooldown to the Player
	 */
	public void addCooldown(String uuid) {
		cooldown.put(uuid, System.currentTimeMillis()+cooldownMillis);
	}
	
	
	
	/**
	 * @param Permission
	 * @param Teamspeak-Servergroup-ID
	 */
	public void addRank(String perm, Integer id) {
		this.permtoid.put(perm, id);
		this.idtoperm.put(id, perm);
		this.perms.add(perm);
		this.ids.add(id);
	}
	
	/**
	 * @return {@link List<String>} the perms
	 */
	public List<String> getPerms() {
		return perms;
	}
	
	/**
	 * 
	 * @param Permission of the Group
	 * @return Returns if Permission with Rank exists.
	 *
	 * @return {@link Boolean}
	 */
	public boolean existsRank(String perm) {
		return this.perms.contains(perm) ? true : false;
	}
	
	/**
	 * 
	 * @param ID of the Rank
	 * @return true if the Rank exists
	 *
	 * @return {@link Boolean}
	 */
	public boolean existsRank(int id) {
		return idtoperm.containsKey(id) ? true : false;
	}
	
	/**
	 * 
	 * @param Permission for the Rank
	 * @return ID of the Rank
	 *
	 * @return {@link Integer}
	 */
	public int getID(String perm) {
		return existsRank(perm) ? permtoid.get(perm) : -1;
	}
	
	/**
	 * 
	 * @param ID of the Rank
	 * @return Permission for the Rank
	 *
	 * @return {@link String}
	 */
	public String getPerm(int id) {
		return existsRank(id) ? idtoperm.get(id) : null;
	}
	
	

}
