package me.sk8ingduck.nick.hook;

import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import dev.iiahmed.disguise.PlayerInfo;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPIExpansion extends PlaceholderExpansion {

	private final DisguiseProvider provider = DisguiseManager.getProvider();

	@Override
	public @NotNull String getIdentifier() {
		return "nick";
	}

	@Override
	public @NotNull String getAuthor() {
		return "sk8ingDuck";
	}

	@Override
	public @NotNull String getVersion() {
		return "2.1";
	}

	@Override
	public @Nullable String onPlaceholderRequest(final Player player, @NotNull String params) {
		if (player == null) {
			return "";
		}

		String request = params.toLowerCase();
		PlayerInfo info = provider.getInfo(player);
		switch (request) {
			case "name":
				return info.getNickname();
			case "realname":
				return info.getName();
			case "is_nicked":
			case "is_disguised":
				return String.valueOf(provider.isDisguised(player));
		}
		return null;
	}

}