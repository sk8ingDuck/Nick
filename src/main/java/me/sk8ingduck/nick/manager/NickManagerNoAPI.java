package me.sk8ingduck.nick.manager;

import me.sk8ingduck.nick.util.Nickname;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;

/**
 * If no tab plugin is found (like TAB or NametagEdit) this class will be used to handle nicknames.
 * Since there is no API to hook into, this class will not actually do anything.
 */
public class NickManagerNoAPI extends NickManager {

	@Override
	public String getFakePrefix(Player player) {
		return "";
	}

	@Override
	public Set<String> getGroupNames() {
		return Collections.emptySet();
	}

	@Override
	public void nick(Player player, Nickname nick) {

	}

	@Override
	public void unnick(Player player) {

	}
}
