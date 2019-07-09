package com.gmail.realtadukoo.TP;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class Perms{
	public static TP plugin = TP.plugin;
	
	/*
	 * Gets the permissions for a player and sets them.
	 */
	public static void reloadPerms(Player player){
		String playerRank;
		UUID ID = player.getUniqueId();
		String idName = ID.toString();
		String worldName = player.getWorld().getName().toLowerCase();
		PermissionAttachment attachment = player.addAttachment(plugin);
		
		// If TadukooPerms doesn't have access to the world, use default world.
		if(!plugin.getConfig().getStringList("worlds").contains(worldName)){
			worldName = "default";
		}
		
		// Get the player's rank.
		playerRank = getRank(player);
		
		// Set player's name in users.yml so that when looking at the file, you can tell who the UUID belongs to.
		plugin.getUsers(worldName).set(idName + ".player", player.getName());
		plugin.saveUsers(worldName);
		
		// Make a String List to use for the ranks inheritance.
		List<String> ranks = new ArrayList<String>();
		
		// Add the player's actual rank
		ranks.add(playerRank);
		
		// Keep going through inheritance until the chain ends.
		boolean contGetRanks = true;
		String curRank = playerRank;
		while(contGetRanks){
			if(plugin.getRank(worldName, curRank).isSet("inherits")){
				boolean setRank = true;
				String prevRank = plugin.getRank(worldName, curRank).getString("inherits");
				if(prevRank.contains(":")){
					// : indicates that the rank inherits from another world's rank.
					String[] rankInherit = prevRank.split(":");
					String newWorld = rankInherit[0];
					String newPrevRank = rankInherit[1];
					if(plugin.getConfig().getStringList("worlds").contains(newWorld)){
						// Use for getting next rank in other world.
						// For now simply ends the inheritance chain due to not being implemented.
						prevRank = curRank;
						contGetRanks = false;
					}else{
						// If the other world isn't found, the rank can't exist.
						plugin.getRank(worldName, curRank).set("inherits", null);
						plugin.getLogger().log(Level.WARNING, ChatColor.RED + "Rank " + curRank + " in world " +
								worldName + " had inheritance of " + newPrevRank + " for world " + newWorld + 
								". World " + newWorld + " could not be found.");
						plugin.getLogger().log(Level.WARNING, ChatColor.RED + "Inheritance has been erased to "
								+ "prevent further confusion with Tadukoo Perms");
						setRank = false;
					}
				}
				if(setRank){
					ranks.add(prevRank);
					curRank = prevRank;
				}else{
					contGetRanks = false;
				}
			}else{
				contGetRanks = false;
			}
		}
		
		/*
		 * Set the permissions.
		 */
		boolean contRanks = true;
		int j = ranks.size() - 1;
		List<String> perms;
		String rank;
		while(contRanks){
			
			// Continue until last rank of inheritance (player's actual rank) is reached.
			if(j < 0){
				contRanks = false;
				break;
			}
			
			// Current rank set to next higher rank in inheritance.
			rank = ranks.get(j);
			
			// Get the permissions for the rank.
			perms = plugin.getRank(worldName, rank).getStringList("permissions");
			
			// Change j to move to next rank in next run through.
			j--;
			
			// Move to actually setting permissions.
			boolean cont = true;
			int i = 0;
			int iL = perms.size();
			String perm;
			boolean setting;
			while(cont){
				
				// Continue until last permission is reached.
				if(i == iL){
					cont = false;
					break;
				}
				
				// Get current permission from list.
				perm = perms.get(i);
				if(perm.startsWith("^")){
					// If permission is false, make setting false and remove the ^.
					setting = false;
					perm = perm.substring(1);
				}else{
					// Otherwise make setting true.
					setting = true;
				}
				
				// Set the permission.
				attachment.setPermission(perm, setting);
				
				// Raise i for next permission in next run through.
				i++;
			}
		}
		
		// Add the permissions to the player.
		plugin.permissions.put(ID, attachment);
	}
	
	/*
	 * Reload all the permissions for all players.
	 */
	public static void reloadAllPerms(TP plugin){
		Player[] players = (Player[]) plugin.getServer().getOnlinePlayers().toArray();
		int p = 0;
		int pL = players.length;
		boolean cont = true;
		while(cont){
			if(p == pL){
				cont = false;
				break;
			}
			reloadPerms(players[p]);
			p++;
		}
	}
	
	/*
	 * Returns the player's rank.
	 */
	public static String getRank(Player player){
		String playerRank;
		String worldName = player.getWorld().getName().toLowerCase();
		String idName = player.getUniqueId().toString();
		
		// Check if the player's rank is set in users.yml for that world.
		if(plugin.getUsers(worldName).isSet(idName + ".rank")){
			// Get player rank from users.yml
			playerRank = plugin.getUsers(worldName).getString(idName + ".rank");
			// If the rank doesn't exist, change it to the default rank for that world.
			if(!plugin.getWorld(worldName).getStringList("ranks").contains(playerRank)){
				playerRank = plugin.getWorld(worldName).getString("default-rank");
				plugin.getUsers(worldName).set(idName + ".rank", playerRank);
				plugin.saveUsers(worldName);
			}
		}else{
			// If player's rank is not set in users.yml, set it to default rank.
			playerRank = plugin.getWorld(worldName).getString("default-rank");
			plugin.getUsers(worldName).set(idName + ".rank", playerRank);
			plugin.saveUsers(worldName);
		}
		return playerRank;
	}
}
