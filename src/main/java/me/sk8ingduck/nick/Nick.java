package me.sk8ingduck.nick;

import dev.iiahmed.disguise.DisguiseManager;
import dev.iiahmed.disguise.DisguiseProvider;
import me.sk8ingduck.nick.command.NickCommand;
import me.sk8ingduck.nick.command.UnnickCommand;
import me.sk8ingduck.nick.config.DBConfig;
import me.sk8ingduck.nick.config.MessagesConfig;
import me.sk8ingduck.nick.config.SettingsConfig;
import me.sk8ingduck.nick.hook.PAPIExpansion;
import me.sk8ingduck.nick.listener.AsyncPlayerChatListener;
import me.sk8ingduck.nick.listener.PlayerInteractListener;
import me.sk8ingduck.nick.listener.PlayerJoinListener;
import me.sk8ingduck.nick.listener.PlayerQuitListener;
import me.sk8ingduck.nick.manager.NickManager;
import me.sk8ingduck.nick.manager.NickManagerNametagEdit;
import me.sk8ingduck.nick.manager.NickManagerNoAPI;
import me.sk8ingduck.nick.manager.NickManagerTAB;
import me.sk8ingduck.nick.sql.Database;
import me.sk8ingduck.nick.sql.MySQL;
import me.sk8ingduck.nick.sql.SQLite;
import me.sk8ingduck.nick.util.Group;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Paths;

import static org.bukkit.Bukkit.getPluginManager;

public final class Nick extends JavaPlugin {

	private static Nick instance;
	private NickManager nickManager;
	private Database sql;
	private SettingsConfig settingsConfig;
	private MessagesConfig messagesConfig;

	private final DisguiseProvider provider = DisguiseManager.getProvider();

	public static Nick getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		instance = this;

		// Entity disguises are enabled, but not used in this plugin.
		DisguiseManager.initialize(this, true);
		ConfigurationSerialization.registerClass(Group.class);

		DBConfig dbConfig = new DBConfig("database.yml", getDataFolder());
		sql = dbConfig.isMySQLEnabled()
				? new MySQL(dbConfig.getHost(), dbConfig.getPort(), dbConfig.getUsername(), dbConfig.getPassword(), dbConfig.getDatabase())
				: new SQLite("friendsystem.db", Paths.get(getDataFolder().getPath(), "database"));



		if (getPluginManager().isPluginEnabled("TAB")) {
			getServer().getConsoleSender().sendMessage("§a[Nick] TAB Plugin found! Initializing Nick with TAB...");
			if (!getPluginManager().isPluginEnabled("LuckPerms")) {
				getServer().getConsoleSender().sendMessage("§c[Nick] LuckPerms was not found! " +
						"Please install LuckPerms if you want to use Nick with TAB.");
				getPluginManager().disablePlugin(this);
				return;
			} else {
				nickManager = new NickManagerTAB();
			}
		} else if (getPluginManager().isPluginEnabled("NametagEdit")) {
			nickManager = new NickManagerNametagEdit();
			getServer().getConsoleSender().sendMessage("§a[Nick] NametagEdit Plugin found! " +
					"Initializing Nick with NametagEdit...");
		} else {
			getServer().getConsoleSender().sendMessage("§a[Nick] Initialzing Nick Plugin...");
			nickManager = new NickManagerNoAPI();
		}

		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			new PAPIExpansion().register();
		}

		settingsConfig = new SettingsConfig("settings.yml", getDataFolder());
		messagesConfig = new MessagesConfig("messages.yml", getDataFolder());

		getCommand("nick").setExecutor(new NickCommand());
		getCommand("unnick").setExecutor(new UnnickCommand());

		getPluginManager().registerEvents(new PlayerJoinListener(), this);
		getPluginManager().registerEvents(new PlayerQuitListener(), this);
		getPluginManager().registerEvents(new PlayerInteractListener(), this);
		getPluginManager().registerEvents(new AsyncPlayerChatListener(), this);
	}


	public NickManager getNickManager() {
		return nickManager;
	}

	public Database getSQL() {
		return sql;
	}

	public SettingsConfig getSettingsConfig() {
		return settingsConfig;
	}

	public MessagesConfig getMessagesConfig() {
		return messagesConfig;
	}

	public void reloadConfigs() {
		settingsConfig = new SettingsConfig("settings.yml", getDataFolder());
		messagesConfig = new MessagesConfig("messages.yml", getDataFolder());
	}

}
