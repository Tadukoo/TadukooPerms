package com.gmail.realtadukoo.TP;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class TP extends JavaPlugin{
	public static TP plugin;
	PluginDescriptionFile pdfFile = this.getDescription();
	public static FileConfiguration config;
	public Map<UUID, PermissionAttachment> permissions = new HashMap<>();
	public File usersFile = null;
	public FileConfiguration users = null;
	public String lastUsersWorld = null;
	public File worldFile = null;
	public FileConfiguration world = null;
	public String lastWorld = null;
	public File rankFile = null;
	public FileConfiguration rank = null;
	public String lastRankWorld = null;
	public String lastRank = null;
	
	@Override
	public void onDisable(){
		permissions.clear();
	}
	@Override
	public void onEnable () {
		getCommand("tperm").setExecutor(new PComExec(this));
		this.getServer().getPluginManager().registerEvents(new PPlayerListener(this), this);
		config = getConfig();
		saveDefaultConfig();
		makeDefaultWorldFiles();
	}
	
	public void makeDefaultWorldFiles(){
		if(!config.isSet("worlds")){
			List<String> worlds = Arrays.asList("default");
			config.set("worlds", worlds);
			saveConfig();
			List<String> defaultMembers = Arrays.asList("^TadukooPerms.use", "^TadukooPerms.setrank", 
					"^TadukooPerms.perm", "^TadukooPerms.rank", "^TadukooPerms.reload");
			getRank("default", "default_member").set("permissions", defaultMembers);
			getRank("default", "default_member").set("prefix", "{WHITE}[Member]");
			saveRank("default", "default_member");
			List<String> defaultOps = Arrays.asList("TadukooPerms.use", "TadukooPerms.setrank", 
					"TadukooPerms.perm", "TadukooPerms.rank", "TadukooPerms.reload");
			getRank("default", "default_op").set("permissions", defaultOps);
			getRank("default", "default_op").set("prefix", "{RED}[Staff]");
			getRank("default", "default_op").set("inherits", "default_member");
			saveRank("default", "default_op");
			getWorld("default").set("default-rank", "default_member");
			List<String> ranks = Arrays.asList("default_member", "default_op");
			getWorld("default").set("ranks", ranks);
			saveWorld("default");
		}
	}
	
	/*
	 * Reloads users.yml for the specified world.
	 */
	public void reloadUsers(String worldName){
		usersFile = new File(getDataFolder(), worldName + "/users.yml");
		users = YamlConfiguration.loadConfiguration(usersFile);
		lastUsersWorld = worldName;
	}
	
	/*
	 * Returns the users.yml for the specified world.
	 */
	public FileConfiguration getUsers(String worldName) {
		if(users == null || (lastUsersWorld != null && worldName != lastUsersWorld)){
			reloadUsers(worldName);
		}
		lastUsersWorld = worldName;
	    return users;
	}
	
	public void saveUsers(String worldName) {
		if(users == null || usersFile == null || (lastUsersWorld != null && worldName != lastUsersWorld)){
			return;
		}
	    try {
	        getUsers(worldName).save(usersFile);
	    } catch (IOException ex) {
	        this.getLogger().log(Level.SEVERE, "Could not save config to " + usersFile + ex);
	    }
	    users = null;
	    usersFile = null;
	    lastUsersWorld = null;
	}
	
	public void reloadWorld(String worldName) {
		worldFile = new File(getDataFolder(), worldName + "/config.yml");
		world = YamlConfiguration.loadConfiguration(worldFile);
		lastWorld = worldName;
	}
	
	public FileConfiguration getWorld(String worldName) {
	    if (world == null || (lastWorld != null && worldName != lastWorld)) {
	        reloadWorld(worldName);
	    }
	    lastWorld = worldName;
	    return world;
	}
	
	public void saveWorld(String worldName) {
		if(world == null || worldFile == null || (lastWorld != null && worldName != lastWorld)){
			return;
		}
	    try {
	        getWorld(worldName).save(worldFile);
	    } catch (IOException ex) {
	        this.getLogger().log(Level.SEVERE, "Could not save config to " + worldFile + ex);
	    }
	    world = null;
	    worldFile = null;
	    lastWorld = worldName;
	}
	
	public void reloadRank(String worldName, String rankName){
		rankFile = new File(getDataFolder(), worldName + "/" + rankName + ".yml");
		rank = YamlConfiguration.loadConfiguration(rankFile);
		lastRankWorld = worldName;
		lastRank = rankName;
	}
	
	public FileConfiguration getRank(String worldName, String rankName) {
		if(rank == null || (lastRankWorld != null && worldName != lastRankWorld) ||
				(lastRank != null && rankName != lastRank)){
			reloadRank(worldName, rankName);
		}
		lastRankWorld = worldName;
		lastRank = rankName;
	    return rank;
	}
	
	public void saveRank(String worldName, String rankName) {
		if(rank == null || rankFile == null || (lastRankWorld != null && worldName != lastRankWorld) ||
				(lastRank != null && rankName != lastRank)){
			return;
		}
	    try {
	        getRank(worldName, rankName).save(rankFile);
	    } catch (IOException ex) {
	        this.getLogger().log(Level.SEVERE, "Could not save config to " + rankFile + ex);
	    }
	    rank = null;
	    rankFile = null;
	    lastRankWorld = worldName;
	    lastRank = rankName;
	}
	
	/*
	 * Returns the prefix for the rank of the player.
	 */
	public static String getRankPrefix(Player player){
		String worldName = player.getWorld().getName().toLowerCase();
		String playerRank = Perms.getRank(player);
		String prefix = plugin.getRank(worldName, playerRank).getString("prefix");
		
		if(prefix.contains("{")){
			// Replace {COLOR} with the color/formatting code.
			prefix = prefix.replaceAll("{AQUA}", ChatColor.AQUA.toString());
			prefix = prefix.replaceAll("{BLACK}", ChatColor.BLACK.toString());
			prefix = prefix.replaceAll("{BLUE}", ChatColor.BLUE.toString());
			prefix = prefix.replaceAll("{B}", ChatColor.BOLD.toString());
			prefix = prefix.replaceAll("{BOLD}", ChatColor.BOLD.toString());
			prefix = prefix.replaceAll("{DARK_AQUA}", ChatColor.DARK_AQUA.toString());
			prefix = prefix.replaceAll("{DARK_BLUE}", ChatColor.DARK_BLUE.toString());
			prefix = prefix.replaceAll("{DARK_GRAY}", ChatColor.DARK_GRAY.toString());
			prefix = prefix.replaceAll("{DARK_GREEN}", ChatColor.DARK_GREEN.toString());
			prefix = prefix.replaceAll("{DARK_PURPLE}", ChatColor.DARK_PURPLE.toString());
			prefix = prefix.replaceAll("{DARK_RED}", ChatColor.DARK_RED.toString());
			prefix = prefix.replaceAll("{GOLD}", ChatColor.GOLD.toString());
			prefix = prefix.replaceAll("{GRAY}", ChatColor.GRAY.toString());
			prefix = prefix.replaceAll("{GREEN}", ChatColor.GREEN.toString());
			prefix = prefix.replaceAll("{I}", ChatColor.ITALIC.toString());
			prefix = prefix.replaceAll("{ITALICS}", ChatColor.ITALIC.toString());
			prefix = prefix.replaceAll("{LIGHT_PURPLE}", ChatColor.LIGHT_PURPLE.toString());
			prefix = prefix.replaceAll("{K}", ChatColor.MAGIC.toString());
			prefix = prefix.replaceAll("{MAGIC}", ChatColor.MAGIC.toString());
			prefix = prefix.replaceAll("{RED}", ChatColor.RED.toString());
			prefix = prefix.replaceAll("{R}", ChatColor.RESET.toString());
			prefix = prefix.replaceAll("{RESET}", ChatColor.RESET.toString());
			prefix = prefix.replaceAll("{STRIKE}", ChatColor.STRIKETHROUGH.toString());
			prefix = prefix.replaceAll("{U}", ChatColor.UNDERLINE.toString());
			prefix = prefix.replaceAll("{UNDERLINE}", ChatColor.UNDERLINE.toString());
			prefix = prefix.replaceAll("{WHITE}", ChatColor.WHITE.toString());
			prefix = prefix.replaceAll("{YELLOW}", ChatColor.YELLOW.toString());
		}else if(prefix.contains("&")){
			// Replace & with the section symbol for color codes.
			prefix = prefix.replace('&', ChatColor.COLOR_CHAR);
		}
		return prefix;
	}
}
