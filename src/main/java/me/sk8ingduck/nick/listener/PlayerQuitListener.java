package me.sk8ingduck.nick.listener;

import me.sk8ingduck.nick.Nick;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		Nick.getInstance().getNickManager().unnickPlayer(player);
	}
}
