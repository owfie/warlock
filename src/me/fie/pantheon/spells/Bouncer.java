package me.fie.pantheon.spells;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import me.fie.pantheon.warlock.Warlock;
import me.fie.pantheon.warlock.Warlock.SpellType;

public class Bouncer extends Spell {

	public Bouncer() {
		super(SpellType.BOUNCER, "Bouncer", 5, 1, 5, 1, 1.25, 0.8, 1.25);
		int[] c = {0,3,4,5,6};
		this.costs = c;
		item = createGuiItem(Material.SLIME_BALL, ""+name, "A projectile which bounces off objects towards the nearest Warlock.");
		enchantItem();
	}
	
	@Override
	public void shootSpell(PlayerInteractEvent e) {
		// TODO Auto-generated method stub

	}

}
