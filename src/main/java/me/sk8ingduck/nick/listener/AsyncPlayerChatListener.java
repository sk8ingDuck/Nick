package me.sk8ingduck.nick.listener;

import me.clip.placeholderapi.PlaceholderAPI;
import me.sk8ingduck.nick.Nick;
import me.sk8ingduck.nick.config.SettingsConfig;
import me.sk8ingduck.nick.manager.NickManager;
import me.sk8ingduck.nick.util.Nickname;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.lang.reflect.Method;

public class AsyncPlayerChatListener implements Listener {

	private final NickManager nickManager = Nick.getInstance().getNickManager();

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		SettingsConfig settingsConfig = Nick.getInstance().getSettingsConfig();

		if (!settingsConfig.isFormatChat())
			return;

		Player player = event.getPlayer();
		Nickname nickname = nickManager.getNickname(player);

		if (nickname == null) return;

		event.setCancelled(true);

		String msg = reformat(player, settingsConfig.getChatFormat()
				.replaceAll("%PREFIX%", nickManager.getFakePrefix(player))
				.replaceAll("%NAME%", nickname.getName())
				.replaceAll("%MESSAGE%", event.getMessage()));

		Bukkit.broadcastMessage(msg);
	}

	private String reformat(Player player, String msg) {
		msg = PlaceholderAPI.setPlaceholders(player, msg);
		msg = replaceItemsAdderPlaceholders(msg);
		msg = msg.replaceAll("&", "§");
		return msg;
	}

	private String replaceItemsAdderPlaceholders(String text) {
		try {
			Class<?> fontImageWrapperClass = Class.forName("dev.lone.itemsadder.api.FontImages.FontImageWrapper");
			Method replaceFontImagesMethod = fontImageWrapperClass.getMethod("replaceFontImages", String.class);
			return (String) replaceFontImagesMethod.invoke(null, text);
		} catch (Exception e) {
			return text;
		}
	}
}
