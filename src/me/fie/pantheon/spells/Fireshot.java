package me.fie.pantheon.spells;

import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.fie.pantheon.Main;
import me.fie.pantheon.warlock.Warlock;
import me.fie.pantheon.warlock.Warlock.SpellType;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;

public class Fireshot extends Spell {

	private Main plugin = Main.plugin;
	private int counter = 0;
	
	public Fireshot() {
		super(SpellType.FIREBALL, "Fireball", 5, 1, 3, 1, 1.25, 0.8, 1.25);
		int[] c = {0,3,4,5,6};
		this.costs = c;
		item = createGuiItem(Material.FIRE_CHARGE, ""+name, "A projectile of fire which explodes on impact.");
		enchantItem();
		setNextItem();
	}
	
	public void shootSpell(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		
//		Arrow f = p.launchProjectile(Arrow.class);
//		f.setShooter(p);

		MagmaCube f = (MagmaCube) p.getWorld().spawnEntity(p.getLocation(), EntityType.MAGMA_CUBE);
		
		
		Vector velocity = new Vector(p.getLocation().getDirection().getX(),0,p.getLocation().getDirection().getZ());
		velocity.normalize();
		
		// TODO Multiply speed by Fireball level
		f.setVelocity(velocity);
		f.setInvulnerable(true);
		f.setGravity(false);
		f.setSize(1);
		PotionEffect pot = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 4);
		f.addPotionEffect(pot);
		
		Fireball fb = p.getWorld().spawn(p.getLocation(),Fireball.class);
		
		f.addPassenger(fb);
		fb.setInvulnerable(true);
		fb.setIsIncendiary(false);
		
//		for(Player pl : Bukkit.getServer().getOnlinePlayers()) {
//		    PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(f.getEntityId());
//		    ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
//		}
		
		// Keep the velocity of the projectile constant
		counter = 0;
		new BukkitRunnable() {
			@Override
			public void run() {
				if(counter == 100 || !fb.isValid()) {
					try {
					f.remove();
					fb.remove();
					} catch (Exception e) {}
					this.cancel();
				}
				counter+=2;
				f.setVelocity(velocity);
				Collection<Entity> nearbyEntities = f.getLocation().getWorld().getNearbyEntities(f.getLocation(),1,1,1);
				for(Entity i : nearbyEntities) {
					if(i instanceof Player) {
						if(((Player) i) != p) {
							f.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, f.getLocation(), 5, 1.0,1.0,1.0);
							p.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
							p.getWorld().playSound(f.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 3, 1);
							
							Vector d = f.getVelocity().add(i.getLocation().toVector().subtract(f.getLocation().toVector()));
							d.normalize();
						
							if(plugin.warlockManager.containsKey(i.getUniqueId())) {
								Warlock w = plugin.warlockManager.get(i.getUniqueId());
								w.knockback(d);
								w.getPlayer().damage(2+(2*baseStrength));;
							}
							
							f.remove();
							fb.remove();
							this.cancel();
						}
					}
				}
				
			}
		}.runTaskTimer(plugin, 0, 2);		
	}
}
