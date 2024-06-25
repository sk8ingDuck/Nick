package me.sk8ingduck.nick.listener;

import me.sk8ingduck.nick.Nick;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack nickItem = Nick.getInstance().getSettingsConfig().getNickItem();

		if (event.getAction().equals(Action.PHYSICAL)
				|| event.getAction().equals(Action.LEFT_CLICK_AIR)
				|| event.getAction().equals(Action.LEFT_CLICK_BLOCK)
				|| player.getInventory().getItemInHand() == null
				|| player.getInventory().getItemInHand().getType() == null
				|| player.getInventory().getItemInHand().getType() == Material.AIR
				|| player.getInventory().getItemInHand().getItemMeta() == null
				|| player.getInventory().getItemInHand().getItemMeta().getDisplayName() == null
				|| nickItem.getItemMeta() == null
				|| nickItem.getItemMeta().getDisplayName() == null
				|| !player.getInventory().getItemInHand().getItemMeta().getDisplayName()
				.equalsIgnoreCase(nickItem.getItemMeta().getDisplayName())) {
			return;
		}
		player.performCommand("nick");
	}
}
