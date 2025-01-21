package fr.justop.hycraftQuestsAddons.listeners;

import fr.justop.hycraftQuestsAddons.HycraftQuestsAddons;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class DiploListener implements Listener
{
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		if(!HycraftQuestsAddons.getInstance().getPhase2().containsKey(player.getUniqueId())) return;
		if(!HycraftQuestsAddons.getInstance().getPhase2().get(player.getUniqueId()).equals("active")) return;
		if (event.getFrom().getBlockX() != event.getTo().getBlockX() ||
				event.getFrom().getBlockY() != event.getTo().getBlockY() ||
				event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {

			Location belowPlayer = event.getTo().clone().subtract(0, 1, 0);
			Material blockType = belowPlayer.getBlock().getType();

			if (!HycraftQuestsAddons.getInstance().getAllowedBlocks().contains(blockType)) {
				player.teleport(new Location(Bukkit.getWorld("Prehistoire"), 0, 0, 0, 0f, 0f));
				player.sendMessage(HycraftQuestsAddons.PREFIX + "§cVous avez attisé la colère du diplodocus. Recommencez et têchez d'être plus discret!");
			}
		}
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (HycraftQuestsAddons.getInstance().getPhase2().containsKey(player.getUniqueId()) || HycraftQuestsAddons.getInstance().getPhase1().containsKey(player.getUniqueId())) {
			event.setCancelled(true);
			player.sendMessage(HycraftQuestsAddons.PREFIX + "§eVous ne pouvez pas executer de commande tant que la quête est en cours! Utilisez §6/quete interrupt §cpour interrompre la quête, vous pourrez la rejoindre plus tard.");
		}
	}
}
