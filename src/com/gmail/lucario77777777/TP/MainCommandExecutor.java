package com.gmail.lucario77777777.TP;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class MainCommandExecutor implements CommandExecutor {
	public TP plugin;
	
	public MainCommandExecutor(TP plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if(cmd.getName().equalsIgnoreCase("tperm")){
			if(args.length == 0 && sender.hasPermission("TadukooPerms.use")){
				sender.sendMessage(ChatColor.GREEN + "Type /tperm help for help.");
				return true;
			}else if(args.length >= 1){
				if(args[0].equalsIgnoreCase("setrank") && sender.hasPermission("TadukooPerms.setrank")){
					if(args.length == 4){
						String world = args[1].toLowerCase();
						String playerName = args[2];
						@SuppressWarnings("deprecation")
						Player player = plugin.getServer().getPlayer(playerName);
						UUID ID = player.getUniqueId();
						String rank = args[3].toLowerCase();
						if(!plugin.getConfig().getStringList("worlds").contains(world)){
							sender.sendMessage(ChatColor.RED + "Sorry, this plugin does not have " +
									"control over the" + world + " world.");
							return true;
						}
						if(plugin.getWorld(world).getStringList("ranks").contains(rank)){
							plugin.getUsers(world).set(ID + ".rank", rank);
							plugin.saveUsers(world);
							reloadPerms(player);
							sender.sendMessage(ChatColor.GREEN + playerName + " is now in the " + rank + 
									" rank in the " + world + " world.");
							return true;
						}else{
							sender.sendMessage(ChatColor.RED + rank + " does not exist in " + world + ".");
							return true;
						}
					}else if(args.length < 4){
						sender.sendMessage(ChatColor.RED + "Not enough args!");
						sender.sendMessage(ChatColor.RED + "/tperm setrank <world> <player> <rank>");
						return true;
					}else if(args.length > 4){
						sender.sendMessage(ChatColor.RED + "Too many args!");
						sender.sendMessage(ChatColor.RED + "/tperm setrank <world> <player> <rank>");
						return true;
					}
				}else if(args[0].equalsIgnoreCase("perm") && sender.hasPermission("TadukooPerms.perm")){
					if(args.length < 4){
						sender.sendMessage(ChatColor.RED + "Not enough args!");
						sender.sendMessage(ChatColor.RED + "/tperm perm <rank> <world> <permission> " +
								"[true|false|remove]");
						return true;
					}else if(args.length > 5){
						sender.sendMessage(ChatColor.RED + "Too many args!");
						sender.sendMessage(ChatColor.RED + "/tperm perm <rank> <world> <permission> " +
								"[true|false|remove]");
						return true;
					}else{
						String rank = args[1].toLowerCase();
						String world = args[2].toLowerCase();
						String perm = args[3];
						String value;
						if(!plugin.getConfig().getStringList("worlds").contains(world)){
							sender.sendMessage(ChatColor.RED + "Sorry, this plugin does not have " +
									"control over the" + world + " world.");
							return true;
						}
						List<String> perms = plugin.getRank(world, rank).getStringList("permissions");
						if(args.length == 4){
							if(perms.contains(perm)){
								value = "true";
							}else if(perms.contains("^" + perm)){
								value = "false";
							}else{
								value = "null";
							}
							sender.sendMessage(ChatColor.GREEN + "For the " + rank + " rank in " + world +
									", " + perm + " is set to " + value);
							return true;
						}else{
							value = args[4].toLowerCase();
							String permTest = perm;
							if(perms.contains(permTest)){
								perms.remove(permTest);
							}else if(perms.contains("^" + permTest)){
								perms.remove("^" + permTest);
							}
							if(value.equals("true")){
								perms.add(perm);
							}else if(value.equals("false")){
								perms.add("^" + perm);
							}else if(value.equals("remove")){
								sender.sendMessage(ChatColor.GREEN + "Removed " + perm + " from " + rank + 
										" rank in " + world + ".");
								return true;
							}else{
								sender.sendMessage(ChatColor.RED + "Sorry, you must type true, false, or " +
										"remove.");
								return true;
							}
							plugin.getRank(world, rank).set("permissions", perms);
							plugin.saveRank(world, rank);
							sender.sendMessage(ChatColor.GREEN + perm + " added to " + rank + " in " + 
									world + ".");
							return true;
						}
					}
				}else if(args[0].equalsIgnoreCase("rank") && sender.hasPermission("TadukooPerms.rank")){
					if(args.length < 4){
						sender.sendMessage(ChatColor.RED + "Not enough args!");
						sender.sendMessage(ChatColor.RED + "/tperm rank <create|remove> <rank> <world>");
						return true;
					}else if(args.length > 4){
						sender.sendMessage(ChatColor.RED + "Too many args!");
						sender.sendMessage(ChatColor.RED + "/tperm rank <create|remove> <rank> <world>");
						return true;
					}else{
						String rank = args[2].toLowerCase();
						String world = args[3].toLowerCase();
						if(!plugin.getConfig().getStringList("worlds").contains(world)){
							sender.sendMessage(ChatColor.RED + "Sorry, this plugin does not have " +
									"control over the" + world + " world.");
							return true;
						}
						List<String> ranks = plugin.getWorld(world).getStringList("ranks");
						if(args[1].equalsIgnoreCase("create")){
							if(ranks.contains(rank)){
								sender.sendMessage(ChatColor.RED + rank + " already exists in " + world + ".");
								return true;
							}else{
								ranks.add(rank);
								plugin.getWorld(world).set("ranks", ranks);
								plugin.saveWorld(world);
								sender.sendMessage(ChatColor.GREEN + "Added " + rank + " rank to " + world + 
										".");
								return true;
							}
						}else if(args[1].equalsIgnoreCase("remove")){
							if(ranks.contains(rank)){
								ranks.remove(rank);
								plugin.getWorld(world).set("ranks", ranks);
								plugin.saveWorld(world);
								plugin.getRank(world, rank).set("permissions", null);
								plugin.saveRank(world, rank);
								sender.sendMessage(ChatColor.GREEN + "Removed " + rank + " rank from " + 
										world + ".");
								return true;
							}else{
								sender.sendMessage(ChatColor.RED + world + " does not contain the " + rank
										+ " rank.");
								return true;
							}
						}else{
							sender.sendMessage(ChatColor.RED + "Sorry, you must type either create or " +
									"remove.");
							return true;
						}
					}
				}else if(args[0].equalsIgnoreCase("reload") && sender.hasPermission("TadukooPerms.reload")){
					reloadAllPerms(plugin);
					sender.sendMessage(ChatColor.GREEN + "Reloaded all permissions.");
					return true;
				}
			}
		}
		return false;
	}
	
	public void reloadPerms(Player player){
		String playerRank;
		UUID ID = player.getUniqueId();
		String idName = ID.toString();
		String worldName = player.getWorld().getName().toLowerCase();
		String realWorld = worldName;
		PermissionAttachment attachment = player.addAttachment(plugin);
		if(!plugin.getConfig().getStringList("worlds").contains(worldName)){
			worldName = "default";
		}
		if(plugin.getUsers(worldName).isSet(idName + ".rank")){
			playerRank = plugin.getUsers(worldName).getString(idName + ".rank");
			if(!plugin.getWorld(worldName).getStringList("ranks").contains(playerRank)){
				playerRank = plugin.getWorld(worldName).getString("default-rank");
				plugin.getUsers(worldName).set(idName + ".rank", playerRank);
				plugin.saveUsers(worldName);
			}
		}else{
			playerRank = plugin.getWorld(worldName).getString("default-rank");
			plugin.getUsers(worldName).set(idName + ".rank", playerRank);
			plugin.saveUsers(worldName);
		}
		plugin.getUsers(worldName).set(idName + ".player", player.getName());
		plugin.saveUsers(worldName);
		if(plugin.getConfig().getBoolean("chat-formatting")){
			String prefix = plugin.getRank(worldName, playerRank).getString("prefix");
			if(prefix.contains("{")){
				prefix = prefix.replaceAll("{AQUA}", ChatColor.AQUA.toString());
				prefix = prefix.replaceAll("{BLACK}", ChatColor.BLACK.toString());
				prefix = prefix.replaceAll("{BLUE}", ChatColor.BLUE.toString());
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
				prefix = prefix.replaceAll("{LIGHT_PURPLE}", ChatColor.LIGHT_PURPLE.toString());
				prefix = prefix.replaceAll("{K}", ChatColor.MAGIC.toString());
				prefix = prefix.replaceAll("{RED}", ChatColor.RED.toString());
				prefix = prefix.replaceAll("{R}", ChatColor.RESET.toString());
				prefix = prefix.replaceAll("{STRIKE}", ChatColor.STRIKETHROUGH.toString());
				prefix = prefix.replaceAll("{U}", ChatColor.UNDERLINE.toString());
				prefix = prefix.replaceAll("{WHITE}", ChatColor.WHITE.toString());
				prefix = prefix.replaceAll("{YELLOW}", ChatColor.YELLOW.toString());
			}else if(prefix.contains("&")){
				prefix = prefix.replace('&', ChatColor.COLOR_CHAR);
			}
			player.setDisplayName("[" + realWorld + "] " + prefix + " " + player.getName());
		}
		List<String> ranks = new ArrayList<String>();
		ranks.add(playerRank);
		boolean contGetRanks = true;
		String curRank = playerRank;
		while(contGetRanks){
			if(plugin.getRank(worldName, curRank).isSet("inherits")){
				String prevRank = plugin.getRank(worldName, curRank).getString("inherits");
				ranks.add(prevRank);
				curRank = prevRank;
			}else{
				contGetRanks = false;
			}
		}
		boolean contRanks = true;
		int j = ranks.size() - 1;
		List<String> perms;
		String rank;
		while(contRanks){
			if(j < 0){
				contRanks = false;
				break;
			}
			rank = ranks.get(j);
			perms = plugin.getRank(worldName, rank).getStringList("permissions");
			j--;
			boolean cont = true;
			int i = 0;
			int iL = perms.size();
			String perm;
			boolean setting;
			while(cont){
				if(i == iL){
					cont = false;
					break;
				}
				perm = perms.get(i);
				if(perm.startsWith("^")){
					setting = false;
					perm = perm.substring(1);
				}else{
					setting = true;
				}
				attachment.setPermission(perm, setting);
				i++;
			}
		}
		plugin.permissions.put(ID, attachment);
	}
	
	public void reloadAllPerms(TP plugin){
		Player[] players = plugin.getServer().getOnlinePlayers();
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
}
