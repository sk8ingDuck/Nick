package me.sk8ingduck.nick.sql;

import me.sk8ingduck.nick.Nick;
import me.sk8ingduck.nick.util.Nickname;
import org.bukkit.Bukkit;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SQLite implements Database {


	private final ExecutorService pool = Executors.newCachedThreadPool();
	private Connection connection;

	public SQLite(String fileName, Path path) {
		try {
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}

			File databaseFile = path.resolve(fileName).toFile();
			this.connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile.getAbsolutePath());

			try (Statement stmt = connection.createStatement()) {
				stmt.executeUpdate("CREATE TABLE IF NOT EXISTS nick(" +
						"UUID VARCHAR(40), " +
						"groupName VARCHAR(40), " +
						"name VARCHAR(40), " +
						"PRIMARY KEY(UUID));");
				stmt.close();
			} catch (SQLException e) {
				Nick.getInstance().getServer().getConsoleSender()
						.sendMessage("§c[Nick] SQLite could not create table. Error:");
				e.printStackTrace();
			}
		} catch (Exception e) {
			Bukkit.getServer().getLogger()
					.info("§c[Nick] SQLite Connection could not be established. Error:");
			e.printStackTrace();
		}
	}

	public Optional<Nickname> getNick(UUID uuid) {
		try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM nick WHERE UUID = ?;")) {
			stmt.setString(1, uuid.toString());
			try (ResultSet resultSet = stmt.executeQuery()) {
				if (resultSet.next()) {
					return Optional.of(new Nickname(
							resultSet.getString("groupName"),
							resultSet.getString("name")));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public void getNickAsync(UUID uuid, Consumer<Optional<Nickname>> nickname) {
		pool.execute(() -> nickname.accept(getNick(uuid)));
	}

	public void setNick(UUID uuid, Nickname nick) {
		try (PreparedStatement stmt = connection.prepareStatement("REPLACE INTO nick(UUID, groupName, name) " +
				"VALUES (?, ?, ?);")) {
			stmt.setString(1, uuid.toString());
			stmt.setString(2, nick.getGroup());
			stmt.setString(3, nick.getName());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setNickAsync(UUID uuid, Nickname nick) {
		pool.execute(() -> setNick(uuid, nick));
	}

	public void removeNick(UUID uuid) {
		try (PreparedStatement stmt = connection.prepareStatement("DELETE FROM nick WHERE UUID = ?;")) {
			stmt.setString(1, uuid.toString());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void removeNickAsync(UUID uuid) {
		pool.execute(() -> removeNick(uuid));
	}
}
