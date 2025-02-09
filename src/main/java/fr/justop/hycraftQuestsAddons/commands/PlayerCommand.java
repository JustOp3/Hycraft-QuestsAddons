package fr.justop.hycraftQuestsAddons.commands;

import fr.justop.hycraftQuestsAddons.HycraftQuestsAddons;
import fr.justop.hycraftQuestsAddons.listeners.ComposterLaunch;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.players.PlayerAccount;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlayerCommand implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
		if(!(commandSender instanceof Player player)) return false;
		HycraftQuestsAddons instance = HycraftQuestsAddons.getInstance();
		switch(strings[0]){
			case "interrupt":
				if (instance.getPhase1().containsKey(player.getUniqueId()))
				{
					instance.getPhase1().put(player.getUniqueId(), "inactive");
					player.setGameMode(GameMode.SURVIVAL);
					player.teleport(new Location(Bukkit.getWorld("Prehistoire"), -270, 13, 200, -90f, -15));
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§eVous avez interrompu la quête §b§lUne descente furtive§r§e. Utilisez §6/quete rejoin §e à tout moment pour reprendre la où vous en étiez");

				}else if(instance.getPhase2().containsKey(player.getUniqueId())) {

					instance.getPhase2().put(player.getUniqueId(), "inactive");
					player.setGameMode(GameMode.SURVIVAL);
					player.teleport(new Location(Bukkit.getWorld("Prehistoire"), -270, 13, 200, -90f, -15));
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§eVous avez interrompu la quête §b§lUne descente furtive§r§e. Utilisez §6/quete rejoin §e à tout moment pour reprendre la où vous en étiez");
				}
				break;

			case "rejoin":
				if (instance.getPhase1().containsKey(player.getUniqueId()))
				{
					QuestsAPI questsAPI = HycraftQuestsAddons.getQuestsAPI();
					PlayerAccount acc = questsAPI.getPlugin().getPlayersManager().getAccount(player);

					acc.getQuestDatas(Objects.requireNonNull(questsAPI.getQuestsManager().getQuest(115))).setStage(1);
					instance.getPhase1().put(player.getUniqueId(), "active");
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§eVous avez rejoint la quête avec succès la où vous en étiez.");
					player.teleport(ComposterLaunch.TARGET_LOCATION);
					player.setGameMode(GameMode.ADVENTURE);
					ComposterLaunch.slowFall(player);

				}else if(instance.getPhase2().containsKey(player.getUniqueId()))
				{
					instance.getPhase2().put(player.getUniqueId(), "active");
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§eVous avez rejoint la quête avec succès la où vous en étiez. Xandros ayant perdu le contact avec vous, vous devez trouver un moyen par vous même pour échapper au diplodocus.");
					player.teleport(new Location(Bukkit.getWorld("Prehistoire"), -35, 215, 207, 90f, 0f));
					player.setGameMode(GameMode.ADVENTURE);
				}




		}

		return false;
	}
}
