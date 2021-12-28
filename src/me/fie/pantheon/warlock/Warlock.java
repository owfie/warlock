package me.fie.pantheon.warlock;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import me.fie.pantheon.Main;
import me.fie.pantheon.arena.GameManager;
import me.fie.pantheon.spells.Fireshot;
import me.fie.pantheon.spells.*;
import net.md_5.bungee.api.ChatColor;

public class Warlock implements Listener {

	// Game Stats
	private Player player;
	private UUID uuid;
	private boolean isAlive;
	private boolean isDisabled;
	private Warlock lastHitter;
	private Warlock assistHitter;
	private int points;
	private int kills;
	private int assists;
	private int roundsWon;
	private int gold;
	private GameManager currentGame;
	private ArmorStand healthbar;
	private BukkitTask onHotFloor;
	private Main plugin = Main.plugin;
	
	// Spell Equipment
	private Spell equippedPotent;
	private Spell equippedHarming;
	private Spell equippedMoving;
	private Spell equippedImpairing;
	private Spell equippedWarding;
	
	// Player Traits
	private double hp;
	private double speed;
	private double lifesteal;
	private double mass;
	private double strength;
	private double lavaDmg;
	private double range;
	
	// Items
	private boolean hat;
	private boolean chest;
	private boolean legs;
	private boolean shoes;
	private boolean off;
	
	public enum SpellType {
		BOOMERANG,BOUNCER,DISABLE,DRAIN,FIREBALL,FIRE_SPRAY,GRAVITY,HOMING,LIGHTNING,LINK,METEOR,ROOT,RUSH,SCOURGE,SHIELD,SPLITTER,SWAP,TELEPORT,THRUST,TIME_SHIFT,WIND_WALK;
	}
	public enum SpellCategory {
		POTENT,HARMFUL,MOVING,IMPAIRING,WARDING;
	}
	
	private HashMap<SpellType,Spell> spells;
	
	public Warlock(Player p) {
		setPlayer(p);
		p.getInventory().clear();
		
		setUuid(p.getUniqueId());
		setPoints(0);
		setKills(0);
		setAssists(0);
		setRoundsWon(0);
		setGold(20);
		setHp(20);
		setSpeed(1);
		setLifesteal(0);
		setRange(1);
		setMass(1);
		setStrength(1);
		setLavaDmg(2);
		setHat(false);
		setChest(false);
		setLegs(false);
		setOff(false);
		spells = new HashMap<SpellType,Spell>();
		Fireshot f = new Fireshot();
		f.buySpell(this);
		addSpell(SpellType.FIREBALL,f);
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	public void buyItem(String type) {
		switch(type) {
		case "hat":
			this.setHat(true);
			break;
		case "chest":
			this.setChest(true);
			break;
		case "shoes":
			this.setShoes(true);
			break;
		case "off":
			this.setOff(true);
			break;
		case "legs":
			this.setLegs(true);
			break;
		default:
			break;
		}
	}
	
	public void buySpell(SpellType st) {
		
		// Handles both the purchasing and leveling up of spells
		// (since they're done with the same action)
		
		if(spells.containsKey(st)) {
			
			// Code to do if Warlock already owns this spell. (i.e. level up action)
			
			if(validatePurchase(getSpell(st))) {
				getSpell(st).levelUpSpell();
			}
		
		} else {
			
			// Code to do if Warlock doesn't own this spell. (i.e. buy spell action)
			
			// TODO bracketise case blocks so Spell variable can be consistent -  { s } local
			
			switch(st) {
			case FIREBALL: 
				Fireshot f = new Fireshot();
				if(validatePurchase(f)) {
					f.buySpell(this);
					addSpell(st, f);
				}
				break;
			case LIGHTNING: 
				Lightning l = new Lightning();
				if(validatePurchase(l)) {
					l.buySpell(this);
					addSpell(st, l);
				}
				break;
			case HOMING: 
				Homing h = new Homing();
				if(validatePurchase(h)) {
					h.buySpell(this);
					addSpell(st, h);
				}
				break;
			case BOOMERANG: 
				Boomerang bm = new Boomerang();
				if(validatePurchase(bm)) {
					bm.buySpell(this);
					addSpell(st, bm);
				}
				break;
			case BOUNCER: 
				Bouncer b = new Bouncer();
				if(validatePurchase(b)) {
					b.buySpell(this);
					addSpell(st, b);
				}
				break;
			case DISABLE: 
				Disable d = new Disable();
				if(validatePurchase(d)) {
					d.buySpell(this);
					addSpell(st, d);
				}
				break;
			case DRAIN: 
				Drain dr = new Drain();
				if(validatePurchase(dr)) {
					dr.buySpell(this);
					addSpell(st, dr);
				}
				break;
			case FIRE_SPRAY: 
				FireSpray s9 = new FireSpray();
				if(validatePurchase(s9)) {
					s9.buySpell(this);
					addSpell(st, s9);
				}
				break;
			case GRAVITY: 
				Gravity s10 = new Gravity();
				if(validatePurchase(s10)) {
					s10.buySpell(this);
					addSpell(st, s10);
				}
				break;
			case LINK: 
				Link s11 = new Link();
				if(validatePurchase(s11)) {
					s11.buySpell(this);
					addSpell(st,s11);
				}
				break;
			case METEOR: 
				Meteor s12 = new Meteor();
				if(validatePurchase(s12)) {
					s12.buySpell(this);
					addSpell(st, s12);
				}
				break;
			case ROOT: 
				Root s13 = new Root();
				if(validatePurchase(s13)) {
					s13.buySpell(this);
					addSpell(st, s13);
				}
				break;
			case RUSH: 
				{
					Rush s = new Rush();
					if(validatePurchase(s)) {
						s.buySpell(this);
						addSpell(st, s);
					}
				}
				break;
			case SCOURGE: 
				{
					Scourge s = new Scourge();
					if(validatePurchase(s)) {
						s.buySpell(this);
						addSpell(st, s);
					}
				}
				break;
			case SHIELD: 
				{
					Shield s = new Shield();
					if(validatePurchase(s)) {
						s.buySpell(this);
						addSpell(st, s);
					}
				}
				break;
			case SPLITTER: 
				{
					Splitter s = new Splitter();
					if(validatePurchase(s)) {
						s.buySpell(this);
						addSpell(st, s);
					}
				}
				break;
			case SWAP: 
				{
					Swap s = new Swap();
					if(validatePurchase(s)) {
						s.buySpell(this);
						addSpell(st, s);
					}
				}
				break;
			case TELEPORT: 
				{
					Teleport s = new Teleport();
					if(validatePurchase(s)) {
						s.buySpell(this);
						addSpell(st, s);
					}
				}
				break;
			case THRUST: 
				{
					Thrust s = new Thrust();
					if(validatePurchase(s)) {
						s.buySpell(this);
						addSpell(st, s);
					}
				}
				break;
			case TIME_SHIFT: 
				{
					TimeShift s = new TimeShift();
					if(validatePurchase(s)) {
						s.buySpell(this);
						addSpell(st, s);
					}
				}
				break;
			case WIND_WALK: 
				{
					WindWalk s = new WindWalk();
					if(validatePurchase(s)) {
						s.buySpell(this);
						addSpell(st, s);
					}
				}
				break;
			default:
				break;
			}
			
			
			
		}
		
		
		
		
		
		
		
		
	}
	
	public boolean validatePurchase(Spell s) {
		if(s.getNextCost() > gold || s.getNextCost() == -1) {
			player.playSound(player.getLocation(), Sound.ITEM_SHIELD_BLOCK, 1, 1);
			return false;
		}
		gold-=s.getNextCost();
		return true;
		
	}
	
	public void resetEquipment() {
		player.getInventory().clear();
		try {
			player.getInventory().setItem(3, equippedPotent.getItem());
		} catch(Exception e) {}
		try {
			player.getInventory().setItem(2, equippedHarming.getItem());
		} catch(Exception e) {}
		try {
			player.getInventory().setItem(4, equippedMoving.getItem());
		} catch(Exception e) {}
		try {
			player.getInventory().setItem(5, equippedImpairing.getItem());
		} catch(Exception e) {}
		try {
			player.getInventory().setItem(6, equippedWarding.getItem());
		} catch(Exception e) {}
		
	}
	
	public void addSpell(SpellType st, Spell s) {
		if(spells.containsKey(st)) {
			return;
		}
		spells.put(st,s);
	}
	
	public HashMap<SpellType,Spell> getSpells() {
		return spells;
	}
	
	public Spell getSpell(SpellType st) {
		if(spells.containsKey(st)) {
			return spells.get(st);
		}
		
		return null;
	}
	
	public void levelupSpell(SpellType st) {
		if(!spells.containsKey(st)) {
			plugin.getServer().getConsoleSender().sendMessage(ChatColor.RED + "Spell " + st + " attempted level up but not found in Warlock.");
			return;
		}
		spells.get(st).levelUpSpell();
	}
	

	public Spell getEquippedSpell(SpellCategory sc) {
		switch(sc) {
		case HARMFUL: return getEquippedHarming();
		case POTENT: return getEquippedPotent();
		case MOVING: return getEquippedMoving();
		case WARDING: return getEquippedWarding();
		case IMPAIRING: return getEquippedImpairing();
		default: return null;
		}
	}
	
	public SpellCategory getSpellCategory(SpellType st) {
		if(st == SpellType.BOOMERANG || st == SpellType.FIREBALL || st == SpellType.HOMING || st == SpellType.LIGHTNING) {
			return SpellCategory.HARMFUL;
		} else if(st == SpellType.METEOR || st == SpellType.SCOURGE || st == SpellType.SPLITTER || st == SpellType.FIRE_SPRAY) {
			return SpellCategory.POTENT;
		} else if(st == SpellType.TELEPORT || st == SpellType.WIND_WALK || st == SpellType.THRUST || st == SpellType.SWAP) {
			return SpellCategory.MOVING;
		} else if(st == SpellType.SHIELD || st == SpellType.RUSH || st == SpellType.TIME_SHIFT || st == SpellType.DRAIN) {
			return SpellCategory.WARDING;
		} else if(st == SpellType.GRAVITY || st == SpellType.LINK || st == SpellType.DISABLE || st == SpellType.ROOT) {
			return SpellCategory.IMPAIRING;
		}
		return null;
	}
	
	public void knockback(Vector d) {
		// Increase knockback for lower mass Warlocks
		double m = 1+(1/mass);
		d.multiply(m);
		
		// Set new player velocity
		Location l = player.getLocation();
		l.setDirection(d);
		player.setVelocity(d);
	}
	
	// Listeners
	
	public void doLavaDamage() {
		if(onHotFloor == null) {
			this.onHotFloor = new BukkitRunnable() {
				public void run() {
					if(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.MAGMA_BLOCK)) {
						player.damage(lavaDmg);
						Entity egg = player.getWorld().spawnEntity(player.getLocation(), EntityType.EGG);
						player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 1, 1);
						player.getWorld().spawnParticle(Particle.LANDING_LAVA, player.getLocation(), 25, 1.0,1.0,1.0);
						player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, player.getLocation(), 5, 1.0,1.0,1.0);
						player.setLastDamageCause(new EntityDamageEvent(egg,DamageCause.MAGIC,lavaDmg));
						egg.remove();
					} else {
						onHotFloor=null;
						this.cancel();
					}
				}
			}.runTaskTimer(plugin,0,20);
		}
	}
	
	
	@EventHandler
	public void onInteract(PlayerInteractEntityEvent e) {
		e.setCancelled(true);
		if(e.getRightClicked() instanceof Villager) {
			Villager v = (Villager) e.getRightClicked();
			if(v.getCustomName().equalsIgnoreCase("Blacksmith")) {
				MerchantGUI m = new MerchantGUI(this);
				m.openEquipmentMenu(this);
			} else if(v.getCustomName().equalsIgnoreCase("Sorceror")) {
				MerchantGUI m = new MerchantGUI(this);
				m.openSpellsMenu(this);
			} else if(v.getCustomName().equalsIgnoreCase("Alchemist")) {
				MerchantGUI m = new MerchantGUI(this);
				m.openMasteriesMenu(this);
			}
		}
	}
	
	// Getters and Setters
	
	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getAssists() {
		return assists;
	}

	public void setAssists(int assists) {
		this.assists = assists;
	}

	public int getRoundsWon() {
		return roundsWon;
	}

	public void setRoundsWon(int roundsWon) {
		this.roundsWon = roundsWon;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public double getHp() {
		return hp;
	}

	public void setHp(double hp) {
		this.hp = hp;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public double getLifesteal() {
		return lifesteal;
	}

	public void setLifesteal(double lifesteal) {
		this.lifesteal = lifesteal;
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public double getStrength() {
		return strength;
	}

	public void setStrength(double strength) {
		this.strength = strength;
	}

	public double getLavaDmg() {
		return lavaDmg;
	}

	public void setLavaDmg(double lavaDmg) {
		this.lavaDmg = lavaDmg;
	}

	public double getRange() {
		return range;
	}

	public void setRange(double range) {
		this.range = range;
	}

	public boolean isHat() {
		return hat;
	}

	public void setHat(boolean hat) {
		this.hat = hat;
	}

	public boolean isChest() {
		return chest;
	}

	public void setChest(boolean chest) {
		this.chest = chest;
	}

	public boolean isLegs() {
		return legs;
	}

	public void setLegs(boolean legs) {
		this.legs = legs;
	}

	public boolean isShoes() {
		return shoes;
	}

	public void setShoes(boolean shoes) {
		this.shoes = shoes;
	}

	public boolean isOff() {
		return off;
	}

	public void setOff(boolean off) {
		this.off = off;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Warlock getLastHitter() {
		return lastHitter;
	}

	public void setLastHitter(Warlock lastHitter) {
		this.lastHitter = lastHitter;
	}

	public Warlock getAssistHitter() {
		return assistHitter;
	}

	public void setAssistHitter(Warlock assistHitter) {
		this.assistHitter = assistHitter;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean isDisabled() {
		return isDisabled;
	}

	public void setDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}

	public GameManager getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(GameManager currentGame) {
		this.currentGame = currentGame;
	}

	public ArmorStand getHealthbar() {
		return healthbar;
	}

	public void setHealthbar(ArmorStand healthbar) {
		this.healthbar = healthbar;
	}

	public BukkitTask getOnHotFloor() {
		return onHotFloor;
	}

	public void setOnHotFloor(BukkitTask onHotFloor) {
		this.onHotFloor = onHotFloor;
	}

	public Spell getEquippedPotent() {
		return equippedPotent;
	}

	public void setEquippedPotent(Spell equippedPotent) {
		this.equippedPotent = equippedPotent;
	}

	public Spell getEquippedHarming() {
		return equippedHarming;
	}

	public void setEquippedHarming(Spell equippedHarming) {
		this.equippedHarming = equippedHarming;
	}

	public Spell getEquippedMoving() {
		return equippedMoving;
	}

	public void setEquippedMoving(Spell equippedMoving) {
		this.equippedMoving = equippedMoving;
	}

	public Spell getEquippedImpairing() {
		return equippedImpairing;
	}

	public void setEquippedImpairing(Spell equippedImpairing) {
		this.equippedImpairing = equippedImpairing;
	}

	public Spell getEquippedWarding() {
		return equippedWarding;
	}

	public void setEquippedWarding(Spell equippedWarding) {
		this.equippedWarding = equippedWarding;
	}
}
