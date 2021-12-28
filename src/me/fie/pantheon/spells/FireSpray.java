package me.fie.pantheon.spells;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import me.fie.pantheon.warlock.Warlock;
import me.fie.pantheon.warlock.Warlock.SpellType;

public class FireSpray extends Spell {

	public FireSpray() {
		super(SpellType.FIRE_SPRAY, "Fire Spray", 5, 1, 5, 1, 1.25, 0.8, 1.25);
		int[] c = {0,3,4,5,6};
		this.costs = c;
		item = createGuiItem(Material.BLAZE_POWDER, ""+name, "Casts a spray of fire in a target direction.");
		enchantItem();
		setNextItem();
	}
	
	@Override
	public void shootSpell(PlayerInteractEvent e) {
		// TODO Auto-generated method stub

	}

}
