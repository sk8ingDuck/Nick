package me.sk8ingduck.nick.config;

import java.io.File;
import java.util.LinkedHashMap;

public class MessagesConfig extends Config {

	private final LinkedHashMap<String, String> msgs;

	public MessagesConfig(String name, File path) {
		super(name, path);

		msgs = new LinkedHashMap<>();
		msgs.put("nick.prefix", "&5Nick &8> ");

		msgs.put("nick.help",
				"&8&m----------------------&r&e« Nick »&8&m----------------------\n\n"
						+ "&a/nick &7- Nick/unnick yourself (with random group and name)\n"
						+ "&a/nick <nick> &7- Nick yourself (with given nick)\n"
						+ "&a/nick <group> &7- Nick yourself (with given group)\n"
						+ "&a/nick <group> <nick> &7- Nick yourself (with given nick/group)\n\n"

						+ "&a/nick <player> &7- Nick other player (with random group/name)\n"
						+ "&a/nick <player> <nick> &7- Nick other player (with given nick)\n"
						+ "&a/nick <player> <group> &7- Nick other player (with given group)\n"
						+ "&a/nick <player> <group> <nick> &7- Nick other player (nick/group)\n\n"
						+ "&a/unnick &7- Unnick yourself\n"
						+ "&a/unnick <player> &7- Unnick other player\n\n"
						+ "&8&m----------------------&r&e« Nick »&8&m----------------------");


		msgs.put("nick.noperms", "&cYou don't have permission to do that.");
		msgs.put("nick.cooldown", "&cPlease wait another %REMAINING% Seconds before using this command.");
		msgs.put("nick.realname", "&a%NICKNAME%'s real name is %PLAYER%");
		msgs.put("nick.nameblacklisted", "&cThis name is blacklisted!");

		msgs.put("nick.successful.self", "&aNicked as %PREFIX%%NAME%");
		msgs.put("nick.successful.other", "&aNicked %PLAYER% as %PREFIX%%NAME%");
		msgs.put("nick.unsuccessful.fail_version_not_supported", "&cFailed to nick: unsupported MC version");
		msgs.put("nick.unsuccessful.fail_plugin_not_initialized", "&cFailed to nick: Plugin is either not initialized or disabled");
		msgs.put("nick.unsuccessful.fail_empty_disguise", "&cFailed to nick: empty skin / name");
		msgs.put("nick.unsuccessful.fail_name_invalid", "&cFailed to nick: invalid nickname");
		msgs.put("nick.unsuccessful.fail_name_already_online", "&cFailed to nick: nickname already online");
		msgs.put("nick.unsuccessful.fail_profile_not_found", "&cFailed to nick: reflections failed to get the player's GameProfile");
		msgs.put("nick.unsuccessful.fail_name_change_exception", "&cFailed to nick: reflections failed to change the player's GameProfile's name");

		msgs.put("unnick.successful.self", "&aUnnicked!");
		msgs.put("unnick.successful.other", "&aUnnicked %PLAYER%!");
		msgs.put("unnick.notnick", "&cYou are not nicked!");
		msgs.put("unnick.other.notnick", "&c%PLAYER% is not nicked!");
		msgs.put("unnick.other.notonline", "&c%PLAYER% is not online!");
		msgs.put("unnick.unsuccessful.fail_version_not_supported", "&cFailed to nick: unsupported MC version");
		msgs.put("unnick.unsuccessful.fail_already_undisguised", "&cFailed to nick: player not nicked");
		msgs.put("unnick.unsuccessful.fail_profile_not_found", "&cFailed to nick: reflections failed to get the player's GameProfile");
		msgs.put("unnick.unsuccessful.fail_name_change_exception", "&cFailed to nick: reflections failed to change the player's GameProfile's name");



		msgs.forEach((msgPath, message) -> msgs.put(msgPath, (String) getPathOrSet(msgPath, message)));
	}

	public String get(String path) {
		return msgs.get("nick.prefix") + msgs.get(path);
	}

	public String getNoPrefix(String path) {
		return msgs.get(path);
	}
}
