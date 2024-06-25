package me.sk8ingduck.nick.util;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;

public class Group implements ConfigurationSerializable {

	private final String group;
	private final int probability;
	private final String permission;

	public Group(String group, int probability, String permission) {
		this.group = group;
		this.probability = probability;
		this.permission = permission;
	}

	public Group(Map<String, Object> map) {
		group = ((String) map.get("group"));
		String probabilityString = (String) map.get("probability");
		probability = Integer.parseInt(probabilityString.substring(0, probabilityString.length() - 1));
		permission = ((String) map.get("permission"));
	}


	public String getGroup() {
		return group;
	}

	public int getProbability() {
		return probability;
	}

	public String getPermission() {
		return permission;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> saveMap = new HashMap<>();
		saveMap.put("group", group);
		saveMap.put("probability", probability + "%");
		saveMap.put("permission", permission);
		return saveMap;
	}
}
