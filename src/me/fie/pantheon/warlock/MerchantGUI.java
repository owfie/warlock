package me.fie.pantheon.warlock;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.fie.pantheon.Main;
import me.fie.pantheon.spells.*;
import me.fie.pantheon.warlock.Warlock.SpellType;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_15_R1.NBTTagByte;
import net.minecraft.server.v1_15_R1.NBTTagCompound;

public class MerchantGUI implements InventoryHolder, Listener {

	private Inventory inv;
	private ItemStack equipment;
	private ItemStack masteries;
	private ItemStack spells;
	private Main plugin = Main.plugin;
	
	public MerchantGUI(Warlock w) {
		
		equipment = createGuiItem(Material.CHAINMAIL_CHESTPLATE, "§r§d§lEquipment");
        
        equipment.addEnchantment(Enchantment.DURABILITY, 1);
        ItemMeta equipMeta = equipment.getItemMeta();
        equipMeta.setLore(null);
        equipMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        equipMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        equipment.setItemMeta(equipMeta);

        masteries = createGuiItem(Material.EXPERIENCE_BOTTLE, "§r§d§lMasteries");
        masteries.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        ItemMeta masteriesMeta = masteries.getItemMeta();
        masteriesMeta.setLore(null);
        masteriesMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS);
        masteries.setItemMeta(masteriesMeta);
        
        spells = createGuiItem(Material.SKULL_BANNER_PATTERN, "§r§d§lSpells");
        spells.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        ItemMeta spellsMeta = spells.getItemMeta();
        spellsMeta.setLore(null);
        spellsMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS,ItemFlag.HIDE_POTION_EFFECTS);
        spells.setItemMeta(spellsMeta);
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

//        openSpellsMenu(w);
	}

	
	public void openGeneralMenu(Warlock w) {
		inv = Bukkit.createInventory(this, 45, "Menu");
		setDefaults(w);
		inv.setItem(20,spells);
		inv.setItem(22,masteries);
        inv.setItem(24,equipment);
        w.getPlayer().openInventory(inv);
	}
	
	public void openSpellsMenu(Warlock w) {
		// Create a new inventory, with "this" owner for comparison with other inventories, a size of nine, called example
		inv = Bukkit.createInventory(this, 45, "Spells");
        setDefaults(w);
        inv.setItem(7,masteries);
        inv.setItem(8,equipment);
        
        ItemStack h = createGuiItem(Material.REDSTONE, "§r§4Harmful Spells", "§r§7Spells that damage one Warlock.");
        
        ItemStack p = createGuiItem(Material.BLAZE_POWDER, "§r§6Potent Spells", "§r§7Spells that damage multiple Warlocks.");
        ItemStack m = createGuiItem(Material.SUGAR, "§r§fMoving Spells", "§r§7Spells that get you somewhere.");
        ItemStack d = createGuiItem(Material.GLOWSTONE_DUST, "§r§eWarding Spells", "§r§7Spells to help you in a fight.");
        ItemStack i = createGuiItem(Material.GUNPOWDER, "§r§8Impairing Spells", "§r§7Spells that disadvantage other Warlocks.");
        
        inv.setItem(18,h);
        inv.setItem(20,p);
        inv.setItem(22,m);
        inv.setItem(24,d);
        inv.setItem(26,i);
        
        w.getPlayer().openInventory(inv);
    }
	
	public void openMasteriesMenu(Warlock w) {
		inv = Bukkit.createInventory(this, 45, "Masteries");
        setDefaults(w);
        inv.setItem(7,spells);
        inv.setItem(8,equipment);
        
        
        w.getPlayer().openInventory(inv);
	}
	
	public void openEquipmentMenu(Warlock w) {
		inv = Bukkit.createInventory(this, 45, "Equipment");
        setDefaults(w);
        inv.setItem(7,masteries);
        inv.setItem(8,spells);
        w.getPlayer().openInventory(inv);
	}
	
	public void openHarmfulSpells(Warlock w) {
		inv = Bukkit.createInventory(this, 45, "Harmful Spells");
        setDefaults(w);    
        
        ItemStack f;
        ItemStack h;
        ItemStack b;
        ItemStack l;
        
        ItemStack e1,e2,e3,e4;
        
        if(w.getSpells().containsKey(SpellType.FIREBALL)) {
        	f = w.getSpell(SpellType.FIREBALL).getNextItem();
        	if(w.getEquippedHarming() == w.getSpell(SpellType.FIREBALL)) {
        		e1 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e1 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Fireball", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
        } else {
        	f = new Fireshot().getNextItem();
        	e1 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
        }
        if(w.getSpells().containsKey(SpellType.HOMING)) {
        	h = w.getSpell(SpellType.HOMING).getNextItem();
        	if(w.getEquippedHarming() == w.getSpell(SpellType.HOMING)) {
        		e3 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e3 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Homing", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
        } else {
        	h = new Homing().getNextItem();
        	e3 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
        }
        if(w.getSpells().containsKey(SpellType.BOOMERANG)) {
        	b = w.getSpell(SpellType.BOOMERANG).getNextItem();
        	if(w.getEquippedHarming() == w.getSpell(SpellType.BOOMERANG)) {
        		e4 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e4 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Boomerang", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
        } else {
        	 b = new Boomerang().getNextItem();
        	 e4 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
        }
		if(w.getSpells().containsKey(SpellType.LIGHTNING)) {
			l = w.getSpell(SpellType.LIGHTNING).getNextItem();
			if(w.getEquippedHarming() == w.getSpell(SpellType.LIGHTNING)) {
        		e2 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e2 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Lightning", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
		} else {
			l = new Lightning().getNextItem();
			e2 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
		}
		
		inv.setItem(19,f);
		inv.setItem(21,l);
		inv.setItem(23,h);
		inv.setItem(25,b);
		inv.setItem(8,spells);
		inv.setItem(28, e1);
		inv.setItem(30, e2);
		inv.setItem(32, e3);
		inv.setItem(34, e4);
        
        w.getPlayer().openInventory(inv);
	}
	
	public void openPotentSpells(Warlock w) {
		inv = Bukkit.createInventory(this, 45, "Potent Spells");
        setDefaults(w);
        
        ItemStack f;
        ItemStack h;
        ItemStack b;
        ItemStack l;
        
        ItemStack e1,e2,e3,e4;
        
        if(w.getSpells().containsKey(SpellType.SCOURGE)) {
        	f = w.getSpell(SpellType.SCOURGE).getNextItem();
        	if(w.getEquippedPotent() == w.getSpell(SpellType.SCOURGE)) {
        		e1 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e1 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Scourge", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
        } else {
        	f = new Scourge().getNextItem();
        	e1 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
        }
        if(w.getSpells().containsKey(SpellType.METEOR)) {
        	h = w.getSpell(SpellType.METEOR).getNextItem();
        	if(w.getEquippedPotent() == w.getSpell(SpellType.METEOR)) {
        		e3 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e3 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Meteor", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
        } else {
        	h = new Meteor().getNextItem();
        	e3 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
        }
        if(w.getSpells().containsKey(SpellType.FIRE_SPRAY)) {
        	b = w.getSpell(SpellType.FIRE_SPRAY).getNextItem();
        	if(w.getEquippedPotent() == w.getSpell(SpellType.FIRE_SPRAY)) {
        		e4 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e4 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Fire Spray", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
        } else {
        	 b = new FireSpray().getNextItem();
        	 e4 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
        }
		if(w.getSpells().containsKey(SpellType.SPLITTER)) {
			l = w.getSpell(SpellType.SPLITTER).getNextItem();
			if(w.getEquippedPotent() == w.getSpell(SpellType.SPLITTER)) {
        		e2 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e2 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Splitter", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
		} else {
			l = new Splitter().getNextItem();
			e2 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
		}
		
		inv.setItem(19,f);
		inv.setItem(21,l);
		inv.setItem(23,h);
		inv.setItem(25,b);
		inv.setItem(8,spells);
		inv.setItem(28, e1);
		inv.setItem(30, e2);
		inv.setItem(32, e3);
		inv.setItem(34, e4);
        
        w.getPlayer().openInventory(inv);
	}
	
	public void openWardingSpells(Warlock w) {
		inv = Bukkit.createInventory(this, 45, "Warding Spells");
        setDefaults(w);
        
        ItemStack f;
        ItemStack h;
        ItemStack b;
        ItemStack l;
        
        ItemStack e1,e2,e3,e4;
        
        if(w.getSpells().containsKey(SpellType.SHIELD)) {
        	f = w.getSpell(SpellType.SHIELD).getNextItem();
        	if(w.getEquippedWarding() == w.getSpell(SpellType.SHIELD)) {
        		e1 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e1 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Shield", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
        } else {
        	f = new Shield().getNextItem();
        	e1 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
        }
        if(w.getSpells().containsKey(SpellType.TIME_SHIFT)) {
        	h = w.getSpell(SpellType.TIME_SHIFT).getNextItem();
        	if(w.getEquippedWarding() == w.getSpell(SpellType.TIME_SHIFT)) {
        		e2 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e2 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Time Shift", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
        } else {
        	h = new TimeShift().getNextItem();
        	e2 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
        }
        if(w.getSpells().containsKey(SpellType.RUSH)) {
        	b = w.getSpell(SpellType.RUSH).getNextItem();
        	if(w.getEquippedWarding() == w.getSpell(SpellType.RUSH)) {
        		e3 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e3 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Rush", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
        } else {
        	 b = new Rush().getNextItem();
        	 e3 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
        }
		if(w.getSpells().containsKey(SpellType.DRAIN)) {
			l = w.getSpell(SpellType.DRAIN).getNextItem();
			if(w.getEquippedWarding() == w.getSpell(SpellType.DRAIN)) {
        		e4 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e4 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Drain", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
		} else {
			l = new Drain().getNextItem();
			e4 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
		}
		
		inv.setItem(19,f);
		inv.setItem(21,l);
		inv.setItem(23,h);
		inv.setItem(25,b);
		inv.setItem(8,spells);
		inv.setItem(28, e1);
		inv.setItem(30, e4);
		inv.setItem(32, e2);
		inv.setItem(34, e3);
        
        w.getPlayer().openInventory(inv);
	}
	
	
	public void openImpairingSpells(Warlock w) {
		inv = Bukkit.createInventory(this, 45, "Impairing Spells");
        setDefaults(w);
        
        ItemStack f;
        ItemStack h;
        ItemStack b;
        ItemStack l;
        
        ItemStack e1,e2,e3,e4;
        
        if(w.getSpells().containsKey(SpellType.GRAVITY)) {
        	f = w.getSpell(SpellType.GRAVITY).getNextItem();
        	if(w.getEquippedImpairing() == w.getSpell(SpellType.GRAVITY)) {
        		e1 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e1 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Gravity", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
        } else {
        	f = new Gravity().getNextItem();
        	e1 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
        }
        if(w.getSpells().containsKey(SpellType.LINK)) {
        	h = w.getSpell(SpellType.LINK).getNextItem();
        	if(w.getEquippedImpairing() == w.getSpell(SpellType.LINK)) {
        		e3 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e3 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Link", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
        } else {
        	h = new Link().getNextItem();
        	e3 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
        }
        if(w.getSpells().containsKey(SpellType.DISABLE)) {
        	b = w.getSpell(SpellType.DISABLE).getNextItem();
        	if(w.getEquippedImpairing() == w.getSpell(SpellType.DISABLE)) {
        		e4 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e4 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Disable", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
        } else {
        	 b = new Disable().getNextItem();
        	 e4 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
        }
		if(w.getSpells().containsKey(SpellType.ROOT)) {
			l = w.getSpell(SpellType.ROOT).getNextItem();
			if(w.getEquippedImpairing() == w.getSpell(SpellType.ROOT)) {
        		e2 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e2 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Root", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
		} else {
			l = new Root().getNextItem();
			e2 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
		}
		
		inv.setItem(19,f);
		inv.setItem(21,l);
		inv.setItem(23,h);
		inv.setItem(25,b);
		inv.setItem(8,spells);
		inv.setItem(28, e1);
		inv.setItem(30, e2);
		inv.setItem(32, e3);
		inv.setItem(34, e4);
        
        w.getPlayer().openInventory(inv);
	}
	
	
	public void openMovementSpells(Warlock w) {
		inv = Bukkit.createInventory(this, 45, "Moving Spells");
        setDefaults(w);
        
        ItemStack f;
        ItemStack h;
        ItemStack b;
        ItemStack l;
        
        ItemStack e1,e2,e3,e4;
        
        if(w.getSpells().containsKey(SpellType.TELEPORT)) {
        	f = w.getSpell(SpellType.TELEPORT).getNextItem();
        	if(w.getEquippedMoving() == w.getSpell(SpellType.TELEPORT)) {
        		e1 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e1 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Teleport", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
        } else {
        	f = new Teleport().getNextItem();
        	e1 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
        }
        if(w.getSpells().containsKey(SpellType.THRUST)) {
        	h = w.getSpell(SpellType.THRUST).getNextItem();
        	if(w.getEquippedMoving() == w.getSpell(SpellType.THRUST)) {
        		e3 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e3 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Thrust", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
        } else {
        	h = new Thrust().getNextItem();
        	e3 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
        }
        if(w.getSpells().containsKey(SpellType.WIND_WALK)) {
        	b = w.getSpell(SpellType.WIND_WALK).getNextItem();
        	if(w.getEquippedMoving() == w.getSpell(SpellType.WIND_WALK)) {
        		e4 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e4 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Wind Walk", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
        } else {
        	 b = new WindWalk().getNextItem();
        	 e4 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
        }
		if(w.getSpells().containsKey(SpellType.SWAP)) {
			l = w.getSpell(SpellType.SWAP).getNextItem();
			if(w.getEquippedMoving() == w.getSpell(SpellType.SWAP)) {
        		e2 = createGuiItem(Material.GREEN_STAINED_GLASS_PANE, ChatColor.GREEN + "Currently Equipped", ChatColor.GRAY + "Buy other Warding Spells to equip them.");
        	} else {
        		e2 = createGuiItem(Material.RED_STAINED_GLASS_PANE, ChatColor.RED + "Equip Swap", ChatColor.GRAY + "Left click here to equip this Spell.");
        	}
		} else {
			l = new Swap().getNextItem();
			e2 = createGuiItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.DARK_GRAY + "Can't Equip", ChatColor.GRAY + "Buy this item to equip this Spell.");
		}
		
		inv.setItem(19,f);
		inv.setItem(21,l);
		inv.setItem(23,h);
		inv.setItem(25,b);
		inv.setItem(8,spells);
		inv.setItem(28, e1);
		inv.setItem(30, e2);
		inv.setItem(32, e3);
		inv.setItem(34, e4);
        
        w.getPlayer().openInventory(inv);
	}
	
	// Nice little method to create a gui item with a custom name, and description
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
    
    // Set default exit and gold for every inventory.
    protected void setDefaults(Warlock w) {
        inv.setItem(0,createGuiItem(Material.BARRIER, "§r§cExit Menu"));
        ItemStack gold = createGuiItem(Material.GOLD_NUGGET, "§r§6You have " + w.getGold() + " gold.", "§r§eYou can get more by getting","§r§ekills/assists or winning rounds.");
        if(w.getGold() > 0) {
        	gold.setAmount(w.getGold());
        	inv.setItem(1,gold);
        } else {
        	gold.setAmount(1);
        	inv.setItem(1,gold);
        }
    }
    
 // You can open the inventory with this
    public void openInventory(final HumanEntity ent)
    {
        ent.openInventory(inv);
    }
    
 // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e)
    {
    	final Player p = (Player) e.getWhoClicked();
        if(!plugin.warlockManager.containsKey(p.getUniqueId())) {
			return;
        }
        Warlock w = plugin.warlockManager.get(p.getUniqueId());
    	
        e.setCancelled(true);
        
        if (e.getInventory().getHolder() != this) {
//        	if(w.getCurrentGame() == null) {
//        		e.setCancelled(true);
//        		
//        	}
        	return;
        }

        

        final ItemStack clickedItem = e.getCurrentItem();
        
        
        
        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        String name = clickedItem.getItemMeta().getDisplayName();
        
        
        
        if(name.contains("Exit")) {
        	p.closeInventory();
        } else if(name.contains("Equipment")) {
        	openEquipmentMenu(w);
        } else if(name.contains("Harmful Spells")) {
        	openHarmfulSpells(w);
        } else if(name.contains("Potent Spells")) {
        	openPotentSpells(w);
        } else if(name.contains("Moving Spells")) {
        	openMovementSpells(w);
        } else if(name.contains("Warding Spells")) {
        	openWardingSpells(w);
        } else if(name.contains("Impairing Spells")) {
        	openImpairingSpells(w);
        } else if(name.contains("Spells")) {
        	openSpellsMenu(w);
        } else if(name.contains("Masteries")) {
        	openMasteriesMenu(w);
    	} else if(name.contains("Fireball")) {
    		if(name.contains("Equip")) {
    			w.getSpell(SpellType.FIREBALL).equipSpell();
    		} else {
    			w.buySpell(SpellType.FIREBALL);
    		}
        	openHarmfulSpells(w);
        } else if (name.contains("Lightning")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.LIGHTNING).equipSpell();
    		} else {
    			w.buySpell(SpellType.LIGHTNING);
    		}
        	openHarmfulSpells(w);
        } else if (name.contains("Homing")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.HOMING).equipSpell();
    		} else {
    			w.buySpell(SpellType.HOMING);
    		}
        	openHarmfulSpells(w);
        } else if (name.contains("Boomerang")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.BOOMERANG).equipSpell();
    		} else {
    			w.buySpell(SpellType.BOOMERANG);
    		}
        	openHarmfulSpells(w);
        } else if (name.contains("Meteor")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.METEOR).equipSpell();
    		} else {
    			w.buySpell(SpellType.METEOR);
    		}
        	openPotentSpells(w);
        } else if (name.contains("Scourge")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.SCOURGE).equipSpell();
    		} else {
    			w.buySpell(SpellType.SCOURGE);
    		}
        	openPotentSpells(w);
        } else if (name.contains("Splitter")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.SPLITTER).equipSpell();
    		} else {
    			w.buySpell(SpellType.SPLITTER);
    		}
        	openPotentSpells(w);
        } else if (name.contains("Fire Spray")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.FIRE_SPRAY).equipSpell();
    		} else {
    			w.buySpell(SpellType.FIRE_SPRAY);
    		}
        	openPotentSpells(w);
        } else if (name.contains("Teleport")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.TELEPORT).equipSpell();
    		} else {
    			w.buySpell(SpellType.TELEPORT);
    		}
        	openMovementSpells(w);
        } else if (name.contains("Swap")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.SWAP).equipSpell();
    		} else {
    			w.buySpell(SpellType.SWAP);
    		}
        	openMovementSpells(w);
        } else if (name.contains("Thrust")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.THRUST).equipSpell();
    		} else {
    			w.buySpell(SpellType.THRUST);
    		}
        	openMovementSpells(w);
        } else if (name.contains("Wind Walk")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.WIND_WALK).equipSpell();
    		} else {
    			w.buySpell(SpellType.WIND_WALK);
    		}
        	openMovementSpells(w);
        } else if (name.contains("Shield")) {
        	if(name.contains("Equip")) {
        		w.getSpell(SpellType.SHIELD).equipSpell();
    		} else {
    			w.buySpell(SpellType.SHIELD);
    		}
        	
        	openWardingSpells(w);
        } else if (name.contains("Time Shift")) {
        	if(name.contains("Equip")) {
        		w.getSpell(SpellType.TIME_SHIFT).equipSpell();
    		} else {
    			w.buySpell(SpellType.TIME_SHIFT);
    		}
        	
        	openWardingSpells(w);
        } else if (name.contains("Rush")) {
        	if(name.contains("Equip")) {
        		w.getSpell(SpellType.RUSH).equipSpell();
    		} else {
    			w.buySpell(SpellType.RUSH);
    		}
        	openWardingSpells(w);
        } else if (name.contains("Drain")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.DRAIN).equipSpell();
    		} else {
    			w.buySpell(SpellType.DRAIN);
    		}
        	
        	openWardingSpells(w);
        } else if (name.contains("Gravity")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.GRAVITY).equipSpell();
    		} else {
    			w.buySpell(SpellType.GRAVITY);
    		}
        	openImpairingSpells(w);
        } else if (name.contains("Link")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.LINK).equipSpell();
    		} else {
    			w.buySpell(SpellType.LINK);
    		}
        	openImpairingSpells(w);
        } else if (name.contains("Disable")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.DISABLE).equipSpell();
    		} else {
    			w.buySpell(SpellType.DISABLE);
    		}
        	openImpairingSpells(w);
        } else if (name.contains("Root")) {
        	if(name.contains("Equip")) {
    			w.getSpell(SpellType.ROOT).equipSpell();
    		} else {
    			w.buySpell(SpellType.ROOT);
    		}
        	openImpairingSpells(w);
        }
    }
    
    protected ItemStack enchantItem(ItemStack item) {
		item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
		ItemMeta m = item.getItemMeta();
        m.addItemFlags(ItemFlag.HIDE_ATTRIBUTES,ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(m);
        return item;
	}


	



    @Override
    public Inventory getInventory()
    {
        return inv;
    }
    
	
}
