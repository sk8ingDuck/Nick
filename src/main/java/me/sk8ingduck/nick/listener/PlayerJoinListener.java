package me.sk8ingduck.nick.listener;

import dev.iiahmed.disguise.DisguiseResponse;
import me.sk8ingduck.nick.Nick;
import me.sk8ingduck.nick.config.MessagesConfig;
import me.sk8ingduck.nick.config.SettingsConfig;
import me.sk8ingduck.nick.manager.NickManager;
import me.sk8ingduck.nick.sql.Database;
import me.sk8ingduck.nick.util.Nickname;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Optional;

public class PlayerJoinListener implements Listener {
	private final Database sql = Nick.getInstance().getSQL();


	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		SettingsConfig settingsConfig = Nick.getInstance().getSettingsConfig();
		Player player = event.getPlayer();

		if (settingsConfig.isGiveNickItemOnJoin() && player.hasPermission(settingsConfig.getNickItemPermission())) {
			player.getInventory().setItem(settingsConfig.getNickItemSlot(), settingsConfig.getNickItem());
		}

		if (!settingsConfig.shouldNickOnThisServer()) {
			return;
		}

		Optional<Nickname> optNick = sql.getNick(player.getUniqueId());
		if (!optNick.isPresent()) return;

		Nickname nick = optNick.get();
		if (!settingsConfig.isUsePreviousNick()) {
			nick = new Nickname(settingsConfig.getRandomName(), settingsConfig.getRandomGroup(player));
			sql.setNick(player.getUniqueId(), nick);
		}

		NickManager nickManager = Nick.getInstance().getNickManager();
		MessagesConfig messagesConfig = Nick.getInstance().getMessagesConfig();
		DisguiseResponse response = nickManager.nickPlayer(player, nick);
		if (response == DisguiseResponse.SUCCESS) {
			String nickName = nick.getName();
			Bukkit.getScheduler().scheduleSyncDelayedTask(Nick.getInstance(), () ->
							player.sendMessage(messagesConfig.get("nick.successful.self")
							.replaceAll("%PREFIX%", nickManager.getFakePrefix(player))
							.replaceAll("%NAME%", nickName)),
					10L);
			Nickname finalNick = nick;
			Bukkit.getScheduler().scheduleSyncDelayedTask(Nick.getInstance(),
					() -> nickManager.nick(player, finalNick), 10L);
		} else {
			player.sendMessage(messagesConfig.get("nick.unsuccessful." + response.toString().toLowerCase()));
		}
	}

}
