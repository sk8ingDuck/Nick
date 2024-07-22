package me.sk8ingduck.nick.command;

import dev.iiahmed.disguise.UndisguiseResponse;
import me.sk8ingduck.nick.Nick;
import me.sk8ingduck.nick.config.MessagesConfig;
import me.sk8ingduck.nick.manager.NickManager;
import me.sk8ingduck.nick.sql.Database;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class UnnickCommand implements CommandExecutor {

    private final NickManager nickManager = Nick.getInstance().getNickManager();
    private final Database sql = Nick.getInstance().getSQL();
    private final HashMap<CommandSender, Long> cooldowns = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("unnick.self") && !commandSender.hasPermission("unnick.other")) {
            commandSender.sendMessage(Nick.getInstance().getMessagesConfig().get("nick.noperms"));
            return true;
        }

        if (strings.length == 1
                && strings[0].equalsIgnoreCase("help")) {
            commandSender.sendMessage(Nick.getInstance().getMessagesConfig().getNoPrefix("nick.help"));
            return true;
        }

        if (strings.length >= 1) {
            handleOtherPlayerUnnick(commandSender, strings[0]);
            return true;
        }

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Not a player.");
            return true;
        }

        Player player = (Player) commandSender;
        handleSelfUnnick(player);

        return true;
    }

    private void handleOtherPlayerUnnick(CommandSender commandSender, String targetName) {
        MessagesConfig messagesConfig = Nick.getInstance().getMessagesConfig();

        if (!commandSender.hasPermission("unnick.other")) {
            commandSender.sendMessage(messagesConfig.get("nick.noperms"));
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(targetName);
        if (targetPlayer == null) {
            commandSender.sendMessage(messagesConfig.get("unnick.other.notonline")
                    .replaceAll("%PLAYER%", targetName));
            return;
        }

        if (nickManager.getNickname(targetPlayer) != null) {
            unnickPlayer(commandSender, targetPlayer);
        } else {
            commandSender.sendMessage(messagesConfig.get("unnick.other.notnick")
                    .replaceAll("%PLAYER%", targetPlayer.getName()));
        }
    }

    private void handleSelfUnnick(Player player) {
        MessagesConfig messagesConfig = Nick.getInstance().getMessagesConfig();

        if (!player.hasPermission("unnick.self")) {
            player.sendMessage(messagesConfig.get("nick.noperms"));
            return;
        }

        if (nickManager.getNickname(player) != null) {
            unnickPlayer(player, player);
        } else {
            player.sendMessage(messagesConfig.get("unnick.notnick"));
        }
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