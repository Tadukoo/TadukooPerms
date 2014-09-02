package com.gmail.lucario77777777.TP;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;

public class PlayerListener implements Listener {
	private TP plugin;
	private MainCommandExecutor me;
	
	public PlayerListener(TP plugin){
		this.plugin = plugin;
		me = new MainCommandExecutor(plugin);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		me.reloadPerms(player);
	}
	
	@EventHandler
	public void onPlayerChangeWorld(PlayerChangedWorldEvent event){
		Player player = event.getPlayer();
		me.reloadPerms(player);
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event){
		removePlayer(event.getPlayer(), event.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		removePlayer(event.getPlayer(), event.getPlayer().getUniqueId());
	}
	
	public void removePlayer(Player player, UUID ID){
		if(plugin.permissions.containsKey(ID)){
			PermissionAttachment attachment = plugin.permissions.get(ID);
			player.removeAttachment(attachment);
			plugin.permissions.remove(ID);
		}
	}
}
