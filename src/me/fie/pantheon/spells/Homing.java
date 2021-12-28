package me.fie.pantheon.spells;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.fie.pantheon.Main;
import me.fie.pantheon.warlock.Warlock;
import me.fie.pantheon.warlock.Warlock.SpellType;
import net.md_5.bungee.api.ChatColor;

public class Homing extends Spell {

	
	
	private Main plugin = Main.plugin;
	
	public Homing() {
		super(SpellType.HOMING , "Homing", 5, 1, 5, 1, 1.25, 0.8, 1.25);
		int[] c = {2,3,4,5,6};
		this.costs = c;
		item = createGuiItem(Material.SNOWBALL, ""+name, "A projectile which moves towards the nearest enemy Warlock.");
		enchantItem();
		setNextItem();
	}
	
	@Override
	public void shootSpell(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		ShulkerBullet sb = p.launchProjectile(ShulkerBullet.class);
		sb.setShooter(p);
		sb.setGravity(false);
		sb.setInvulnerable(true);

		sb.setVelocity(new Vector(p.getLocation().getDirection().getX(),0,p.getLocation().getDirection().getZ()));

		sb.setVelocity(sb.getVelocity().multiply(0.25));

		new BukkitRunnable() {
			@Override
			public void run() {
				// If projectile (sb) doesn't exist, don't run
				if(!sb.isValid()) {
					this.cancel();
				}
				// Sort through the list of nearby entities in a 10 block radius
				double closest = Double.MAX_VALUE;
				Player closestp = null;
				List<Entity> l = sb.getNearbyEntities(30, 0, 30);
				l.remove(p); // Remove the shooter from the list of "attracted" targets
				if(!l.isEmpty()) {
					for(Entity e : l) {
						double dist = e.getLocation().distance(sb.getLocation());
						if(e.getType().equals(EntityType.PLAYER)) {
							if(closest == Double.MAX_VALUE || dist < closest) {
								closest = dist;
								closestp = (Player) e;
							}								
						}
					}
					// Intermediary "pull factor" vector - difference vector between the projectile and the target
					Vector pull = closestp.getLocation().toVector().setY(0).subtract(sb.getLocation().toVector().setY(0));
					pull.normalize();
					double fac = 2/(closestp.getLocation().distance(sb.getLocation()));
					double newfac = 0.2+(0.6*fac);
					pull.multiply(fac);
					// The current velocity of the projectile
					Vector current = sb.getVelocity();
					// Add the 
//					current.add(pull);
					// Normalize the new direction vector
					Vector resultant = current.add(pull);
//					resultant.setX(current.getX()/(current.getX()+current.getZ()));
//					resultant.setZ(current.getZ()/(current.getX()+current.getZ()));
					// Slow the projectile down when it changes direction
					resultant.setY(0).normalize().multiply(newfac);	
					// Set the projectile's new velocity
					sb.setVelocity(resultant);	
				}			
			}
			//								p.sendMessage(Double.toString(resultant.getX()) + " " + Double.toString(resultant.getY()) + " " + Double.toString(resultant.getZ()));
		}.runTaskTimer(plugin, 20, 5);
		
		new BukkitRunnable() {
			@Override
			public void run() {
				sb.remove();
			}
		}.runTaskLater(plugin, 200);
		
		
//					sb.setMetadata(arg0, arg1);
//					sb.addPassenger(p);
//					sb.setVelocity(p.getLocation().getDirection().multiply(1));
//					f.setYield(7.0F);
		

	}

}
