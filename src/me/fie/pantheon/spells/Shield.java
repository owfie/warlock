package me.fie.pantheon.spells;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.fie.pantheon.Main;
import me.fie.pantheon.warlock.Warlock;
import me.fie.pantheon.warlock.Warlock.SpellType;

public class Shield extends Spell {

	private Main plugin = Main.plugin;
	
	public Shield() {
		super(SpellType.SHIELD , "Shield", 5, 1, 5, 1, 1.25, 0.8, 1.25);
		int[] c = {0,3,4,5,6};
		this.costs = c;
		item = createGuiItem(Material.MAGMA_CREAM, ""+name, "Summons a protective bubble around the caster which deflects enemy spells.");
		enchantItem();

		setNextItem();
	}
	
	@Override
	public void shootSpell(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Location loc = p.getLocation();

		Creeper c = (Creeper)p.getWorld().spawnEntity(loc, EntityType.CREEPER);
		Creeper d = (Creeper)p.getWorld().spawnEntity(loc, EntityType.CREEPER);
		Slime s = (Slime)p.getWorld().spawnEntity(loc, EntityType.SLIME);
		PotionEffect pot = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 4);
		PotionEffect pott = new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 4);
		
		// Slime
		s.setSize(-3);
		s.addPassenger(c);
		s.setInvulnerable(true);
		s.addPotionEffect(pot);
		s.addPotionEffect(pott);
		p.addPassenger(s);
		
		c.setPowered(true);
		c.setInvulnerable(true);
		c.addPotionEffect(pot);
		c.setAI(false);
		c.setCollidable(false);
		d.setPowered(true);
		d.setInvulnerable(true);
		d.addPotionEffect(pot);
		d.setAI(false);
		d.setCollidable(false);
		d.setCustomName("Dinnerbone");
		s.addPassenger(d);
		
		p.setCollidable(false);
		
		
		
		new BukkitRunnable() {
			public void run() {
				s.remove();
				c.remove();
				d.remove();
			}
		}.runTaskLater(plugin, 100);
	}

}
