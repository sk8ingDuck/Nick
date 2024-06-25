package me.sk8ingduck.nick.manager;

import dev.iiahmed.disguise.*;
import me.sk8ingduck.nick.util.Nickname;
import me.sk8ingduck.nick.util.UUIDFetcher;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public abstract class NickManager {

	private final DisguiseProvider provider = DisguiseManager.getProvider();
	protected final HashMap<Player, Nickname> nicknames = new HashMap<>();
	protected final HashMap<Player, String> realnames = new HashMap<>();

	public DisguiseResponse nickPlayer(Player player, Nickname nick) {
		Disguise.Builder builder = Disguise.builder();

		UUID uuid = UUIDFetcher.getUUID(nick.getName());
		if (uuid == null) {
			builder.setName(nick.getName());
		} else {
			builder.setName(nick.getName()).setSkin(uuid);
		}
		String realName = player.getName();
		DisguiseResponse response = provider.disguise(player, builder.build());

		if (response == DisguiseResponse.SUCCESS) {
			nicknames.put(player, nick);
			realnames.put(player, realName);

			nick(player, nick);
		}

		return response;
	}

	public UndisguiseResponse unnickPlayer(Player player) {
		UndisguiseResponse response = provider.undisguise(player);

		if (response == UndisguiseResponse.SUCCESS) {
			unnick(player);

			realnames.remove(player);
			nicknames.remove(player);
		}

		return response;
	}

	public Nickname getNickname(Player player) {
		return nicknames.get(player);
	}

	public String getRealName(Player player) { return realnames.get(player); }

	public abstract String getFakePrefix(Player player);

	public abstract Set<String> getGroupNames();

	/**
	 * Change the player's prefix and group to the TAB or NametagEdit group.
	 * @param player the player
	 * @param nick the nickname
	 */
	public abstract void nick(Player player, Nickname nick);

	/**
	 * Reset the player's prefix and group to the default TAB or NametagEdit group.
	 * @param player the player
	 */
	public abstract void unnick(Player player);

}
