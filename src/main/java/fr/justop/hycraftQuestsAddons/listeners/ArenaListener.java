package fr.justop.hycraftQuestsAddons.listeners;

import fr.justop.hycraftQuestsAddons.HycraftQuestsAddons;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.players.PlayerAccount;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.UUID;

public class ArenaListener implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getKiller() != null) {
            Player player = entity.getKiller();
            if (HycraftQuestsAddons.getInstance().getActivePlayers().containsKey(player.getUniqueId())) {
                event.getDrops().clear();
                player.getInventory().setItem(1, null);
                player.getInventory().setItem(2, null);
                int remaining = HycraftQuestsAddons.getInstance().getRemainingMobs().get(player.getUniqueId()) - 1;
                HycraftQuestsAddons.getInstance().getRemainingMobs().put(player.getUniqueId(), remaining);
                HycraftQuestsAddons.getInstance().getMobsKilled().put(player.getUniqueId(), HycraftQuestsAddons.getInstance().getMobsKilled().getOrDefault(player.getUniqueId(), 0) + 1);
                updateBossBar(player);

                if (HycraftQuestsAddons.getInstance().getRemainingMobs().get(player.getUniqueId()) <= 0) {
                    player.sendMessage(HycraftQuestsAddons.PREFIX + "§aVous êtes parvenu à vaincre toutes les vagues de Plantes Mutantes! Téléportation dans 5 secondes...");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    launchFireworks(player);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            endArenaChallenge(player);
                        }
                    }.runTaskLater(HycraftQuestsAddons.getInstance(), 100);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (HycraftQuestsAddons.getInstance().getActivePlayers().containsKey(player.getUniqueId()) && (player.getHealth() - event.getFinalDamage()) <= 0) {
                event.setCancelled(true);
                player.setHealth(20.0);
                cancelArenaChallenge(player);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        if(HycraftQuestsAddons.getInstance().getActivePlayers().containsKey(player.getUniqueId()))
        {
            cancelArenaChallenge(player);
        }
    }

    private void cancelArenaChallenge(Player player)
    {
        HycraftQuestsAddons.getInstance().getRemainingMobs().remove(player.getUniqueId());
        HycraftQuestsAddons.getInstance().getMobsKilled().remove(player.getUniqueId());
        if (HycraftQuestsAddons.getInstance().getBossBars().containsKey(player.getUniqueId())) {
            HycraftQuestsAddons.getInstance().getBossBars().get(player.getUniqueId()).removeAll();
            HycraftQuestsAddons.getInstance().getBossBars().remove(player.getUniqueId());
        }
        HycraftQuestsAddons.getInstance().restoreInventory(player);
        removeNearbyEntities(player);

        if (HycraftQuestsAddons.getInstance().getActiveTasks().containsKey(player.getUniqueId())) {
            HycraftQuestsAddons.getInstance().getActiveTasks().get(player.getUniqueId()).cancel();
            HycraftQuestsAddons.getInstance().getActiveTasks().remove(player.getUniqueId());
        }

        player.teleport(new Location(Bukkit.getWorld("Prehistoire"), -44.5 , -18, -293.5, 180.0f, 0.0f));
        HycraftQuestsAddons.getInstance().getActivePlayers().remove(player.getUniqueId());

        QuestsAPI questsAPI = HycraftQuestsAddons.getQuestsAPI();
        PlayerAccount acc = questsAPI.getPlugin().getPlayersManager().getAccount(player);

        acc.getQuestDatas(Objects.requireNonNull(questsAPI.getQuestsManager().getQuest(118))).setStage(7);
        player.sendMessage(HycraftQuestsAddons.PREFIX + "§cTu as échoué! Tache d'être plus agile au prochain essai!");
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);

    }

    private void removeNearbyEntities(Player player) {
        World world = player.getWorld();
        world.getEntities().stream()
                .filter(entity -> entity.getLocation().distance(player.getLocation()) <= 100 && !(entity instanceof Player))
                .forEach(Entity::remove);
    }


    private void endArenaChallenge(Player player) {
        HycraftQuestsAddons.getInstance().restoreInventory(player);
        player.teleport(new Location(Bukkit.getWorld("Prehistoire"), -44.5 , -18, -293.5, 180.0f, 0.0f));
        HycraftQuestsAddons.getInstance().getActivePlayers().remove(player.getUniqueId());
        UUID playerId = player.getUniqueId();
        if (HycraftQuestsAddons.getInstance().getBossBars().containsKey(playerId)) {
            HycraftQuestsAddons.getInstance().getBossBars().get(playerId).removeAll();
            HycraftQuestsAddons.getInstance().getBossBars().remove(playerId);
        }

        if (HycraftQuestsAddons.getInstance().getActiveTasks().containsKey(player.getUniqueId())) {
            HycraftQuestsAddons.getInstance().getActiveTasks().get(player.getUniqueId()).cancel();
            HycraftQuestsAddons.getInstance().getActiveTasks().remove(player.getUniqueId());
        }

        player.sendMessage(HycraftQuestsAddons.PREFIX + "§aParlez à Donovan");
        player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.0f, 1.0f);
    }

    private void updateBossBar(Player player) {
        UUID playerId = player.getUniqueId();
        BossBar bossBar = HycraftQuestsAddons.getInstance().getBossBars().get(playerId);
        if (bossBar != null) {
            int killed = HycraftQuestsAddons.getInstance().getMobsKilled().getOrDefault(playerId, 0);
            int total = HycraftQuestsAddons.getInstance().getRemainingMobs().getOrDefault(playerId, 0) + killed;
            bossBar.setTitle("§eProgression: " + killed + "/" + total);
            if (total > 0) {
                bossBar.setProgress(Math.max(0.01, (double) killed / total));
            } else {
                bossBar.setProgress(1.0);
            }
            bossBar.setColor(BarColor.YELLOW);
        }

    }

    private void launchFireworks(Player player) {
        new BukkitRunnable() {
            int count = 5;
            @Override
            public void run() {
                if (count <= 0) {
                    cancel();
                    return;
                }
                Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.setPower(1);
                firework.setFireworkMeta(meta);
                count--;
            }
        }.runTaskTimer(HycraftQuestsAddons.getInstance(), 0, 20);
    }


}
