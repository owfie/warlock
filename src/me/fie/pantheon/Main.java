package me.fie.pantheon;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import me.fie.pantheon.commands.DeveloperCommands;
import me.fie.pantheon.commands.ManageArenaCommands;
import me.fie.pantheon.warlock.MerchantGUI;
import me.fie.pantheon.warlock.SpellHandler;
import me.fie.pantheon.warlock.Warlock;
import me.fie.pantheon.arena.GameManager;

public class Main extends JavaPlugin implements Listener {

	public static Main plugin;
	public HashMap<Integer,GameManager> arenaHashMap = new HashMap<>();
	public SpellHandler sh = new SpellHandler();
	public HashMap<UUID,Warlock> warlockManager = new HashMap<UUID,Warlock>();
	
	
	@Override
	public void onEnable() {
		plugin = this;
		this.getConfig().options().copyDefaults();
		this.saveConfig();
		this.getCommand("warlock").setExecutor(new ManageArenaCommands());
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new SpellHandler(), this);
		getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "\n\nPantheon Suite has been enabled.\n\n");
		new DeveloperCommands();
	}
	
	@Override
	public void onDisable() {
		getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "\n\nPantheon Suite has been disabled.\n\n");
		for(GameManager i : arenaHashMap.values()) {
			i.gameStop();
		}
	}
	
}
