/**
 * @author mnjg123
 *
 * @license Licensed to ProxySocke.NET and their Management, unauthorized replication is prohibited!
 *
 * Copyright 27 Mar 2020 mnjg123 ( including mnjg123.de, mnjg123.eu, devdb.eu ), ProxySocke to Present
 * All Rights Reserved.
 */
package me.mnjg123.ProxySocke.TSVerify.teamspeak;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

import javax.imageio.ImageIO;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.TS3Query.FloodRate;
import com.github.theholywaffle.teamspeak3.api.ClientProperty;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import me.mnjg123.ProxySocke.TSVerify.cache.TeamSpeakCache;
import me.mnjg123.ProxySocke.TSVerify.handlers.MessageHandler;

/**
 * @author mnjg123 
 */
public class TeamSpeakHandler extends Thread {
	
	
	private final TeamSpeakCache tsCache;
	
	private final TS3Config ts3config = new TS3Config();
	private final TS3Query ts3query = new TS3Query(ts3config);
	private final TS3Api ts3api = ts3query.getApi();
	private final TS3ApiAsync ts3asyncapi = ts3query.getAsyncApi();
	
	/**
	 * Starts the Thread and connects with the TeamSpeak-3 Server
	 */
	public TeamSpeakHandler(TeamSpeakCache tsCache, MessageHandler messageHandler) {
		this.tsCache = tsCache;
		
		ts3config.setHost(getTsCache().getHost());
		ts3config.setQueryPort(getTsCache().getPort());
		ts3config.setFloodRate(FloodRate.UNLIMITED);
		ts3config.setReconnectStrategy(ReconnectStrategy.constantBackoff(25));
		ts3query.connect();
		
		ts3api.login(getTsCache().getUsername(), getTsCache().getPassword());
		ts3api.selectVirtualServerByPort(9987);
		ts3api.setNickname(getTsCache().getBotname());
		
		
		
		
		
	}
	
	/**
	 * 
	 * @param ip of the player and client
	 * @param id of the servergroup
	 * @param uuid of the player
	 * @return iconid of the playericon
	 *
	 * @return {@link Arrays}
	 */
	public String[] addRankToIP(String ip, int id, String uuid, String username) {
		Client client = null;
		for(Client clients : ts3api.getClients()) {
			if(clients.getIp().equals(ip)) {
				client = clients;
				break;
			}

		}
		
		ts3asyncapi.addClientToServerGroup(id, client.getDatabaseId());
		int icon_id = getIconAsInteger(uuid);
		
		String desc = getTsCache().getDescription();
		desc = desc.replace("%user%", username);
		desc = desc.replace("%uuid%", uuid);
		ts3asyncapi.editClient(client.getId(), Collections.singletonMap(ClientProperty.CLIENT_DESCRIPTION, desc));
		ts3asyncapi.addClientPermission(client.getDatabaseId(), "i_icon_id", icon_id, false);
		
		ts3asyncapi.sendPrivateMessage(client.getId(), "Du wurdest erfolgreich verifiziert!");
		
		String[] returnStrings = new String[2];
		returnStrings[0] = client.getUniqueIdentifier().toString();
		returnStrings[1] = icon_id+"";
		return returnStrings;
		
	}
	
	/**
	 * 
	 * @param ip of the player and client
	 * @param id of the servergroup
	 * @param uuid of the player
	 * @return iconid of the playericon
	 *
	 * @return {@link Arrays}
	 */
	public String[] addRankToUID(String uid, int id, String uuid, String username) {
		
		ts3asyncapi.addClientToServerGroup(id, ts3api.getDatabaseClientByUId(uid).getDatabaseId());
		String desc = getTsCache().getDescription();
		desc = desc.replace("%user%", username);
		desc = desc.replace("%uuid%", uuid);
		ts3asyncapi.editClient(ts3api.getClientByUId(uid).getId(), Collections.singletonMap(ClientProperty.CLIENT_DESCRIPTION, desc));
		int icon_id = getIconAsInteger(uuid);
		ts3asyncapi.addClientPermission(ts3api.getDatabaseClientByUId(uid).getDatabaseId(), "i_icon_id", icon_id, false);
		
		
		String[] returnStrings = new String[2];
		returnStrings[0] = uid;
		returnStrings[1] = icon_id+"";
		return returnStrings;
		
	}
	
	/**
	 * 
	 * @param UID of the Client
	 * @return true if Client is online
	 *
	 * @return {@link Boolean}
	 */
	public boolean isOnline(String uid) {
		return ts3api.isClientOnline(uid) ? true : false;
	}
	
	/**
	 * 
	 * @param uid of the Client
	 * @return true if the uid exists
	 *
	 * @return {@link Boolean}
	 */
	public boolean existsUID(String uid) {
		if(ts3api.getDatabaseClientByUId(uid) != null)
			return true;
		return false;
	}
	
	/**
	 * 
	 * @param ip of the Player
	 * @return How many instances with the same IP are connected
	 *
	 * @return {@link Integer}
	 */
	public int isOnlineIP(String ip) {
		int clients = 0;
		
		for(Client client : ts3api.getClients()) {
			if(client.getIp().equals(ip))
				clients++;
		}
			
		return clients;
	}
	
	/**
	 * 
	 * @param id of the Servergroup
	 * @param uid of the Client
	 *
	 */
	public void removeRank(int id, String uid, long iconid) {
		ts3asyncapi.removeClientFromServerGroup(id, ts3api.getDatabaseClientByUId(uid).getDatabaseId());
		ts3asyncapi.deleteClientPermission(ts3api.getDatabaseClientByUId(uid).getDatabaseId(), "i_icon_id");
		ts3asyncapi.deleteIcon(iconid);
	}
	
	
	/**
	 * @return {@link TeamSpeakCache} the tsCache
	 */
	public TeamSpeakCache getTsCache() {
		return tsCache;
	}
	
	/**
	 * 
	 * @param uuid of the Player
	 * @return the iconid
	 *
	 * @return {@link Integer}
	 */
	  private Integer getIconAsInteger(String uuid) {
	   Integer iconId = null;
	    try {
	      URL url = new URL("https://minotar.net/helm/" + uuid + "/16.png");
	      InputStream inputStream = url.openStream();
	      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	      byteArrayOutputStream.flush();
	      ImageIO.write(ImageIO.read(inputStream), "PNG", byteArrayOutputStream);
	      iconId = Integer.valueOf(((Long)ts3asyncapi.uploadIconDirect(byteArrayOutputStream.toByteArray()).getUninterruptibly()).intValue());
	      byteArrayOutputStream.close();
	    } catch (Exception ex) {
	      ex.printStackTrace();
	    } 
	    return iconId;
	  }
	
	

}
