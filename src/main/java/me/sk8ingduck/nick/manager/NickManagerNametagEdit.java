package me.sk8ingduck.nick.manager;

import com.nametagedit.plugin.NametagEdit;
import com.nametagedit.plugin.api.INametagApi;
import com.nametagedit.plugin.api.data.GroupData;
import me.sk8ingduck.nick.Nick;
import me.sk8ingduck.nick.util.Nickname;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class NickManagerNametagEdit extends NickManager {
	private final HashMap<String, PermissionAttachment> perms = new HashMap<>();

	private final INametagApi api;

	public NickManagerNametagEdit() {
		api = NametagEdit.getApi();
	}

	public void nick(Player player, Nickname nick) {
		PermissionAttachment attachment = player.addAttachment(Nick.getInstance());

		//remove all nametag permissions from the player while he is nicked
		api.getGroupData()
				.forEach(groupData -> attachment.setPermission(groupData.getPermission(), false));

		//add permission to player for the new nametag group the player will be in
		api.getGroupData()
				.stream()
				.filter(groupData -> groupData.getGroupName().equalsIgnoreCase(nick.getGroup()))
				.findFirst().ifPresent(groupData -> attachment.setPermission(groupData.getPermission(), true));
		perms.put(nick.getName(), attachment);
		api.reloadNametag(player);
	}


	public void unnick(Player player) {
		Nickname nick = nicknames.get(player);
		player.removeAttachment(perms.get(nick.getName()));
		//restore permissions, so that the player gets his correct Nametag group again
		perms.remove(nick.getName());
		api.reloadNametag(player);
	}

	public String getFakePrefix(Player player) {
		return api.getNametag(player).getPrefix();
	}

	public Set<String> getGroupNames() {
		return api.getGroupData().stream().map(GroupData::getGroupName).collect(Collectors.toSet());
	}
}
