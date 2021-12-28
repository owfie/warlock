package me.fie.pantheon.spells;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import me.fie.pantheon.spells.Spell;
import me.fie.pantheon.warlock.Warlock;
import me.fie.pantheon.warlock.Warlock.SpellType;
import net.md_5.bungee.api.ChatColor;

public class Lightning extends Spell {

	public Lightning() {
		super(SpellType.LIGHTNING , "Lightning", 5, 1, 5, 1, 1.25, 0.8, 1.25);
		int[] c = {2,3,4,5,6};
		this.costs = c;
		item = createGuiItem(Material.PRISMARINE_SHARD, ""+name, "Summons a lightning bolt to the nearest Warlock to the cast point.");
		enchantItem();
		setNextItem();
	}
	
	@Override
	public void shootSpell(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		p.setCooldown(e.getMaterial(), 100);
		p.sendMessage(ChatColor.GREEN + "Woosh!");
		Arrow ws = p.launchProjectile(Arrow.class);
		ws.setShooter(p);
		ws.setGravity(false);
		ws.setInvulnerable(true);
		BlockData bd = Bukkit.createBlockData(Material.PACKED_ICE);
		FallingBlock fb = ws.getWorld().spawnFallingBlock(ws.getLocation(), bd);
		fb.setVelocity(ws.getVelocity());
		fb.setVelocity(fb.getVelocity().setY(0));
		fb.setDropItem(false);
		fb.setHurtEntities(false);
		fb.setGravity(false);
		fb.setGlowing(true);
		
	}

}
