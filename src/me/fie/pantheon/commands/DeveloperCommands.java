package me.fie.pantheon.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.graph.ElementOrder.Type;

import me.fie.pantheon.Main;
import me.fie.pantheon.warlock.MerchantGUI;
import me.fie.pantheon.warlock.Warlock;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_15_R1.VillagerType;

public class DeveloperCommands implements CommandExecutor {
	
	private Main plugin = Main.plugin;
	
	public DeveloperCommands() {
		plugin.getCommand("warlockdev").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args ) {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Only players may use this command.");
			return true;
		}
		
		Player p = (Player) sender;
		
		if(args[0].equalsIgnoreCase("cw") || args[0].equalsIgnoreCase("create")) {
			p.sendMessage(ChatColor.DARK_GRAY + "Generating Warlocks from players in this server...");
			for(Player i : Bukkit.getOnlinePlayers()) {
				if(!plugin.warlockManager.containsKey(i.getUniqueId())) {
					Warlock w = new Warlock(i);
					plugin.warlockManager.put(i.getUniqueId(),w);
					p.sendMessage(ChatColor.DARK_GRAY + "Added Warlock " + w.getPlayer().getName() + " to Warlocks.");
				}
			}
			p.sendMessage(ChatColor.DARK_GRAY + "All players are now Warlocks!");
			return true;
		} else if(args[0].equalsIgnoreCase("dw") || args[0].equalsIgnoreCase("delete")) {
			plugin.warlockManager.clear();
			p.sendMessage(ChatColor.DARK_GRAY + "All Warlocks cleared.");
			return true;
		} else if(args[0].equalsIgnoreCase("lw") || args[0].equalsIgnoreCase("list")) {
			p.sendMessage(ChatColor.DARK_GRAY + "All Warlocks:");
			for(Warlock w : plugin.warlockManager.values()) {
				p.sendMessage(ChatColor.DARK_GRAY + w.getPlayer().getName());
			}
			return true;
		} else if(args[0].equalsIgnoreCase("merchant")) {
			Villager v = (Villager) p.getWorld().spawnEntity(p.getLocation(), EntityType.VILLAGER);
			if(args[1].equalsIgnoreCase("blacksmith")) {
				
				v.setProfession(Profession.ARMORER);
				v.setVillagerType(Villager.Type.SNOW);
				v.setCustomNameVisible(true);
				v.setCustomName("Blacksmith");
			} else if(args[1].equalsIgnoreCase("alchemist")) {
				v.setProfession(Profession.CLERIC);
				v.setVillagerType(Villager.Type.SNOW);
				v.setCustomNameVisible(true);
				v.setCustomName("Alchemist");
			} if(args[1].equalsIgnoreCase("sorceror")) {
				v.setProfession(Profession.LIBRARIAN);
				v.setVillagerType(Villager.Type.SNOW);
				v.setCustomNameVisible(true);
				v.setCustomName("Sorceror");
			} 
						
			v.setAI(false);
			v.setInvulnerable(true);
			v.setCanPickupItems(false);
			v.setGravity(false);
			v.setGlowing(true);
			
			return true;
		} else if(args[0].equalsIgnoreCase("uuid")) {
			p.sendMessage("Your UUID is " + p.getUniqueId());
			return true;
		} else if(args[0].equalsIgnoreCase("menu")) {
			if(plugin.warlockManager.containsKey(p.getUniqueId())) {
				Warlock w = plugin.warlockManager.get(p.getUniqueId());
				MerchantGUI m = new MerchantGUI(w);
				
				m.openGeneralMenu(w);
			} else {
				p.sendMessage(ChatColor.RED + "No Warlock associated with player.");
			}
			return true;
		} else if(args[0].equalsIgnoreCase("gold")) {
			
				try {
					int g = Integer.parseInt(args[1]);
					if(plugin.warlockManager.containsKey(p.getUniqueId())) {
						Warlock w = plugin.warlockManager.get(p.getUniqueId());
						w.setGold(w.getGold()+g);
						w.getPlayer().sendMessage(ChatColor.GOLD + "You were given " + g + " gold.");
						return true;
					} else {
						p.sendMessage(ChatColor.RED + "Something went wrong. Are you not a Warlock?");
						return false;
					}
				} catch(Exception e) {
					return false;
				}
		} else if(args[0].equalsIgnoreCase("r") || args[0].equalsIgnoreCase("round")) {
			if(args[1].equalsIgnoreCase("s") || args[1].equalsIgnoreCase("start")) {
				p.sendMessage(ChatColor.DARK_GRAY + "Test round started.");
				
				
				
				
				for(Warlock w : plugin.warlockManager.values()) {
					ItemStack fc = new ItemStack(Material.MAGMA_CREAM);
					fc.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
					ItemMeta meta = fc.getItemMeta();
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					meta.setLore(Arrays.asList(""));
					ArrayList<String> lore = new ArrayList<String>();
					lore.add("A ball of fire!");
					meta.setLore(lore);
					meta.setDisplayName("Shield");
					fc.setItemMeta(meta);
					w.getPlayer().getInventory().addItem(fc);
					
					BossBar bossBar = Bukkit.createBossBar(ChatColor.RED + "Next border shrink:", BarColor.RED, BarStyle.SEGMENTED_20);
					new BukkitRunnable() {
				        int seconds = 20;
				            @Override
				            public void run() {
				               if ((seconds -= 1) == 0) {
				                    this.cancel();
				                    bossBar.removeAll();
				                } else {
				                    bossBar.setProgress(seconds / 20.0);
				                 }
				             }
				        }.runTaskTimer(plugin, 0, 20);
				    
				    bossBar.setVisible(true);
				    bossBar.addPlayer(w.getPlayer());

				}
				return true;
			} else if(args[1].equalsIgnoreCase("s") || args[1].equalsIgnoreCase("start")) {
				p.sendMessage(ChatColor.DARK_GRAY + "Test round closed.");
				return true;
			}
		}
		return false;
	}
}

// TODO
// Add permissions
//
//if(p.hasPermission("warlocks.use")) {
//	
//} else {
//	p.sendMessage("You don't have permission to use this command.");
//}