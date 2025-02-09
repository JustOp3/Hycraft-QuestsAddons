package fr.justop.hycraftQuestsAddons.listeners;

import fr.justop.hycraftQuestsAddons.HycraftQuestsAddons;
import fr.justop.hycraftQuestsAddons.objects.CuboidRegion;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.players.PlayerAccount;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Objects;

public class DiploListener implements Listener
{
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		QuestsAPI questsAPI = HycraftQuestsAddons.getQuestsAPI();
		PlayerAccount acc = questsAPI.getPlugin().getPlayersManager().getAccount(player);

		CuboidRegion diploRegion = HycraftQuestsAddons.getInstance().getRegions().get("DiploOpenRegion");
		if (diploRegion != null && diploRegion.isInside(event.getTo())) {
			if (HycraftQuestsAddons.getInstance().getPhase1().containsKey(player.getUniqueId())) {
				event.setCancelled(true);
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
				player.sendMessage(HycraftQuestsAddons.PREFIX + "§cTu n'as pas accès à cette zone pour le moment!");
			}
		}

		if(!HycraftQuestsAddons.getInstance().getPhase2().containsKey(player.getUniqueId())) return;
		if(!HycraftQuestsAddons.getInstance().getPhase2().get(player.getUniqueId()).equals("active") || !(acc.getQuestDatas(Objects.requireNonNull(questsAPI.getQuestsManager().getQuest(115))).getStage() == 4)) return;

		if (player.getVelocity().getY() > 0) {
			return;
		}

		if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
				event.getFrom().getBlockY() != event.getTo().getBlockY() ||
				event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {

			Location belowPlayer = event.getTo().clone().subtract(0, 1, 0);
			Material blockType = belowPlayer.getBlock().getType();
			player.sendMessage();

			if (!HycraftQuestsAddons.getInstance().getAllowedBlocks().contains(blockType)) {
				player.teleport(new Location(Bukkit.getWorld("Prehistoire"), -53, 219, 207, 90f, 0f));
				player.sendMessage(HycraftQuestsAddons.PREFIX + "§cVous avez attisé la colère du diplodocus. Recommencez et têchez d'être plus discret!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
			}
		}
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if(event.getMessage().equalsIgnoreCase("/q interrupt") || event.getMessage().equalsIgnoreCase("/q rejoin")) return;
		if (HycraftQuestsAddons.getInstance().getPhase2().containsKey(player.getUniqueId())) {
			if(HycraftQuestsAddons.getInstance().getPhase2().get(player.getUniqueId()) == "active")
			{
				event.setCancelled(true);
				player.sendMessage(HycraftQuestsAddons.PREFIX + "§eVous ne pouvez pas executer de commande tant que la quête est en cours! Utilisez §6/q interrupt §epour interrompre la quête, vous pourrez la rejoindre plus tard.");
				return;
			}

		}
		if (HycraftQuestsAddons.getInstance().getPhase1().containsKey(player.getUniqueId())) {
			if(HycraftQuestsAddons.getInstance().getPhase1().get(player.getUniqueId()) == "active")
			{
				event.setCancelled(true);
				player.sendMessage(HycraftQuestsAddons.PREFIX + "§eVous ne pouvez pas executer de commande tant que la quête est en cours! Utilisez §6/q interrupt §epour interrompre la quête, vous pourrez la rejoindre plus tard.");
			}

		}
	}
}
