package me.fie.pantheon.warlock;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldedit.world.entity.EntityType;

import org.bukkit.event.entity.PlayerDeathEvent;


import me.fie.pantheon.Main;
import me.fie.pantheon.spells.Fireshot;
import me.fie.pantheon.spells.Gravity;
import me.fie.pantheon.spells.Homing;
import me.fie.pantheon.spells.Lightning;
import me.fie.pantheon.spells.Scourge;
import me.fie.pantheon.spells.Shield;
import me.fie.pantheon.spells.Teleport;
import me.fie.pantheon.spells.WindWalk;
import me.fie.pantheon.warlock.Warlock.SpellType;
import net.md_5.bungee.api.ChatColor;

public class SpellHandler implements Listener {

	private Main plugin = Main.plugin;
	
	// Cancels interactions unless OP, calls spell shooting function
	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			chooseSpell(e);
			if(!e.getPlayer().isOp()) {
				e.setCancelled(true);
			}
		} else {
			if(!e.getPlayer().isOp()) {
				e.setCancelled(true);
			}
		}
	}
	
	// Cancel item drops
	@EventHandler
	public void onDrop(PlayerDropItemEvent e) {
		if(!e.getPlayer().isOp()) {
			e.setCancelled(true);
		}
	}
	
	// Movement handler for dealing custom lava damage
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(plugin.warlockManager.containsKey(e.getPlayer().getUniqueId())) {
			if(e.getTo().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.MAGMA_BLOCK)) {
				plugin.warlockManager.get(e.getPlayer().getUniqueId()).doLavaDamage();
			}
		}
	}
	
	// Disable all natural damage Entity vs Entity
	@EventHandler
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		event.setCancelled(true);
	}
	
	// Disable all damage done by hot floors
	@EventHandler
    public void onDamage(EntityDamageEvent event){
        if (event.getEntity() instanceof Player) {     
        	if(event.getCause() == DamageCause.HOT_FLOOR) {
        		event.setCancelled(true);
        	}
        }
    }
	
	// Disable native explosions
	@EventHandler
	public void onExplosion(EntityExplodeEvent e) {
		e.setCancelled(true);
	}
	
	// Cancel vehicle collisions
	@EventHandler
	public void onVehicCollision(VehicleEntityCollisionEvent e) {
		e.setCancelled(true);
	}
	
	// General projectile hit event handling
	@EventHandler
	public void onHit(ProjectileHitEvent e) {
		if(e.getHitBlock() != null) {
			Block hitblock = e.getHitBlock();
			hitblock.getWorld().createExplosion(hitblock.getLocation(), 0, false);
		}
		Entity target = e.getHitEntity();
		if(target instanceof Player) {
			Player p = (Player) target;
			p.damage(10);
		}
	}
	
	public void chooseSpell(PlayerInteractEvent e) {
		
		Player p = e.getPlayer();
		if(e.getItem() == null) {
			return;
		}
		if(p.hasCooldown(e.getMaterial())) {
			p.sendMessage(ChatColor.RED + "This spell is on cooldown.");
			return;
		}
		
		if(!plugin.warlockManager.containsKey(e.getPlayer().getUniqueId())) {
			// TODO 
			// This will fire for ALL items, not just spell items. 
			Bukkit.getConsoleSender().sendMessage("A non-Warlock tried to use a Warlock spell. This isn't an error but it probably shouldn't be happening. Did an OP Warlock drop a spell item?");
			return;
		}
		
		// TODO
		// Set item cooldown based on Spell cooldown value.
			// This could be done natively in the shoot spell method?

		// TODO
		// Replace Spell constructors with Warlock friend Spells. 
		// This will require adding spells to Warlocks.		
		
		Warlock w = plugin.warlockManager.get(p.getUniqueId());
		ItemStack i = e.getItem();
		Inventory inv = p.getInventory();
		int index = 0;
		for(index = 0; index < inv.getSize(); index++) {
			if(i.equals(inv.getItem(index))) {
				break;
			}
		}
		
		switch(index) {
		case 2: {
			w.getEquippedHarming().shootSpell(e);
			int c = (int) w.getEquippedHarming().getBaseCooldown()*20;
			p.setCooldown(e.getMaterial(), c);
		}
			break;
		case 3: {
			w.getEquippedPotent().shootSpell(e);
			int c = (int) w.getEquippedPotent().getBaseCooldown()*20;
			p.setCooldown(e.getMaterial(), c);
		}
			break;
		case 4: {
			w.getEquippedMoving().shootSpell(e);
			int c = (int) w.getEquippedMoving().getBaseCooldown()*20;
			p.setCooldown(e.getMaterial(), c);
		}
			break;
		case 5: {
			w.getEquippedWarding().shootSpell(e);
			int c = (int) w.getEquippedWarding().getBaseCooldown()*20;
			p.setCooldown(e.getMaterial(), c);
		}
			break;
		case 6: {
			w.getEquippedImpairing().shootSpell(e);
			int c = (int) w.getEquippedImpairing().getBaseCooldown()*20;
			p.setCooldown(e.getMaterial(), c);
		}
			break;
		default:
			break;
		}
		
	}
}
