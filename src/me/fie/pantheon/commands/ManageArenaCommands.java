package me.fie.pantheon.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.fie.pantheon.Main;
import me.fie.pantheon.arena.GameManager;
import me.fie.pantheon.warlock.Warlock;
import net.md_5.bungee.api.ChatColor;

public class ManageArenaCommands implements CommandExecutor {
	private Main plugin = Main.plugin;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		
		if(sender instanceof Player) {
			Player player = (Player) sender;

			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("create")) {
					try {
						String map = args[1];
						String name = args[2];
						int maxplayers = Integer.parseInt(args[3]);
						GameManager gm = new GameManager(map, name, maxplayers);
						plugin.arenaHashMap.put(gm.getId(), gm);
						player.sendMessage(ChatColor.GREEN + "Arena " + gm.getName() + " created with ID of " + gm.getId() + ".");
						return true;
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				} else if (args[0].equalsIgnoreCase("list")) {
					if(!plugin.arenaHashMap.values().isEmpty()) {
						for(GameManager i : plugin.arenaHashMap.values()) {
							player.sendMessage(ChatColor.DARK_GRAY + "Arena " + i.getId() + ": " + i.getName() + ". Map: " + i.getMap() + ", Max Players: " + i.getplayersNeeded() + ".");
							return true;
						}
					} else {
						player.sendMessage(ChatColor.DARK_GRAY + "There are no current Arenas!");
						return true;
					}
					
					// OTHER NON - ID COMMANDS GO BEFORE HERE
				} else if(args[0].equalsIgnoreCase("leave")) {
					
					
					// TODO Check if warlock / ingame
					Warlock w = plugin.warlockManager.get(player.getUniqueId());
					GameManager gm = w.getCurrentGame();
					gm.removePlayer(player);
					return true;
				} else if(args[0].equalsIgnoreCase("start")) {
					// TODO Check if warlock / ingame
					Warlock w = plugin.warlockManager.get(player.getUniqueId());
					GameManager gm = w.getCurrentGame();
					gm.lobbyCountdown();
					return true;
				} else if(args[0].equalsIgnoreCase("stop")) {
					// TODO Check if warlock / ingame
					Warlock w = plugin.warlockManager.get(player.getUniqueId());
					GameManager gm = w.getCurrentGame();
					gm.gameStop();
					return true;
				} else {
					// ID-BASED COMMANDS
					if(args.length > 1) {
						try {
							int id = Integer.parseInt(args[0]);
							if(!plugin.arenaHashMap.containsKey(id)) {
								player.sendMessage(ChatColor.RED + "Arena not found. Perhaps it doesn't exist? Try /warlock list.");
								return true;
							}
							GameManager gm = plugin.arenaHashMap.get(id);
							String n = gm.getName();
							if(args[1].equalsIgnoreCase("delete")) {
								gm.gameStop();
								plugin.arenaHashMap.remove(id);
								player.sendMessage(ChatColor.GREEN + "Arena " + n + " deleted.");
								return true;
							} else if(args[1].equalsIgnoreCase("join")) {
								player.sendMessage(ChatColor.DARK_GRAY + "Joining Arena " + n + "....");
								plugin.arenaHashMap.get(id).addPlayer(player);
								return true;
							} else if(args[1].equalsIgnoreCase("set")) {
								if(args[2].equalsIgnoreCase("lobby")) {
									gm.setLobbySpawn(player.getLocation());
									player.sendMessage(ChatColor.GREEN + "Lobby point set.");
									return true;
								} else if(args[2].equalsIgnoreCase("game")) {
									player.sendMessage(ChatColor.DARK_GRAY + "Setting game spawn point...");
									gm.addGameSpawn(player.getLocation());
									return true;
								} else if(args[2].equalsIgnoreCase("shop")) {
									gm.setShopSpawn(player.getLocation());
									player.sendMessage(ChatColor.GREEN + "Shop point set.");
									return true;
								}
							}
						} catch(Exception e) {
							e.printStackTrace();
							player.sendMessage(ChatColor.RED + "Command not recognised: Usage: /warlock <id> <delete/join/set>.");
						}
					}
				}
			}
		}
		return false;
	}
}
