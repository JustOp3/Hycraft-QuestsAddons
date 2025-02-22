package fr.justop.hycraftQuestsAddons.listeners;

import fr.justop.hycraftQuestsAddons.HycraftQuestsAddons;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class FishingListener implements Listener {

	@EventHandler
	public void onPlayerFish(PlayerFishEvent event) {
		if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH && event.getCaught() instanceof Item caughtItem) {
			Bukkit.getScheduler().runTaskLater(HycraftQuestsAddons.getInstance(), () -> {
				ItemStack item = caughtItem.getItemStack();
				event.getPlayer().sendMessage("a");
				if (item.hasItemMeta() && item.hasItemMeta()) {
					String customName = item.getItemMeta().toString();
					event.getPlayer().sendMessage("Tu as pêché : " + customName);
				} else {
					event.getPlayer().sendMessage("Tu as pêché un poisson sans nom personnalisé.");
				}
			}, 2L);
		}
	}
}

