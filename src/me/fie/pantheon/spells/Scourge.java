package me.fie.pantheon.spells;

import java.util.Collection;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.fie.pantheon.Main;
import me.fie.pantheon.warlock.Warlock;
import me.fie.pantheon.warlock.Warlock.SpellType;

public class Scourge extends Spell {

	private Main plugin = Main.plugin;
	
	public Scourge() {
		super(SpellType.SCOURGE , "Scourge", 5, 1, 5, 1, 1.25, 0.8, 1.25);
		int[] c = {0,3,4,5,6};
		this.costs = c;
		item = createGuiItem(Material.GUNPOWDER, ""+name, "Deals damage to you and any surrounding Warlocks.");
		enchantItem();
		setNextItem();
	}
	
	@Override
	public void shootSpell(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		p.playSound(p.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1, 1);
		PotionEffect pot = new PotionEffect(PotionEffectType.SLOW, 50, 4);
		p.addPotionEffect(pot);
		new BukkitRunnable() {
			public void run() {
				p.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, p.getLocation(), 5, 1.0,1.0,1.0);
				p.getWorld().playSound(p.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 3, 1);
				Collection<Entity> nearbyEntities = p.getLocation().getWorld().getNearbyEntities(p.getLocation(),3,3,3);
				for(Entity i : nearbyEntities) {
					if(i instanceof Player) {
						if(((Player) i) != p) {
							
							p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
							
							
							Vector d = p.getVelocity().add(i.getLocation().toVector().subtract(p.getLocation().toVector())).multiply(1.5);
							d.normalize();
						
							if(plugin.warlockManager.containsKey(i.getUniqueId())) {
								Warlock w = plugin.warlockManager.get(i.getUniqueId());
								w.knockback(d);
								w.getPlayer().damage(2+(2*baseStrength));;
							}

						}
					}
				}
			}
		}.runTaskLater(plugin, 50);
	}
}
