package me.fie.pantheon.spells;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.fie.pantheon.Main;
import me.fie.pantheon.warlock.Warlock;
import me.fie.pantheon.warlock.Warlock.SpellType;

public class Gravity extends Spell {

	public Gravity() {
		super(SpellType.GRAVITY, "Gravity", 5, 1, 5, 1, 1.25, 0.8, 1.25);
		int[] c = {0,3,4,5,6};
		this.costs = c;
		item = createGuiItem(Material.NETHER_STAR, ""+name, "A projectile which drags enemy Warlocks towards it.");
		enchantItem();
		setNextItem();
	}
	
	private Main plugin = Main.plugin;
	
	@Override
	public void shootSpell(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		Snowball f = p.launchProjectile(Snowball.class);
		f.setShooter(p);
		f.setVelocity(new Vector(p.getLocation().getDirection().getX()*0.3,0,p.getLocation().getDirection().getZ()*0.3));
		f.setGravity(false);


		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!f.isValid()) {
					this.cancel();
				}
				List<Entity> l = f.getNearbyEntities(15, 0, 15);
				l.remove(p); // Remove the shooter from the list of "attracted" targets
				if(!l.isEmpty()) {
					for(Entity i : l) {
						if(i instanceof Player) {
							Vector pull = f.getLocation().toVector().setY(0).subtract(i.getLocation().toVector().setY(0));
							pull.normalize();
							double fac = 1/(f.getLocation().distance(i.getLocation()));
							double newfac = 0.2+(0.6*fac);
							pull.multiply(fac);
							// The current velocity of the projectile
							Vector current = i.getVelocity();
							// Add the 
//							current.add(pull);
							// Normalize the new direction vector
							Vector resultant = current.add(pull);
//							resultant.setX(current.getX()/(current.getX()+current.getZ()));
//							resultant.setZ(current.getZ()/(current.getX()+current.getZ()));
							// Slow the projectile down when it changes direction
							resultant.setY(0).normalize().multiply(newfac);	
							// Set the projectile's new velocity
							i.setVelocity(resultant);	
							
						}
						// Intermediary "pull factor" vector - difference vector between the projectile and the target
										
					}
				}
			}
		}.runTaskTimer(plugin, 20, 5);
		new BukkitRunnable() {
			@Override
			public void run() {
				f.remove();
			}
		}.runTaskLater(plugin, 100);
	}

}
