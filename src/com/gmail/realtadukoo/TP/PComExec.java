package com.gmail.realtadukoo.TP;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PComExec implements CommandExecutor {
	public TP plugin;
	
	public PComExec(TP plugin) {
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
						UUID ID = plugin.TCClass.getUUID(playerName);
						Player player = plugin.getServer().getPlayer(ID);
						String rank = args[3].toLowerCase();
						if(!plugin.getConfig().getStringList("worlds").contains(world)){
							sender.sendMessage(ChatColor.RED + "Sorry, this plugin does not have " +
									"control over the " + world + " world.");
							return true;
						}
						if(plugin.getWorld(world).getStringList("ranks").contains(rank)){
							plugin.getUsers(world).set(ID + ".rank", rank);
							plugin.saveUsers(world);
							Perms.reloadPerms(player);
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
					Perms.reloadAllPerms(plugin);
					sender.sendMessage(ChatColor.GREEN + "Reloaded all permissions.");
					return true;
				}
			}
		}
		return false;
	}
}
