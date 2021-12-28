package me.fie.pantheon.spells;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.fie.pantheon.warlock.Warlock;
import me.fie.pantheon.warlock.Warlock.SpellType;

public class WindWalk extends Spell {

	public WindWalk() {
		super(SpellType.WIND_WALK , "Wind Walk", 5, 1, 5, 1, 1.25, 0.8, 1.25);
		int[] c = {0,3,4,5,6};
		this.costs = c;
		item = createGuiItem(Material.FEATHER, ""+name, "Turns the caster invisible and gives a speed boost.");
		enchantItem();
		setNextItem();
	}
	
	@Override
	public void shootSpell(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		PotionEffect pot = new PotionEffect(PotionEffectType.INVISIBILITY, 50, 4);
		PotionEffect pot2 = new PotionEffect(PotionEffectType.SPEED, 50, 2);
		p.addPotionEffect(pot);
		p.addPotionEffect(pot2);
		
	}

}
