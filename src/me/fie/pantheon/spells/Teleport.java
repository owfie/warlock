package me.fie.pantheon.spells;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import me.fie.pantheon.warlock.Warlock;
import me.fie.pantheon.warlock.Warlock.SpellType;

public class Teleport extends Spell {

	public Teleport() {
		super(SpellType.TELEPORT , "Teleport", 5, 1, 5, 1, 1.25, 0.8, 1.25);
		int[] c = {0,3,4,5,6};
		this.costs = c;
		item = createGuiItem(Material.ENDER_PEARL, ""+name, "Instantly teleports you to where you look.");
		enchantItem();
		setNextItem();
	}
	
	@Override
	public void shootSpell(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		Block b = (Block) p.getTargetBlock(null, 30);
		Location tpto = b.getLocation();
		if(b.getState().getType() == Material.AIR || b.getState().getType() == Material.BARRIER) {
			tpto.setY(p.getLocation().getY());
		} else {
			tpto.setY(tpto.getY()+1);
		}
		Float pitch = p.getLocation().getPitch();
		Float yaw = p.getLocation().getYaw();
		tpto.setYaw(yaw);
		tpto.setPitch(pitch);
		p.teleport(tpto);
		p.playSound(p.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1, 1);
	}

}
