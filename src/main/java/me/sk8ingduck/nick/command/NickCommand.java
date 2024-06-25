package me.sk8ingduck.nick.command;

import dev.iiahmed.disguise.DisguiseResponse;
import dev.iiahmed.disguise.UndisguiseResponse;
import me.sk8ingduck.nick.Nick;
import me.sk8ingduck.nick.config.MessagesConfig;
import me.sk8ingduck.nick.config.SettingsConfig;
import me.sk8ingduck.nick.manager.NickManager;
import me.sk8ingduck.nick.sql.Database;
import me.sk8ingduck.nick.util.Group;
import me.sk8ingduck.nick.util.Nickname;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class NickCommand implements CommandExecutor {

	private final NickManager nickManager = Nick.getInstance().getNickManager();
	private final Database sql = Nick.getInstance().getSQL();
	private final HashMap<CommandSender, Long> cooldowns = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
		if (strings.length == 1
				&& strings[0].equalsIgnoreCase("help")
				&& commandSender.hasPermission("nick.nick")) {
			commandSender.sendMessage(Nick.getInstance().getMessagesConfig().getNoPrefix("nick.help"));
			return true;
		}

		if (strings.length == 1
				&& strings[0].equalsIgnoreCase("reload")
				&& commandSender.hasPermission("nick.reload")) {
			Nick.getInstance().reloadConfigs();
			commandSender.sendMessage("§aConfigs reloaded.");
			return true;
		}

		if (strings.length == 2
				&& strings[0].equalsIgnoreCase("realname")
				&& commandSender.hasPermission("nick.realname")) {
			Player targetPlayer = Bukkit.getPlayer(strings[1]);
			String realName = nickManager.getRealName(targetPlayer);
			commandSender.sendMessage(Nick.getInstance().getMessagesConfig().get("nick.realname")
					.replaceAll("%NICKNAME%", strings[1])
					.replaceAll("%PLAYER%", realName == null ? strings[1] : realName));
			return true;
		}

		if (strings.length >= 1) {
			Player targetPlayer = Bukkit.getPlayer(strings[0]);

			if (targetPlayer != null && commandSender.hasPermission("nick.other")) {
				handleOtherPlayerNick(commandSender, targetPlayer, strings);
				return true;
			}
		}

		if (!(commandSender instanceof Player)) {
			commandSender.sendMessage("Not a player.");
			return true;
		}

		Player player = (Player) commandSender;
		handleSelfNick(player, strings);

		return true;
	}

	private void handleOtherPlayerNick(CommandSender commandSender, Player targetPlayer, String[] strings) {
		SettingsConfig settingsConfig = Nick.getInstance().getSettingsConfig();
		MessagesConfig messagesConfig = Nick.getInstance().getMessagesConfig();

		switch (strings.length) {
			case 1:
				// Syntax: /nick <otherPlayer>
				// Nick other player with random name and random group
				if (!commandSender.hasPermission("nick.other.nick")) {
					commandSender.sendMessage(messagesConfig.get("nick.noperms"));
					return;
				}

				if (nickManager.getNickname(targetPlayer) == null) {
					nickPlayer(commandSender, targetPlayer,
							settingsConfig.getRandomGroup(commandSender), settingsConfig.getRandomName());
				} else {
					unnickPlayer(commandSender, targetPlayer);
				}

				return;
			case 2:
				// Syntax: /nick <otherPlayer> <name/group>
				if (!commandSender.hasPermission("nick.other.custom.name")
						&& !commandSender.hasPermission("nick.other.custom.group")) {
					commandSender.sendMessage(messagesConfig.get("nick.noperms"));
					return;
				}

				if (commandSender.hasPermission("nick.other.custom.group")) {
					Group group = settingsConfig.getGroupByName(strings[1]);
					if (group != null) {
						if (group.getPermission() != null && !commandSender.hasPermission(group.getPermission())) {
							commandSender.sendMessage(messagesConfig.get("nick.noperms"));
							return;
						}
						nickPlayer(commandSender, targetPlayer, strings[1], settingsConfig.getRandomName());
						return;
					}
				}

				if (commandSender.hasPermission("nick.other.custom.name")) {
					nickPlayer(commandSender, targetPlayer, settingsConfig.getRandomGroup(commandSender), strings[1]);
				}
				return;
			case 3:
				// Syntax: /nick <otherPlayer> <group> <name>
				// Nick other player with given group and given name
				if (!commandSender.hasPermission("nick.other.custom.name")
						|| !commandSender.hasPermission("nick.other.custom.group")) {
					commandSender.sendMessage(messagesConfig.get("nick.noperms"));
					return;
				}

				nickPlayer(commandSender, targetPlayer, strings[1], strings[2]);
				return;
			default:
				commandSender.sendMessage(messagesConfig.getNoPrefix("nick.help"));
		}
	}

	private void handleSelfNick(Player player, String[] strings) {
		SettingsConfig settingsConfig = Nick.getInstance().getSettingsConfig();
		MessagesConfig messagesConfig = Nick.getInstance().getMessagesConfig();

		switch (strings.length) {
			case 0:
				// Syntax: /nick
				// Nick yourself with random group and random name, or unnick, if you're nicked
				if (!player.hasPermission("nick.nick")) {
					player.sendMessage(messagesConfig.get("nick.noperms"));
					return;
				}

				if (nickManager.getNickname(player) == null) {
					nickPlayer(player, player, settingsConfig.getRandomGroup(player), settingsConfig.getRandomName());
				} else {
					unnickPlayer(player, player);
				}

				return;
			case 1:
				// Syntax: /nick <name/group>
				if (!player.hasPermission("nick.custom.name")
						&& !player.hasPermission("nick.custom.group")) {
					player.sendMessage(messagesConfig.get("nick.noperms"));
					return;
				}

				if (player.hasPermission("nick.custom.group")) {
					Group group = settingsConfig.getGroupByName(strings[0]);
					if (group != null) {
						if (group.getPermission() != null && !player.hasPermission(group.getPermission())) {
							player.sendMessage(messagesConfig.get("nick.noperms"));
							return;
						}
						nickPlayer(player, player, strings[0], settingsConfig.getRandomName());
						return;
					}
				}

				if (player.hasPermission("nick.custom.name")) {
					nickPlayer(player, player, settingsConfig.getRandomGroup(player), strings[0]);
				}

				return;
			case 2:
				// Syntax: /nick <group> <name>
				// Nick yourself with given group and given name
				if (!player.hasPermission("nick.custom.name")
						|| !player.hasPermission("nick.custom.group")) {
					player.sendMessage(messagesConfig.get("nick.noperms"));
					return;
				}

				nickPlayer(player, player, strings[0], strings[1]);
				return;
			default:
				player.sendMessage(messagesConfig.getNoPrefix("nick.help"));
		}
	}


	/**
	 * Process nick command
	 *
	 * @param sender  the command sender (player or console)
	 * @param player  Player who will be nicked
	 * @param group   New group
	 * @param newNick New nickname
	 */
	private void nickPlayer(CommandSender sender, Player player, String group, String newNick) {
		if (isCooldownActive(sender)) return;

		MessagesConfig messagesConfig = Nick.getInstance().getMessagesConfig();
		if (Nick.getInstance().getSettingsConfig().isNameBlacklisted(newNick)
				&& !player.hasPermission("nick.bypassblacklist")) {
			sender.sendMessage(Nick.getInstance().getMessagesConfig().get("nick.nameblacklisted"));
			return;
		}
		Nickname nick = new Nickname(group, newNick);
		sql.setNick(player.getUniqueId(), nick);

		if (!Nick.getInstance().getSettingsConfig().shouldNickOnThisServer())
			return;

		DisguiseResponse response = nickManager.nickPlayer(player, nick);
		if (response != DisguiseResponse.SUCCESS) {
			sender.sendMessage(messagesConfig.get("nick.unsuccessful." + response.toString().toLowerCase()));
			return;
		}

		//delay the response otherwise prefix might be null
		Bukkit.getScheduler().scheduleSyncDelayedTask(Nick.getInstance(),
				() -> {
					player.sendMessage(messagesConfig.get("nick.successful.self")
							.replaceAll("%PREFIX%", nickManager.getFakePrefix(player))
							.replaceAll("%NAME%", newNick));

					if (!sender.getName().equalsIgnoreCase(player.getName())) {
						sender.sendMessage(Nick.getInstance().getMessagesConfig().get("nick.successful.other")
								.replaceAll("%PLAYER%", nickManager.getRealName(player))
								.replaceAll("%PREFIX%", nickManager.getFakePrefix(player))
								.replaceAll("%NAME%", newNick));
					}
				}, 10L);
	}


	/**
	 * Process unnick command for the player
	 *
	 * @param sender Command sender (player or console)
	 * @param player Player who will be unnicked
	 */
	private void unnickPlayer(CommandSender sender, Player player) {
		if (isCooldownActive(sender)) return;

		MessagesConfig messagesConfig = Nick.getInstance().getMessagesConfig();

		sql.removeNick(player.getUniqueId());
		UndisguiseResponse response = nickManager.unnickPlayer(player);

		if (response != UndisguiseResponse.SUCCESS) {
			sender.sendMessage(messagesConfig.get("unnick.unsuccessful." + response.toString().toLowerCase()));
			return;
		}

		player.sendMessage(messagesConfig.get("unnick.successful.self"));
		if (!sender.getName().equals(player.getName())) {
			sender.sendMessage(messagesConfig.get("unnick.successful.other")
					.replaceAll("%PLAYER%", player.getName()));
		}
	}

	private boolean isCooldownActive(CommandSender sender) {
		if (sender.hasPermission("nick.bypasscooldown")) return false;

		long cooldownTime = Nick.getInstance().getSettingsConfig().getNickCooldown();
		long currentTime = System.currentTimeMillis() / 1000L;

		if (cooldowns.containsKey(sender)) {
			long lastUsageTime = cooldowns.get(sender);
			long remainingTime = (lastUsageTime + cooldownTime) - currentTime;

			if (remainingTime > 0) {
				sender.sendMessage(Nick.getInstance().getMessagesConfig().get("nick.cooldown")
						.replaceAll("%REMAINING%", String.valueOf(remainingTime)));
				return true;
			}

			cooldowns.remove(sender);
		}
		cooldowns.put(sender, currentTime);
		return false;
	}
}
