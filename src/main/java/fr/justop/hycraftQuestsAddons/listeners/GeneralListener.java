package fr.justop.hycraftQuestsAddons.listeners;

import fr.justop.hycraftQuestsAddons.HycraftQuestsAddons;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.players.PlayerAccount;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import java.util.Objects;

public class GeneralListener implements Listener {

    @EventHandler
    public void onPlayerDismount(VehicleExitEvent event) {
        if (event.getExited() instanceof Player player && event.getVehicle() instanceof Horse horse) {
            if(horse.getCustomName() == null) return;
            if(!(horse.getCustomName().equalsIgnoreCase("Nicolas"))) return;
            player.sendMessage(HycraftQuestsAddons.PREFIX + "\u00a7cVous ne pouvez pas descendre de votre cheval pendant la course !");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        QuestsAPI questsAPI = HycraftQuestsAddons.getQuestsAPI();
        PlayerAccount acc = questsAPI.getPlugin().getPlayersManager().getAccount(player);
        if(!(acc.getQuestDatas(Objects.requireNonNull(questsAPI.getQuestsManager().getQuest(132))).getStage() == 1)) return;

        acc.getQuestDatas(Objects.requireNonNull(questsAPI.getQuestsManager().getQuest(132))).setStage(0);
    }
}
