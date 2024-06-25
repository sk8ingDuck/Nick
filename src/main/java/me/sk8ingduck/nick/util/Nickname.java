package me.sk8ingduck.nick.util;

public class Nickname {

	private final String group;
	private final String name;

	public Nickname(String group, String name) {
		this.group = group;
		this.name = name;
	}

	public String getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}
}
