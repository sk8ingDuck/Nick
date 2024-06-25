package me.sk8ingduck.nick.manager;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.nametag.NameTagManager;
import me.neznamy.tab.api.tablist.TabListFormatManager;
import me.sk8ingduck.nick.util.Nickname;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PrefixNode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.Set;
import java.util.stream.Collectors;

public class NickManagerTAB extends NickManager {

	private final RegisteredServiceProvider<LuckPerms> provider;

	public NickManagerTAB() {
		provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
	}

	public void nick(Player player, Nickname nick) {
		TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
		TabListFormatManager tablistFormatManager = TabAPI.getInstance().getTabListFormatManager();
		if (tabPlayer != null && tablistFormatManager != null)
			tablistFormatManager.setName(tabPlayer, nick.getName()); //set name in tab list

		Group newGroup = provider.getProvider().getGroupManager().getGroup(nick.getGroup()); //luckperms group

		if (newGroup == null) return;

		String prefix = newGroup.getCachedData().getMetaData().getPrefix();
		if (tablistFormatManager != null && tabPlayer != null)
			tablistFormatManager.setPrefix(tabPlayer, prefix);

		NameTagManager nametagManager = TabAPI.getInstance().getNameTagManager();
		if (nametagManager != null && tabPlayer != null)
			nametagManager.setPrefix(tabPlayer, prefix);
		if (prefix != null) {
			provider.getProvider().getUserManager().modifyUser(player.getUniqueId(),
					user -> user.data().add(PrefixNode.builder(prefix, 100).build()));
		}
		/*
		for (PrefixNode prefixNode : newGroup.getNodes(NodeType.PREFIX)) {
			String prefix = prefixNode.getMetaValue();
			tablistFormatManager.setPrefix(tabPlayer, prefix);
			teamManager.setPrefix(tabPlayer, prefix);
			break;
		}
		*/
		if (tabPlayer != null)
			tabPlayer.setTemporaryGroup(nick.getGroup());
	}


	public void unnick(Player player) {
		NameTagManager teamManager = TabAPI.getInstance().getNameTagManager();
		TabPlayer tabPlayer = TabAPI.getInstance().getPlayer(player.getUniqueId());
		TabListFormatManager tablistFormatManager = TabAPI.getInstance().getTabListFormatManager();

		tablistFormatManager.setPrefix(tabPlayer, getOriginalPrefix(player));
		tablistFormatManager.setName(tabPlayer, player.getName());

		teamManager.setPrefix(tabPlayer, getOriginalPrefix(player));

		tabPlayer.setTemporaryGroup(null);

		provider.getProvider().getUserManager().modifyUser(player.getUniqueId(),
				user -> user.data().clear(node -> node.getType() == NodeType.PREFIX));
	}

	public String getOriginalPrefix(Player player) {
		User user = provider.getProvider().getUserManager().getUser(player.getUniqueId());
		Group ogGroup = provider.getProvider().getGroupManager().getGroup(user.getPrimaryGroup());
		return ogGroup == null ? "" : ogGroup.getCachedData().getMetaData().getPrefix();
	}
	public String getFakePrefix(Player player) {
		Group newGroup = provider.getProvider().getGroupManager().getGroup(nicknames.get(player).getGroup());

		if (newGroup == null || newGroup.getCachedData().getMetaData().getPrefix() == null) return "";
		return newGroup.getCachedData().getMetaData().getPrefix().replaceAll("&", "§");
	}

	public Set<String> getGroupNames() {
		return provider.getProvider().getGroupManager()
				.getLoadedGroups().stream().map(Group::getName).collect(Collectors.toSet());
	}

}
