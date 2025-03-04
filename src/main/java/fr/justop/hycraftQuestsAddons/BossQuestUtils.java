package fr.justop.hycraftQuestsAddons;

import fr.justop.hycraftQuestsAddons.listeners.ArrowListener;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.players.PlayerAccount;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BossQuestUtils {

	public static void startBossFight(Player player) {
		Location arenaLocation = getAvailableArena(1);
		if (arenaLocation == null) {
			player.sendMessage("\u00a7cAucune arène disponible.");
			return;
		}

		Location bossSpawn = arenaLocation.clone();
		Location playerSpawn = bossSpawn.clone().add(0, 0, 9);
		playerSpawn.setYaw(180f);
		HycraftQuestsAddons.saveInventory(player);
		giveBossKit(player);
		player.setHealth(20.0);
		player.teleport(playerSpawn);
		player.removePotionEffect(PotionEffectType.CONFUSION);
		new BukkitRunnable() {
			int countdown = 5;

			@Override
			public void run() {
				if (countdown > 0) {
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§aDébut dans §e" + countdown + "§a secondes...");
					player.sendTitle("§e§l" + countdown, null);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
					countdown--;
				} else {
					MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob("Mutant_Flower").orElse(null);
					if (mob != null) {
						ActiveMob activeMob = mob.spawn(BukkitAdapter.adapt(bossSpawn), 1);
						arenaLocation.setWorld(Bukkit.getWorld("Challenge"));
						HycraftQuestsAddons.getInstance().getBossPlayers().put(player.getUniqueId(), HycraftQuestsAddons.getInstance().getArenaLocations().indexOf(arenaLocation));
						HycraftQuestsAddons.getInstance().getBosses().put(player.getUniqueId(), activeMob);
						HycraftQuestsAddons.getInstance().getBossPhase().put(player.getUniqueId(), 1);
					}
					startMobWaves(player, bossSpawn, 1);
					cancel();
				}
			}
		}.runTaskTimer(HycraftQuestsAddons.getInstance(), 0, 20);
	}

	public static Location getAvailableArena(int mode) {
		if (mode == 0) {
			int max = HycraftQuestsAddons.getInstance().getActivePlayers().isEmpty() ? -1 : Collections.max(HycraftQuestsAddons.getInstance().getActivePlayers().values());
			if (max <= 6) {
				Location loc = HycraftQuestsAddons.getInstance().getArenaLocations().get(max + 1);
				loc.setWorld(Bukkit.getWorld("Challenge"));
				return loc;
			}
		} else if (mode == 1) {
			int max = HycraftQuestsAddons.getInstance().getBossPlayers().isEmpty() ? -1 : Collections.max(HycraftQuestsAddons.getInstance().getBossPlayers().values());
			if (max <= 6) {
				Location loc = HycraftQuestsAddons.getInstance().getArenaLocations().get(max + 1);
				loc.setWorld(Bukkit.getWorld("BossFight1"));
				return loc;
			}
		}

		return null;
	}

	public static void startMobWaves(Player player, Location arenaLocation, int mode) {
		List<Location> mobSpawns = Arrays.asList(
				arenaLocation.clone().add(11, 0, 9),
				arenaLocation.clone().add(11, 0, -9),
				arenaLocation.clone().add(-10, 0, 9),
				arenaLocation.clone().add(-10, 0, -9)
		);


		BukkitRunnable waves = getWaves(player, mobSpawns, mode);
		int period = mode == 0 ? 20 * 20 : 60 * 20;
		HycraftQuestsAddons.getInstance().getActiveTasks().put(player.getUniqueId(), waves);
		waves.runTaskTimer(HycraftQuestsAddons.getInstance(), 0, period);
	}

	@NotNull
	public static BukkitRunnable getWaves(Player player, List<Location> mobSpawns, int mode) {
		return new BukkitRunnable() {
			int wave = 0;

			@Override
			public void run() {
				if (wave >= 3) {
					if (mode == 0) {
						return;
					}
				}

				if (wave == 0 && mode == 1) {
					wave++;
					return;
				}

				if (HycraftQuestsAddons.getInstance().getFrozenBosses().contains(HycraftQuestsAddons.getInstance().getBosses().get(player.getUniqueId())))
					return;

				if(mode == 1){
					int phase = HycraftQuestsAddons.getInstance().getBossPhase().get(player.getUniqueId());

					switch (phase){
						case 1:
							for (Location loc : mobSpawns) {
								MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob("Plante_mutante").orElse(null);
								if (mob != null) {
									mob.spawn(BukkitAdapter.adapt(loc), 1);
								}
							}
							break;

						case 2:
							for (Location loc : mobSpawns) {
								MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob("Golem_de_pierre").orElse(null);
								if (mob != null) {
									mob.spawn(BukkitAdapter.adapt(loc), 1);
								}
							}
							break;

						case 3:
							for (Location loc : mobSpawns) {
								MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob("Plante_mutante").orElse(null);
								MythicMob mob2 = MythicBukkit.inst().getMobManager().getMythicMob("Golem_de_pierre").orElse(null);
								MythicMob mob3 = MythicBukkit.inst().getMobManager().getMythicMob("Salamandre").orElse(null);
								if (mob != null && mob2 != null && mob3 != null) {
									mob.spawn(BukkitAdapter.adapt(loc), 1);
									mob2.spawn(BukkitAdapter.adapt(loc), 1);
									mob3.spawn(BukkitAdapter.adapt(loc), 1);
								}
							}
							break;
					}
				}
				int mobCount = 0;

				for (Location loc : mobSpawns) {
					MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob("Plante_mutante").orElse(null);
					if (mob != null) {
						mob.spawn(BukkitAdapter.adapt(loc), 1);
						mobCount++;
					}
				}

				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
				String msg = mode == 0 ? "§aLa vague §e" + (wave + 1) + "§a est apparue. §e(+4)" : "§aLe boss a fait apparaître des renforts!";
				player.sendMessage(HycraftQuestsAddons.PREFIX + msg);
				if (mode == 0)
					HycraftQuestsAddons.getInstance().getRemainingMobs().put(player.getUniqueId(), HycraftQuestsAddons.getInstance().getRemainingMobs().get(player.getUniqueId()) + mobCount);
				wave++;
			}
		};
	}

	public static void giveBossKit(Player player) {
		ItemStack ironHelmet = new ItemStack(Material.NETHERITE_HELMET);
		ItemStack ironChestplate = new ItemStack(Material.NETHERITE_CHESTPLATE);
		ItemStack ironLeggings = new ItemStack(Material.NETHERITE_LEGGINGS);
		ItemStack ironBoots = new ItemStack(Material.NETHERITE_BOOTS);

		ironHelmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		ironChestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		ironLeggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
		ironBoots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);

		player.getInventory().setArmorContents(new ItemStack[]{ironBoots, ironLeggings, ironChestplate, ironHelmet});

		Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"o give " + player.getName() +" epee_chronite_t3");
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"o give " + player.getName() +" arc_chronite_t3");

		ItemStack goldenApples = new ItemStack(Material.GOLDEN_APPLE, 15);

		player.getInventory().setItem(8, goldenApples);
		player.getInventory().setItem(9, new ItemStack(Material.ARROW));
	}

	public static void freezeBoss(ActiveMob boss, int duration) {
		if (boss instanceof LivingEntity livingEntity) {
			((LivingEntity) boss).setAI(false);

			livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration, 255));
			livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 255));
			HycraftQuestsAddons.getInstance().getFrozenBosses().add(boss);

			Bukkit.getScheduler().runTaskLater(HycraftQuestsAddons.getInstance(), () -> {
				((LivingEntity) boss).setAI(true);
				livingEntity.removePotionEffect(PotionEffectType.SLOW);
				livingEntity.removePotionEffect(PotionEffectType.WEAKNESS);
				HycraftQuestsAddons.getInstance().getFrozenBosses().remove(boss);
			}, duration);
		}
	}

	public static void invokeSpirit(Player player) {
		Location arenaLocation = HycraftQuestsAddons.getInstance().getArenaLocations().get(HycraftQuestsAddons.getInstance().getBossPlayers().get(player.getUniqueId()));
		arenaLocation.setWorld(Bukkit.getWorld("BossFight1"));

		Location cristalLocation = ArrowListener.getRandomCristal(arenaLocation);
		HycraftQuestsAddons.getInstance().getActiveCristalPos().put(player.getUniqueId(), cristalLocation);

		cristalLocation.getBlock().setType(Material.SHROOMLIGHT);
		startParticleTask(player, cristalLocation);
	}

	public static void cancelBossChallenge(Player player)
	{
		HycraftQuestsAddons.getInstance().getSpiritPlayers().remove(player.getUniqueId());
		HycraftQuestsAddons.getInstance().getBossPhase().remove(player.getUniqueId());

		ActiveMob boss = HycraftQuestsAddons.getInstance().getBosses().get(player.getUniqueId());
		boss.remove();
		HycraftQuestsAddons.getInstance().getBosses().remove(player.getUniqueId());
		HycraftQuestsAddons.getInstance().getFrozenBosses().remove(boss);

		HycraftQuestsAddons.getInstance().restoreInventory(player);
		HycraftQuestsAddons.removeNearbyEntities(player);

		if (HycraftQuestsAddons.getInstance().getActiveTasks().containsKey(player.getUniqueId())) {
			HycraftQuestsAddons.getInstance().getActiveTasks().get(player.getUniqueId()).cancel();
			HycraftQuestsAddons.getInstance().getActiveTasks().remove(player.getUniqueId());
		}

		player.teleport(new Location(Bukkit.getWorld("Prehistoire"), -44.5 , -18, -293.5, 180.0f, 0.0f));
		HycraftQuestsAddons.getInstance().getBossPlayers().remove(player.getUniqueId());

		QuestsAPI questsAPI = HycraftQuestsAddons.getQuestsAPI();
		PlayerAccount acc = questsAPI.getPlugin().getPlayersManager().getAccount(player);

		acc.getQuestDatas(Objects.requireNonNull(questsAPI.getQuestsManager().getQuest(125))).setStage(7); //todo: edit
		player.sendMessage(HycraftQuestsAddons.PREFIX + "§cTu as échoué! Tâche d'être en meilleure forme au prochain essai!");
		player.sendMessage(HycraftQuestsAddons.PREFIX + "§eAfin de réessayer le boss, exécute §b/q retry §eà tout moment.");
		player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);

	}

	public static void endBossChallenge(Player player) {
		HycraftQuestsAddons.getInstance().restoreInventory(player);
		HycraftQuestsAddons.removeNearbyEntities(player);
		player.teleport(new Location(Bukkit.getWorld("Prehistoire"), -44.5 , -18, -293.5, 180.0f, 0.0f));
		player.removePotionEffect(PotionEffectType.CONFUSION);
		HycraftQuestsAddons.getInstance().getBossPlayers().remove(player.getUniqueId());
		UUID playerId = player.getUniqueId();
		HycraftQuestsAddons.removeNearbyEntities(player);

		if (HycraftQuestsAddons.getInstance().getBossBars().containsKey(playerId)) {
			HycraftQuestsAddons.getInstance().getBossBars().get(playerId).removeAll();
			HycraftQuestsAddons.getInstance().getBossBars().remove(playerId);
		}

		if (HycraftQuestsAddons.getInstance().getActiveTasks().containsKey(player.getUniqueId())) {
			HycraftQuestsAddons.getInstance().getActiveTasks().get(player.getUniqueId()).cancel();
			HycraftQuestsAddons.getInstance().getActiveTasks().remove(player.getUniqueId());
		}

		LuckPerms luckPerms = LuckPermsProvider.get();
		HycraftQuestsAddons.addPermission(luckPerms.getPlayerAdapter(Player.class).getUser(player), "hycraft.questsaddons.boss1");

		player.sendMessage(HycraftQuestsAddons.PREFIX + "§aAllez voir Donovan, en incompréhension face aux évènements");
		player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.0f, 1.0f);
	}

	public static void startParticleTask(Player player, Location loc) {
		new BukkitRunnable() {
			@Override
			public void run() {
				UUID playerId = player.getUniqueId();

				if (HycraftQuestsAddons.getInstance().getSpiritPlayers().containsKey(playerId)) {
					showColoredParticleAura(player, loc, Color.fromRGB(255, 85, 0), 50);
				}else{
					cancel();
				}
			}
		}.runTaskTimer(HycraftQuestsAddons.getInstance(), 0L, 10L);
	}

	public static void showColoredParticleAura(Player player, Location loc, Color color, int count) {
		Particle.DustOptions dustOptions = new Particle.DustOptions(color, 1.5F);

		for (int i = 0; i < count; i++) {
			double offsetX = (Math.random() * 2 - 1) * 1.5;
			double offsetY = (Math.random() * 2 - 1) * 1.5;
			double offsetZ = (Math.random() * 2 - 1) * 1.5;

			Location particleLoc = loc.clone().add(0.5 + offsetX, 0.5 + offsetY, 0.5 + offsetZ);
			player.spawnParticle(Particle.REDSTONE, particleLoc, 1, 0, 0, 0, 0, dustOptions);
		}
	}


	public static void startDisplayingActionBar(Player player) {
		BukkitRunnable task = new BukkitRunnable() {
			@Override
			public void run() {
				if(!(player.isOnline())) cancel();
				if (HycraftQuestsAddons.getInstance().getSpiritPlayers().containsKey(player.getUniqueId()))
				{
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§eLe boss est actuellement invulnérable!"));
				}

			}


		};
		HycraftQuestsAddons.getInstance().getActionbarTasks().put(player.getUniqueId(),task);
		task.runTaskTimer(HycraftQuestsAddons.getInstance(), 0,20L);
	}
}