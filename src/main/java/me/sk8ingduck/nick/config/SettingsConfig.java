package me.sk8ingduck.nick.config;

import me.sk8ingduck.nick.Nick;
import me.sk8ingduck.nick.util.Group;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class SettingsConfig extends Config {

	private final boolean nickPlayersOnThisServer;
	private final boolean usePreviousNick;
	private final int nickCooldown;
	private final boolean formatChat;
	private final String chatFormat;
	private final String defaultGroup;
	private final ArrayList<String> names;
	private final ArrayList<String> blacklistedNames;
	private ArrayList<Group> groups;

	private final boolean giveNickItemOnJoin;

	private final String nickItemPermission;
	private final ItemStack nickItem;
	private final int nickItemSlot;
	public SettingsConfig(String name, File path) {
		super(name, path);

		nickPlayersOnThisServer = (boolean) getPathOrSet("nickPlayersOnThisServer", true);
		usePreviousNick = (boolean) getPathOrSet("usePreviousNick", true);
		nickCooldown = (int) getPathOrSet("nickCooldown", 30);

		formatChat = (boolean) getPathOrSet("chatFormat.enabled", true);
		chatFormat = (String) getPathOrSet("chatFormat.format", "%PREFIX%%NAME%&7: &r%MESSAGE%");

		ArrayList<String> defaultNames = new ArrayList<>(Arrays.asList("Notch", "Stone"));
		names = (ArrayList<String>) getPathOrSet("names", defaultNames);

		ArrayList<String> defaultBlacklistedNames = new ArrayList<>(Arrays.asList("md_5", "Hitler"));
		blacklistedNames = (ArrayList<String>) getPathOrSet("blacklistedNames", defaultBlacklistedNames);

		giveNickItemOnJoin = (boolean) getPathOrSet("nickItem.giveItemOnJoin", true);
		nickItemPermission = (String) getPathOrSet("nickItem.permissionNeeded", "nick.item");
		ItemStack nickItem = new ItemStack(Material.NAME_TAG);
		ItemMeta nickMeta = nickItem.getItemMeta();
		nickMeta.setDisplayName("§5Nick");
		nickItem.setItemMeta(nickMeta);
		nickItemSlot = (Integer) getPathOrSet("nickItem.slot", 4);
		this.nickItem = (ItemStack) getPathOrSet("nickItem.item", nickItem);

		defaultGroup = (String) getPathOrSet("defaultGroup", "default");

		ArrayList<Group> defaultGroups = new ArrayList<>();

		//delay this because otherwise groups might be empty
		Bukkit.getScheduler().scheduleSyncDelayedTask(Nick.getInstance(), () -> {
			Nick.getInstance().getNickManager().getGroupNames()
					.forEach(groupName -> defaultGroups.add(new Group(groupName, 10, null)));
			groups = (ArrayList<Group>) getPathOrSet("groups", defaultGroups);
		}, 40L);

	}

	public boolean shouldNickOnThisServer() {
		return nickPlayersOnThisServer;
	}

	public boolean isUsePreviousNick() {
		return usePreviousNick;
	}

	public int getNickCooldown() {
		return nickCooldown;
	}

	public boolean isFormatChat() {
		return this.formatChat;
	}

	public String getChatFormat() {
		return this.chatFormat;
	}

	public boolean isGiveNickItemOnJoin() {
		return giveNickItemOnJoin;
	}

	public String getNickItemPermission() {
		return nickItemPermission;
	}

	public int getNickItemSlot() {
		return nickItemSlot;
	}

	public ItemStack getNickItem() {
		return nickItem;
	}

	public String getRandomName() {
		ThreadLocalRandom random = ThreadLocalRandom.current();
		return names.get(random.nextInt(names.size()));
	}

	public String getRandomGroup(CommandSender commandSender) {
		int randomInt = ThreadLocalRandom.current().nextInt(100);
		int sum = 0;

		for (Group group : groups) {
			if (group.getPermission() != null && !commandSender.hasPermission(group.getPermission()))
				continue;

			sum += group.getProbability();
			if (sum > randomInt) return group.getGroup();
		}

		return defaultGroup;
	}

	public boolean isNameBlacklisted(String name) {
		return blacklistedNames.contains(name);
	}

	public Group getGroupByName(String name) {
		return groups.stream().filter(group -> group.getGroup().equals(name)).findFirst().orElse(null);
	}
}

