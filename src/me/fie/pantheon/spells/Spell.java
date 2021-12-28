package me.fie.pantheon.spells;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import me.fie.pantheon.Main;
import me.fie.pantheon.warlock.Warlock;
import me.fie.pantheon.warlock.Warlock.SpellCategory;
import me.fie.pantheon.warlock.Warlock.SpellType;
import net.md_5.bungee.api.ChatColor;

public abstract class Spell implements Listener {
	
	protected String name;
	protected String displayName;
	protected int numberOfLevels;
	protected int currentLevel;
	protected int[] costs;
	protected double baseStrength;
	protected double baseCooldown;
	protected double baseSpeed;
	protected double levelupStrengthMultiplier;
	protected double levelupCooldownMultiplier;
	protected double levelupSpeedMultiplier;
	protected Warlock owner;
	protected ItemStack item;
	protected ItemStack nextItem;
	protected SpellType type;
//	public abstract void shootSpell(PlayerInteractEvent e);
	

	
	public Spell(SpellType type, String name, int numberOfLevels, double baseStrength, double baseCooldown, double baseSpeed, double levelupStrengthMultiplier, double levelupCooldownMultiplier, double levelupSpeedMultiplier) {
		this.type = type;
		this.name=name;
		this.numberOfLevels=numberOfLevels;
		this.baseStrength = baseStrength;
		this.baseCooldown = baseCooldown;
		this.levelupStrengthMultiplier = levelupStrengthMultiplier;
		this.levelupCooldownMultiplier = levelupCooldownMultiplier;
		this.currentLevel = 0; // 0 implies the spell exists but has not yet been purchased. 1 is the first purchasable level.
		this.baseSpeed = baseSpeed;
		this.levelupSpeedMultiplier = levelupSpeedMultiplier;
		this.displayName = name;
	}
	
	public void buySpell(Warlock w) {
		this.owner = w;
		levelUpSpell();
		w.getPlayer().sendMessage(ChatColor.DARK_GRAY + "You now have the " + ChatColor.GOLD + this.name + ChatColor.DARK_GRAY + " spell.");
		if(owner.getEquippedSpell(owner.getSpellCategory(type)) == null) {
			equipSpell();
		}
	}
	
	public void levelUpSpell() {
		if(currentLevel == numberOfLevels) {
			
			return;
		}
		currentLevel++;
		this.setDisplayName(name + " " + toRomanNumerals(currentLevel));
		setNextItem();
//		owner.getPlayer().playSound(owner.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
		playChord();
	}
	
	public void equipSpell() {
		SpellCategory cat = owner.getSpellCategory(type);
		switch(cat) {
		case HARMFUL: 
			owner.setEquippedHarming(this);
			owner.getPlayer().sendMessage(ChatColor.DARK_GRAY + "You equipped " + ChatColor.GOLD + this.getDisplayName() + ChatColor.DARK_GRAY + " as your " + ChatColor.DARK_RED + "Harming Spell.");
			owner.getPlayer().getInventory().setItem(2, this.getItem());
			break;
		case POTENT:
			owner.setEquippedPotent(this);
			owner.getPlayer().sendMessage(ChatColor.DARK_GRAY + "You equipped " + ChatColor.GOLD + this.getDisplayName() + ChatColor.DARK_GRAY + " as your " + ChatColor.GOLD + "Potent Spell.");
			owner.getPlayer().getInventory().setItem(3, this.getItem());
			break;
		case MOVING:
			owner.setEquippedMoving(this);
			owner.getPlayer().sendMessage(ChatColor.DARK_GRAY + "You equipped " + ChatColor.GOLD + this.getDisplayName() + ChatColor.DARK_GRAY + " as your " + ChatColor.WHITE + "Moving Spell.");
			owner.getPlayer().getInventory().setItem(4, this.getItem());
			break;
		case IMPAIRING:
			owner.setEquippedImpairing(this);
			owner.getPlayer().sendMessage(ChatColor.DARK_GRAY + "You equipped " + ChatColor.GOLD + this.getDisplayName() + ChatColor.DARK_GRAY + " as your " + ChatColor.YELLOW + "Impairing Spell.");
			owner.getPlayer().getInventory().setItem(5, this.getItem());
			break;
		case WARDING:
			owner.setEquippedWarding(this);
			owner.getPlayer().sendMessage(ChatColor.DARK_GRAY + "You equipped " + ChatColor.GOLD + this.getDisplayName() + ChatColor.DARK_GRAY + " as your " + ChatColor.GRAY + "Warding Spell.");
			owner.getPlayer().getInventory().setItem(6, this.getItem());
			break;
		}
		owner.getPlayer().playSound(owner.getPlayer().getLocation(),Sound.ITEM_ARMOR_EQUIP_GOLD,1,1);
		
		
	}
	
	public void playChord() {
		switch(currentLevel) {
		case 1:
			owner.getPlayer().playNote(owner.getPlayer().getLocation(), Instrument.CHIME, Note.natural(0, Tone.C));
			owner.getPlayer().playNote(owner.getPlayer().getLocation(), Instrument.CHIME, Note.natural(0, Tone.E));
			owner.getPlayer().playNote(owner.getPlayer().getLocation(), Instrument.CHIME, Note.natural(0, Tone.G));
			break;
		case 2:
			owner.getPlayer().playNote(owner.getPlayer().getLocation(), Instrument.CHIME, Note.natural(0, Tone.D));
			owner.getPlayer().playNote(owner.getPlayer().getLocation(), Instrument.CHIME, Note.natural(0, Tone.F));
			owner.getPlayer().playNote(owner.getPlayer().getLocation(), Instrument.CHIME, Note.natural(1, Tone.A));
			break;
		case 3:
			owner.getPlayer().playNote(owner.getPlayer().getLocation(), Instrument.CHIME, Note.natural(0, Tone.E));
			owner.getPlayer().playNote(owner.getPlayer().getLocation(), Instrument.CHIME, Note.natural(0, Tone.G));
			owner.getPlayer().playNote(owner.getPlayer().getLocation(), Instrument.CHIME, Note.natural(1, Tone.B));
			break;
		case 4:
			owner.getPlayer().playNote(owner.getPlayer().getLocation(), Instrument.CHIME, Note.natural(0, Tone.F));
			owner.getPlayer().playNote(owner.getPlayer().getLocation(), Instrument.CHIME, Note.natural(1, Tone.A));
			owner.getPlayer().playNote(owner.getPlayer().getLocation(), Instrument.CHIME, Note.natural(1, Tone.C));
			break;
		case 5:
			owner.getPlayer().playNote(owner.getPlayer().getLocation(), Instrument.CHIME, Note.natural(0, Tone.G));
			owner.getPlayer().playNote(owner.getPlayer().getLocation(), Instrument.CHIME, Note.natural(1, Tone.B));
			owner.getPlayer().playNote(owner.getPlayer().getLocation(), Instrument.CHIME, Note.natural(1, Tone.D));
			break;
		default:
			
			break;
		}
	}
	
	public int getNextCost() {
		
		if(currentLevel == numberOfLevels) {
			return -1;
		}
		
		return costs[currentLevel];
	}
	
	public void spellStrikesBlock() {
		
	}
	
	public void spellStrikesPlayer() {
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getNumberOfLevels() {
		return numberOfLevels;
	}
	public void setNumberOfLevels(int numberOfLevels) {
		this.numberOfLevels = numberOfLevels;
	}
	public int getCurrentLevel() {
		return currentLevel;
	}
	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}
	public int[] getCosts() {
		return costs;
	}
	public void setCosts(int[] costs) {
		this.costs = costs;
	}
	public double getBaseStrength() {
		return baseStrength;
	}
	public void setBaseStrength(double baseStrength) {
		this.baseStrength = baseStrength;
	}
	public double getBaseCooldown() {
		return baseCooldown;
	}
	public void setBaseCooldown(double baseCooldown) {
		this.baseCooldown = baseCooldown;
	}
	public double getLevelupStrengthMultiplier() {
		return levelupStrengthMultiplier;
	}
	public void setLevelupStrengthMultiplier(double levelupStrengthMultiplier) {
		this.levelupStrengthMultiplier = levelupStrengthMultiplier;
	}
	public double getLevelupCooldownMultiplier() {
		return levelupCooldownMultiplier;
	}
	public void setLevelupCooldownMultiplier(double levelupCooldownMultiplier) {
		this.levelupCooldownMultiplier = levelupCooldownMultiplier;
	}

	public abstract void shootSpell(PlayerInteractEvent e);

	protected ItemStack createGuiItem(final Material material, final String name, final String... lore)
    {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }
    
	protected void enchantItem() {
		item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		ItemMeta m = item.getItemMeta();
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(m);
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public void setDisplayName(String s) {
		this.displayName = s;
	}
	
	public ItemStack getNextItem() {
		return nextItem;
	}
	
	public void setNextItem() {
		if(currentLevel == numberOfLevels) {
			nextItem = item.clone();
			ItemMeta nim = nextItem.getItemMeta();
			nextItem.setAmount(currentLevel);
			nim.setDisplayName(this.name + " " + toRomanNumerals(currentLevel) + " (Max Level reached)");
			nextItem.setItemMeta(nim);
		} else {
			nextItem = item.clone();
			ItemMeta nim = nextItem.getItemMeta();
			nextItem.setAmount(currentLevel+1);
			nim.setDisplayName("Buy " + this.name + " " + toRomanNumerals(currentLevel+1));
			List<String> lore = nim.getLore();
			lore.add("§r§6" + getNextCost() + " gold");
			nim.setLore(lore);
			nextItem.setItemMeta(nim);
		}	
	}
	
	public String toRomanNumerals(int i) {
		switch(i) {
		case 1: return "I";
		case 2: return "II";
		case 3: return "III";
		case 4: return "IV";
		case 5: return "V";
		default: return "";
		}
		
	}
	
}
