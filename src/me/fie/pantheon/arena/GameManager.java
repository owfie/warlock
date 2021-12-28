package me.fie.pantheon.arena;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;

import me.fie.pantheon.Main;
import me.fie.pantheon.warlock.Warlock;
import net.md_5.bungee.api.ChatColor;

public class GameManager implements Listener {

	private Main plugin = Main.plugin;
	private final String map;
	private String name;
	private static final AtomicInteger count = new AtomicInteger(0); 
	private final int arenaID;
	private boolean gameStarted;
	private Location lobbySpawn;
	private Location shopSpawn;
	private ArrayList<Location> gameSpawn;
	private HashMap<UUID,Warlock> players;
	private HashMap<UUID,Warlock> playersAlive;
	private BukkitTask shopTimer;
	private BukkitTask roundTimer;
	private BossBar roundBar;
	private BossBar shopBar;
	private World world;
	private int borderSize;
	
	private int playersNeeded;
	private int lobbyCountdown;
	private int gameSpawnsSet;
	
	public int currentRound;
	
	
	public GameManager(String map, String name, int playersNeeded) {
		this.map = map;
		this.name = name;
		this.players = new HashMap<UUID,Warlock>();
		this.playersAlive = new HashMap<UUID,Warlock>();
		this.gameSpawn = new ArrayList<Location>();
		this.gameStarted = false;
		this.arenaID = count.incrementAndGet();
		this.playersNeeded = playersNeeded;
		this.lobbyCountdown = 10;
		this.setGameSpawnsSet(0);
		this.currentRound = 0;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void broadcastInternal(String s) {
		for(Warlock i : players.values()) {
			i.getPlayer().sendMessage(s);
		}
	}
	
	public void pasteSchematic(String filepath) {
		File file = new File(plugin.getDataFolder().getAbsolutePath() + "/Schematics/" + filepath + ".schem");
	    ClipboardFormat format = ClipboardFormats.findByFile(file);
	    try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
	       Clipboard clipboard = reader.read();
	       
	       com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(world);

		    try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld,-1)) {
		        Operation operation = new ClipboardHolder(clipboard)
		                .createPaste(editSession)
		                .to(BlockVector3.at(0,90,0))
		                .ignoreAirBlocks(false)
		                .build();
		        try {
					Operations.complete(operation);
				} catch (WorldEditException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
	  } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
 	public void lobbyWait() {
 		int online = players.size();
		int diff = playersNeeded-online;
		if(diff == 0) {
			broadcastInternal(ChatColor.GOLD + "Lobby is full!");
			lobbyCountdown();
			setGameStarted(true);
		} else if(diff == 1) {
			broadcastInternal(ChatColor.DARK_GRAY + "There are " + online + "/" + playersNeeded + " players in the Lobby. " + diff + " more player needed to start the game.");
		} else {
			broadcastInternal(ChatColor.DARK_GRAY + "There are " + online + "/" + playersNeeded + " players in the Lobby. " + diff + " more players needed to start the game.");
		}
		updateScoreboard();
 	}
 	
 	public void shopTimer() {
 		shopBar = Bukkit.createBossBar(ChatColor.GOLD + "Shop time remaining:", BarColor.YELLOW, BarStyle.SOLID);
		shopTimer = new BukkitRunnable() {
	        int seconds = 20;
	            @Override
	            public void run() {
	               if ((seconds -= 1) == 0) {
	                    
	            	   shopStop();
	            	   
	            	   this.cancel();
	                    
	                } else {
	                    shopBar.setProgress(seconds / 20.0);
	                 }
	             }
	        }.runTaskTimer(plugin, 0, 20);
	    
	    shopBar.setVisible(true);
	    for(Warlock i : players.values()) {
		    shopBar.addPlayer(i.getPlayer());
	    }
 	}
 	
 	public void roundTimer() {
 		roundBar = Bukkit.createBossBar(ChatColor.RED + "Next border shrink:", BarColor.RED, BarStyle.SEGMENTED_20);
        roundBar.setVisible(true);
        for(Warlock i : players.values()) {
		    roundBar.addPlayer(i.getPlayer());
	    }
		roundTimer = new BukkitRunnable() {
        int seconds = 10;
            @Override
            public void run() {
               if ((seconds -= 1) == 0) {
            	   roundBar.setVisible(false);
            	   roundBar.removeAll();
            	   
            	   // SHRINK CODE GO HERE 
            	   switch(borderSize) {
            	   case 20:pasteSchematic("neth_r19");break;
            	   case 19:pasteSchematic("neth_r18");break;
            	   case 18:pasteSchematic("neth_r17");break;
            	   case 17:pasteSchematic("neth_r16");break;
            	   case 16:pasteSchematic("neth_r15");break;
            	   case 15:pasteSchematic("neth_r14");break;
            	   case 14:pasteSchematic("neth_r13");break;
            	   case 13:pasteSchematic("neth_r12");break;
            	   case 12:pasteSchematic("neth_r11");break;
            	   case 11:pasteSchematic("neth_r10");break;
            	   case 10:pasteSchematic("neth_r9");break;
            	   case 9:pasteSchematic("neth_r8");break;
            	   case 8:pasteSchematic("neth_r7");break;
            	   case 7:pasteSchematic("neth_r6");break;
            	   case 6:pasteSchematic("neth_r5");break;
            	   case 5:pasteSchematic("neth_r4");break;
            	   case 4:pasteSchematic("neth_r3");break;
            	   case 3:pasteSchematic("neth_r2");break;
            	   case 2:pasteSchematic("neth_r1");break;
            	   case 1:pasteSchematic("neth_r0");break;
            	   case 0:pasteSchematic("neth");break;
            	   default:Bukkit.getConsoleSender().sendMessage("Something went wrong with the border shrink");break;
            	   }
            	   
            	   this.cancel();
            	   if(borderSize > 0) {
            		   borderSize--;
            		   roundTimer();
            	   }
            	   
                } else {
                	roundBar.setProgress(seconds / 10.0);
                 }
             }
        }.runTaskTimer(plugin, 0, 20);
 	}
	
 	public void roundStart() {
 		int t = 0;
 		for(Warlock i : players.values()) {
 			playersAlive.put(i.getUuid(), i);
 			i.setAlive(true);
 			i.getPlayer().setHealth(20.0);
 			i.getPlayer().setFoodLevel(20);
 			
// 			List<Entity> l = i.getPlayer().getPassengers();
// 			for(Entity x : l) {
// 				Bukkit.broadcastMessage(ChatColor.DARK_RED + "Passenger " + x.getName() + " still mounted.");
// 			}
 			i.getPlayer().teleport(gameSpawn.get(t));
 			t++;
 			i.getPlayer().sendMessage(ChatColor.DARK_GRAY + "Teleporting you to the arena...");
 			i.resetEquipment();
 		}
		createHealthBars();
		borderSize = 20;
		pasteSchematic("neth_r20");
 		roundTimer();
 
 	}
 	
 	public void roundStop() {
 		
 		//Clear invent
 		roundBar.removeAll();
 		roundTimer.cancel();
 		deleteAllHealthBars();
 		if(shopTimer == null) {
 			shopStart();
 		}
 		

 	}
 	
 	public void shopStart() {
 		playersAlive.clear();
 		broadcastInternal(ChatColor.YELLOW + "You have 30 seconds to shop!");
 		for(Warlock i : players.values()) {
 			if(currentRound != 0) {
 				i.getPlayer().sendMessage("You were given 5 gold.");
 	 			i.setGold(i.getGold()+5);
 			}
 			i.setAlive(true);
 			i.getPlayer().teleport(shopSpawn);
 			i.getPlayer().setHealth(20.0);
 			i.getPlayer().setFoodLevel(20);
 			i.resetEquipment();
 			i.getPlayer().sendMessage(ChatColor.GOLD + "You have " + i.getGold() + " gold. Use it to buy Spells, Masteries, or Equipment.");
 		}
 		if(currentRound != 0) broadcastInternal(ChatColor.DARK_GRAY + "You'll get more gold after every round.");
 		createHealthBars();
 		shopTimer();
 	}
 	
 	public void shopStop() {
 		
 		//Clear invent
 		shopBar.removeAll();
 		shopTimer.cancel();
 		shopTimer = null;
 		deleteAllHealthBars();
 		
 		roundStart();

 	}
 	
 	public void gameStart() {
 		gameStarted = true;
 		
		shopStart();
		
 	}
 	
 	public void gameStop() {
 		pasteSchematic("neth_r20");
 		deleteAllHealthBars();
 		if(shopTimer != null) {
 			shopBar.removeAll();
 	 		shopTimer.cancel();
 		}
 		if(roundTimer != null) {
 			roundBar.removeAll();
 	 		roundTimer.cancel();
 		}
 		for(Warlock i : players.values()) {
 			plugin.warlockManager.remove(i.getUuid());
 		}
 		playersAlive.clear();
 		players.clear();
 		broadcastInternal(ChatColor.DARK_GRAY + "Game stopped.");
 		gameStarted = false;
 		//implement bungeecord
 	}	 	
 	public void lobbyCountdown() {

 			new BukkitRunnable() {
 	 			@Override
 	 			public void run() {
 	 				if(lobbyCountdown > 0) {
 	 					Bukkit.getServer().broadcastMessage(ChatColor.DARK_GRAY + "Game starting in " + lobbyCountdown + "...");
 	 					lobbyCountdown -= 1;
 	 				} else {
 	 					gameStart();
 	 					this.cancel();
 	 				}
 	 			}
 	 		}.runTaskTimer(plugin,0,20l);

 	}	
 	public void addPlayer(Player player) {
 		if(gameStarted == true) {
 			player.sendMessage(ChatColor.RED + "This game has already started. Please join another.");
 			return; 
 		} else if(players.size() == playersNeeded) {
 			player.sendMessage(ChatColor.RED + "This game is full. Please join another.");
 			return; 
 		} else if(gameSpawnsSet != playersNeeded || lobbySpawn == null || shopSpawn == null) {
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "Not enough spawn points set.");
			return;
		}
 		
		Warlock w = new Warlock(player);
		w.setCurrentGame(this);
 		players.put(player.getUniqueId(),w);
 		plugin.warlockManager.put(player.getUniqueId(), w);
 		player.teleport(lobbySpawn);
		lobbyWait();
	}
	public void removePlayer(Player player) {
		
		Warlock w = players.get(player.getUniqueId());
		players.remove(player.getUniqueId());
		plugin.warlockManager.remove(player.getUniqueId());
		deleteHealthBar(player);
		if(shopTimer != null) {
 			shopBar.removeAll();
 	 		shopTimer.cancel();
 		}
 		if(roundTimer != null) {
 			roundBar.removeAll();
 	 		roundTimer.cancel();
 		}
		
		w.setCurrentGame(null);
		lobbyWait();
	}
	public void clearPlayers() {
		players.clear();
	}
	public void addGameSpawn(Location l) {
		if(gameSpawnsSet<playersNeeded) {
			gameSpawn.add(l);
			gameSpawnsSet++;
			world=l.getWorld();
		} else {
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "Enough spawn points already set.");
		}
	}
	public void clearGameSpawns() {
		gameSpawn.clear();
	}
	
	public void createHealthBars() {
		for(Warlock i : players.values()) {
			Player p = i.getPlayer();
			Location loc = p.getLocation();
			PotionEffect pot = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 4);
			PotionEffect pott = new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 4);
		
			ArmorStand a = (ArmorStand)p.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
			Slime s = (Slime)p.getWorld().spawnEntity(loc, EntityType.SLIME);
			a.setVisible(false);
			a.setInvulnerable(true);
			a.setCollidable(false);
			s.setCollidable(false);
			s.setSize(-3);
			s.addPotionEffect(pot);
			s.addPotionEffect(pott);
			s.setAI(false);
			s.setInvulnerable(true);
			p.addPassenger(s);
			s.addPassenger(a);
			a.setCustomName(ChatColor.DARK_RED + "❤❤❤❤❤❤❤❤❤❤");
			a.setCustomNameVisible(true);
			i.setHealthbar(a);
		}
	}
	
	public void setHealthBar(Player p, double d) {
		
		if(players.containsKey(p.getUniqueId())) {
			Warlock w = players.get(p.getUniqueId());
			if(w.getHealthbar() == null) {
				return;
			}
			ArmorStand t = w.getHealthbar();
			if(d >= 19.0) {
				t.setCustomName(ChatColor.DARK_RED + "❤❤❤❤❤❤❤❤❤❤");
			} else if(d >= 17.0) {
				t.setCustomName(ChatColor.DARK_RED + "❤❤❤❤❤❤❤❤❤" + ChatColor.DARK_GRAY + "❤");
			} else if(d >= 15.0) {
				t.setCustomName(ChatColor.DARK_RED + "❤❤❤❤❤❤❤❤" + ChatColor.DARK_GRAY + "❤❤");
			} else if(d >= 13.0) {
				t.setCustomName(ChatColor.DARK_RED + "❤❤❤❤❤❤❤" + ChatColor.DARK_GRAY + "❤❤❤");
			} else if(d >= 11.0) {
				t.setCustomName(ChatColor.DARK_RED + "❤❤❤❤❤❤" + ChatColor.DARK_GRAY + "❤❤❤❤");
			} else if(d >= 9.0) {
				t.setCustomName(ChatColor.DARK_RED + "❤❤❤❤❤" + ChatColor.DARK_GRAY + "❤❤❤❤❤");
			} else if(d >= 7.0) {
				t.setCustomName(ChatColor.DARK_RED + "❤❤❤❤" + ChatColor.DARK_GRAY + "❤❤❤❤❤❤");
			} else if(d >= 5.0) {
				t.setCustomName(ChatColor.DARK_RED + "❤❤❤" + ChatColor.DARK_GRAY + "❤❤❤❤❤❤❤");
			} else if(d >= 3.0) {
				t.setCustomName(ChatColor.DARK_RED + "❤❤" + ChatColor.DARK_GRAY + "❤❤❤❤❤❤❤❤");
			} else if(d >= 1.0) {
				t.setCustomName(ChatColor.DARK_RED + "❤" + ChatColor.DARK_GRAY + "❤❤❤❤❤❤❤❤❤");
			} else if(d >= 1.0) {
				t.setCustomName(ChatColor.DARK_GRAY + "❤❤❤❤❤❤❤❤❤❤");
			}
			w.setHealthbar(t);
		}
	}
	
	public void deleteAllHealthBars() {
//		for(Entity e : world.getEntities()) {
//			if(e instanceof ArmorStand || e instanceof Slime) {
//				e.remove();
//			}
//		}
		for(Warlock i : players.values()) {
			deleteHealthBar(i.getPlayer());
		}
	}
	
	public void deleteHealthBar(Player p) {
		List<Entity> l = p.getPassengers();
		for(Entity i : l) {
			
			List<Entity> sl = i.getPassengers();
			for(Entity t : sl) {
				i.removePassenger(t);
				t.remove();
			}
			p.removePassenger(i);
			i.remove();
		}

		if(players.containsKey(p.getUniqueId())) {
			players.get(p.getUniqueId()).setHealthbar(null);
		}

	}
	
	public void updateScoreboard() {

		
		
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = board.registerNewObjective("ServerName", "dummy", "        Round 1/10        ");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        
        Score s1 = obj.getScore(ChatColor.GRAY + " ");
        
        Score s2 = obj.getScore(ChatColor.GRAY + " frostjunkie");
        Score s3 = obj.getScore(ChatColor.GOLD + " Kills:     0");
        Score s4 = obj.getScore(ChatColor.BLUE + " Assists: 0");
        Score s5 = obj.getScore(ChatColor.GRAY + "   ");
        Score s6 = obj.getScore(ChatColor.GRAY + " LONGNAMEEEEEEEEEEE");
        Score s7 = obj.getScore(ChatColor.GOLD + " Kills:     0 ");
        Score s8 = obj.getScore(ChatColor.BLUE + " Assists: 0 ");
        Score s9 = obj.getScore(ChatColor.GRAY + "  ");
        s1.setScore(15);
        s2.setScore(14);
        s3.setScore(13);
        s4.setScore(12);
        s5.setScore(11);
        s6.setScore(10);
        s7.setScore(9);
        s8.setScore(8);
        s9.setScore(7);
        
        Team onlineCounter = board.registerNewTeam("onlineCounter");
        onlineCounter.addEntry(ChatColor.BLACK + "" + ChatColor.WHITE);
        
        for(Warlock i : players.values()) {
        	i.getPlayer().setScoreboard(board);
        }
        
	}
	
	// Event Handling
	@EventHandler
    public void onDamage(EntityDamageEvent e){
		if(e.getEntity() instanceof Player) {
			
			Player p = (Player)e.getEntity();
			if(!players.containsKey(p.getUniqueId())) {
				return;
			}
			
			if(!playersAlive.containsKey(p.getUniqueId())) {
				e.setCancelled(true);
			}
			double d = p.getHealth();
			setHealthBar(p,d);
		}
		
		
//				((Player)e.getEntity()).getUniqueId()
    }
	
	@EventHandler
	public void onRegen(EntityRegainHealthEvent e) {
		if(e.getEntity() instanceof Player) {
			Player p = (Player)e.getEntity();
			if(!players.containsKey(p.getUniqueId())) {
				return;
			}
			
			
			
			double d = p.getHealth();
			setHealthBar(p,d);
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		
		Player p = e.getEntity();
		if(!players.containsKey(p.getUniqueId())) {
			return;
		}

		deleteHealthBar(p);
		playersAlive.remove(p.getUniqueId());
		// If there is still fighting left after the kill.
		if(playersAlive.size() > 1) {
//			e.setDeathMessage(ChatColor.DARK_GRAY + "Warlock " + p.getName() + " was slain. " + playersAlive + " Warlocks remain.");
			broadcastInternal(ChatColor.DARK_GRAY + "Warlock " + p.getName() + " was slain. " + playersAlive + " Warlocks remain.");
			p.spigot().respawn();
			p.teleport(lobbySpawn);
			Warlock w = players.get(p.getUniqueId());
			w.setAlive(false);
		// If there is one player left after the kill.
		} else {
//			e.setDeathMessage(ChatColor.DARK_GRAY + "Warlock " + p.getName() + " was slain.");
			broadcastInternal(ChatColor.DARK_GRAY + "Warlock " + p.getName() + " was slain.");
			p.spigot().respawn();
			p.teleport(lobbySpawn);
			Warlock w = players.get(p.getUniqueId());
			w.setAlive(false);
			for(Warlock i : playersAlive.values()) {
				broadcastInternal(ChatColor.GOLD + i.getPlayer().getName() + " wins this round!");
				deleteHealthBar(i.getPlayer());
				playersAlive.remove(i.getUuid());
			}
			roundStop();
		}
	}
	
	@EventHandler
	public void onHungry(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		if(players.containsKey(e.getPlayer().getUniqueId())) {
			players.remove(e.getPlayer().getUniqueId());
		}
	}
	
	// Getters and Setters
	
	public void setName(String name) {
		this.name = name;
	}	
	public String getName() {
		return name;
	}
	public Location getLobbySpawn() {
		return lobbySpawn;
	}	
	public void setLobbySpawn(Location loc) {
		lobbySpawn = loc;
	}	
	public HashMap<UUID,Warlock> getPlayers() {
		return players;
	}
	public boolean getGameStarted() {
		return gameStarted;
	}
	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}
	public int getId() {
		return arenaID;
	}
	public String getMap() {
		return map;
	}
	public int getplayersNeeded() {
		return playersNeeded;
	}
	public void setplayersNeeded(int playersNeeded) {
		this.playersNeeded = playersNeeded;
	}
	public Location getShopSpawn() {
		return shopSpawn;
	}
	public void setShopSpawn(Location shopSpawn) {
		this.shopSpawn = shopSpawn;
	}
	public int getGameSpawnsSet() {
		return gameSpawnsSet;
	}
	public void setGameSpawnsSet(int gameSpawnsSet) {
		this.gameSpawnsSet = gameSpawnsSet;
	}
	
	public static WorldEditPlugin getWorldEdit() {
		return (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
	}
	
	public HashMap<UUID,Warlock> getPlayersAlive() {
		return playersAlive;
	}

	public void setPlayersAlive(HashMap<UUID,Warlock> playersAlive) {
		this.playersAlive = playersAlive;
	}


	public int getBorderSize() {
		return borderSize;
	}

	public void setBorderSize(int borderSize) {
		this.borderSize = borderSize;
	}
}
