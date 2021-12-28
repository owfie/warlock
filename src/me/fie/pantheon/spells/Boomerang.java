package me.fie.pantheon.spells;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import me.fie.pantheon.spells.Spell;
import me.fie.pantheon.warlock.Warlock;
import me.fie.pantheon.warlock.Warlock.SpellType;

public class Boomerang extends Spell {

	public Boomerang() {
		super(SpellType.BOOMERANG, "Boomerang", 5, 1, 5, 1, 1.25, 0.8, 1.25);
		int[] c = {2,3,4,5,6};
		this.costs = c;
		item = createGuiItem(Material.BONE, ""+name, "Casts an invincible projectile which always returns to you.");
		enchantItem();
		setNextItem();
	}
	
	public void shootSpell(PlayerInteractEvent e) {
		// TODO Auto-generated method stub

	}

}
