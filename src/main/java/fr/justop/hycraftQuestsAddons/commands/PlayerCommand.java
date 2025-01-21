package fr.justop.hycraftQuestsAddons.commands;

import fr.justop.hycraftQuestsAddons.HycraftQuestsAddons;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerCommand implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
		if(!(commandSender instanceof Player player)) return false;
		switch(strings[0]){

			case "interrupt":
				HycraftQuestsAddons instance = HycraftQuestsAddons.getInstance();
				if (instance.getPhase1().containsKey(player.getUniqueId()))
				{
					instance.getPhase1().put(player.getUniqueId(), "inactive");
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§eVous avez interrompu la quête §b§lUne descente furtive§r§e. Utilisez §6/quete rejoin §e à tout moment pour reprendre la où vous en étiez");
				}else if(instance.getPhase2().containsKey(player.getUniqueId())) {

					instance.getPhase2().put(player.getUniqueId(), "inactive");
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§eVous avez interrompu la quête §b§lUne descente furtive§r§e. Utilisez §6/quete rejoin §e à tout moment pour reprendre la où vous en étiez");
				}




		}

		return false;
	}
}
