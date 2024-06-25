package me.sk8ingduck.nick.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.sk8ingduck.nick.Nick;
import me.sk8ingduck.nick.util.Nickname;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MySQL implements Database {

	private final HikariDataSource dataSource;
	private final ExecutorService pool = Executors.newCachedThreadPool();

	public MySQL(String host, int port, String username, String password, String database) {

		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true");
		config.setUsername(username);
		config.setPassword(password);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
		dataSource = new HikariDataSource(config);

		try (Connection connection = dataSource.getConnection()) {
			Statement stmt = connection.createStatement();
			stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + database);
			stmt.executeUpdate("USE " + database);
			stmt.executeUpdate("CREATE TABLE IF NOT EXISTS nick(" +
					"UUID VARCHAR(40), " +
					"groupName VARCHAR(40), " +
					"name VARCHAR(40), " +
					"PRIMARY KEY(UUID));");
			stmt.close();
		} catch (SQLException e) {
			Nick.getInstance().getServer().getConsoleSender()
					.sendMessage("§c[Nick] MySQL Connection could not be established. Error:");
			e.printStackTrace();
		}
	}

	public void close() {
		dataSource.close();
	}

	public Optional<Nickname> getNick(UUID uuid) {
		try (Connection connection = dataSource.getConnection()) {
			PreparedStatement stmt = connection.prepareStatement("SELECT * FROM nick WHERE UUID = ?;");
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
		try (Connection connection = dataSource.getConnection()) {
			PreparedStatement stmt = connection.prepareStatement("REPLACE INTO nick(UUID, groupName, name) " +
					"VALUES (?, ?, ?);");
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
		try (Connection connection = dataSource.getConnection()) {
			PreparedStatement stmt = connection.prepareStatement("DELETE FROM nick WHERE UUID = ?;");
			stmt.setString(1, uuid.toString());
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void removeNickAsync(UUID uuid) {
		pool.execute(() -> removeNick(uuid));
	}
}
