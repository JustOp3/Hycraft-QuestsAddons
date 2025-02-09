package fr.justop.hycraftQuestsAddons.listeners;

import fr.justop.hycraftQuestsAddons.HycraftQuestsAddons;
import fr.justop.hycraftQuestsAddons.objects.CuboidRegion;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.players.PlayerAccount;
import fr.skytasul.quests.api.quests.Quest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class DropperListener implements Listener
{
    @EventHandler
    public void onMove(PlayerMoveEvent event){

        Player player = event.getPlayer();
        QuestsAPI questsAPI = HycraftQuestsAddons.getQuestsAPI();
        PlayerAccount acc = questsAPI.getPlugin().getPlayersManager().getAccount(player);

        Quest quest = questsAPI.getQuestsManager().getQuest(102);
        assert quest != null;

        if(!(acc.hasQuestDatas(quest)))
        {
            Location playerLocation = player.getLocation();
            for (Location triggerLocation : HycraftQuestsAddons.getInstance().getTriggerLocations()) {
                if (isPassingThroughTrigger(playerLocation, triggerLocation)) {
                    triggerEvent(player);
                    break;
                }
            }
        }


    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        if (to == null) return;

        for (String name : HycraftQuestsAddons.getInstance().getRegions().keySet()) {
            if (HycraftQuestsAddons.getInstance().getRegions().get("TeleportRegion").isInside(to)) {
                CuboidRegion region = HycraftQuestsAddons.getInstance().getRegions().get("TeleportRegion");
                if (to.getBlock().getType() == Material.WATER && HycraftQuestsAddons.getInstance().getPlayerConstraints().get(player.getUniqueId())) {
                    HycraftQuestsAddons.getInstance().getPlayerConstraints().put(player.getUniqueId(), false);
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                    player.sendMessage(HycraftQuestsAddons.PREFIX + "§aFélicitation! Vous êtes parvenu à atteindre le fond de la crevasse sans toucher les parois!");
                }
                if(event.getFrom().getY() > to.getY() && player.isOnGround() && HycraftQuestsAddons.getInstance().getPlayerConstraints().containsKey(player.getUniqueId()))
                {
                    if(region.getTeleportLocation() != null){
                        player.teleport(region.getTeleportLocation());
                        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
                        player.sendMessage(HycraftQuestsAddons.PREFIX + "§cVous avez échoué! Veuillez recommencer.");
                    }
                }
                return;
            }
            if(HycraftQuestsAddons.getInstance().getRegions().get("ResetRegion").isInside(to))
            {
                HycraftQuestsAddons.getInstance().getPlayerConstraints().put(player.getUniqueId(), true);
            }

        }
    }

    private boolean isPassingThroughTrigger(Location playerLocation, Location triggerLocation) {
        int playerX = (int)Math.floor(playerLocation.getX());
        int playerY = (int)Math.floor(playerLocation.getY());
        int playerZ = (int)Math.floor(playerLocation.getZ());
        return (playerLocation.getWorld().equals(triggerLocation.getWorld()) && playerX == triggerLocation
                .getBlockX() && playerZ == triggerLocation
                .getBlockZ() && playerY == triggerLocation
                .getBlockY());
    }

    private void triggerEvent(Player player) {

        player.teleport(new Location(player.getWorld(), 386.0D, 91.0D, 392.0D, -45.0F, 0.0F));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
        player.sendMessage(HycraftQuestsAddons.PREFIX + "§cVous n'avez pas accès à cette zone pour le moment! Continuez votre aventure au fil de l'histoire, décrochez le rang \uA4C7 et commencez la quête '§bUne crevasse scintillante' pour y accéder. Utilise §b/rang §cpour plus d'informations.");
    }
}
