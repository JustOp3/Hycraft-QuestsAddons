package fr.justop.hycraftQuestsAddons.commands;

import fr.justop.hycraftQuestsAddons.HycraftQuestsAddons;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CommandAddon implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
		if(commandSender instanceof Player) return false;
		if(command.getName().equals("registerPlayerGoats"))
		{
			Player player = Bukkit.getPlayer(args[0]);
			HycraftQuestsAddons.getInstance().getGoatsCounter().put(player.getUniqueId(), new ArrayList<>());
			return true;
		}
		return false;
	}
}
