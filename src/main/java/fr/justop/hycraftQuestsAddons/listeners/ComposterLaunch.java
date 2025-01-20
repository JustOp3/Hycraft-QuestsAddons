package fr.justop.hycraftQuestsAddons.listeners;

import fr.justop.hycraftQuestsAddons.HycraftQuestsAddons;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.players.PlayerAccount;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ComposterLaunch implements Listener {

	private final int LAUNCH_HEIGHT = 200;
	private int indice = 0;
	private final Location TARGET_LOCATION = new Location(Bukkit.getWorld("prehistoire"), -34.5, 256, 207.5, 90f, 40f);
	List<String> xandrosDialog1 = Arrays.asList("§6§lXandros §r§e: Hahaha! Tu vois ça, ma vieille bricole fonctionne toujours à merveille!", "§e§lXandros §r§e: Ok, j'ai oublié de te préciser un petit détail... Le diplodocus est très sensible sur certaines parties de sa peau.",
			"§6§lXandros §r§e: Seuls les blocs les plus durs sauront tromper ses sensation, c'est le cas de §6§lla concrete bleue §r§eou bien du §6§lcorail§r§e!",
			"§6§lXandros §r§e: Si tu a le malheur de marcher sur le reste de sa peau, sa colère risque s'être terrible",
			"§6§lXandros §r§e: Bon ceci dit, à toi de jouer, va récupérer la corne et je ensuite je te ramènerai à moi.");

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String message = event.getMessage();

		if (message.equalsIgnoreCase("Kaboum!")) {
			Block block = player.getLocation().getBlock();
			QuestsAPI questsAPI = HycraftQuestsAddons.getQuestsAPI();
			PlayerAccount acc = questsAPI.getPlugin().getPlayersManager().getAccount(player);

			if (!(acc.getQuestDatas(Objects.requireNonNull(questsAPI.getQuestsManager().getQuest(115))).getStage() == 1)) return;

			if (block.getType() == Material.COMPOSTER) {
				event.setCancelled(true);

				Bukkit.getScheduler().runTask(HycraftQuestsAddons.getInstance(), () -> {
					Location playerLoc = player.getLocation().add(0, 1, 0);
					World world = player.getWorld();
					for (int x = -1; x <= 1; x++) {
						for (int z = -1; z <= 1; z++) {
							if (x == 0 && z == 0) continue;
							world.getBlockAt(playerLoc.clone().add(x, 0, z)).setType(Material.BARRIER);
							world.getBlockAt(playerLoc.clone().add(0, 1, 0)).setType(Material.BARRIER);
						}
					}
				});

				new BukkitRunnable() {
					int countdown = 3;

					@Override
					public void run() {
						if (countdown > 0) {
							String color = countdown == 1 ? "\u00a7c" : "\u00a7e";
							player.sendTitle(color + "" + countdown, "", 0, 20, 0);
							player.sendMessage(HycraftQuestsAddons.PREFIX + "§aLancement dans §e" + countdown + "§a...");
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f + (3 - countdown) * 0.2f);
							countdown--;

						} else {
							this.cancel();

							Bukkit.getScheduler().runTask(HycraftQuestsAddons.getInstance(), () -> {
								Location playerLoc = player.getLocation().add(0, 1, 0);
								World world1 = player.getWorld();
								for (int x = -1; x <= 1; x++) {
									for (int z = -1; z <= 1; z++) {
										if (x == 0 && z == 0) continue;
										Block barrierBlock = world1.getBlockAt(playerLoc.clone().add(x, 0, z));
										if (barrierBlock.getType() == Material.BARRIER) {
											barrierBlock.setType(Material.AIR);
											world1.getBlockAt(playerLoc.clone().add(0, 1, 0)).setType(Material.AIR);
										}
									}
								}
							});
							player.sendMessage("§6§lXandros §r§e: C'est parti mon kiki, on se reparle la haut!");
							launchPlayer(player);
						}
					}
				}.runTaskTimer(HycraftQuestsAddons.getInstance(), 0, 20);
			}
		}
	}

	private void launchPlayer(Player player) {
		World world = player.getWorld();
		World temporaryWorld = Bukkit.getWorld("mondesurvie");
		player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1.0f, 1.0f);

		new BukkitRunnable() {
			double velocity = 5.0;
			double height = 0;

			@Override
			public void run() {
				if (height < LAUNCH_HEIGHT) {
					velocity *= 0.97;
					player.setVelocity(new Vector(0, velocity, 0));
					world.spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation(), 10, 0.5, 0.5, 0.5, 0.1);
					height = player.getLocation().getY();
				} else {
					this.cancel();

					new BukkitRunnable() {
						@Override
						public void run() {
							if (temporaryWorld != null) {
								player.teleport(new Location(temporaryWorld, 0, 100, 0));
							}

							new BukkitRunnable() {
								@Override
								public void run() {
									player.teleport(TARGET_LOCATION);
									player.setFlying(false);
									slowFall(player);
								}
							}.runTaskLater(HycraftQuestsAddons.getInstance(), 40);
						}
					}.runTaskLater(HycraftQuestsAddons.getInstance(), 20);
				}
			}
		}.runTaskTimer(HycraftQuestsAddons.getInstance(), 0, 2);
	}

	private void slowFall(Player player)
	{
		new BukkitRunnable() {
			double velocity = -0.1;

			@Override
			public void run() {
				if (player.getLocation().getY() > TARGET_LOCATION.getY() - 41) { // Stop fall near the ground
					player.setVelocity(new Vector(0, velocity, 0));

					if (player.getTicksLived() % 90 == 0) {
						player.sendMessage(xandrosDialog1.get(indice));
						player.sendMessage("");
						if(indice < 4) indice++;
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
					}
				} else {
					this.cancel();
					indice = 0;
				}
			}
		}.runTaskTimer(HycraftQuestsAddons.getInstance(), 0, 1);
	}
}

