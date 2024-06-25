package me.sk8ingduck.nick.sql;

import me.sk8ingduck.nick.util.Nickname;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public interface Database {

	Optional<Nickname> getNick(UUID uuid);
	void getNickAsync(UUID uuid, Consumer<Optional<Nickname>> callback);

	void setNick(UUID uuid, Nickname nickname);
	void setNickAsync(UUID uuid, Nickname nickname);

	void removeNick(UUID uuid);
	void removeNickAsync(UUID uuid);
}
