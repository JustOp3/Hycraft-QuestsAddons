package fr.justop.hycraftQuestsAddons;

import fr.justop.hycraftQuestsAddons.listeners.ArrowListener;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
						HycraftQuestsAddons.getInstance().getBossPhase().put(player.getUniqueId(), false);
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
		List<Location> mobSpawns = new ArrayList<>();
		switch (mode) {
			case 0:
				mobSpawns = Arrays.asList(
						arenaLocation.clone().add(11, 0, 9),
						arenaLocation.clone().add(11, 0, -9),
						arenaLocation.clone().add(-10, 0, 9),
						arenaLocation.clone().add(-10, 0, -9)
				);
				break;

			case 1:
				mobSpawns = Arrays.asList(
						arenaLocation.clone().add(11, 0, 9),
						arenaLocation.clone().add(11, 0, -9),
						arenaLocation.clone().add(-10, 0, 9),
						arenaLocation.clone().add(0, 0, 14),
						arenaLocation.clone().add(0, 0, -13),
						arenaLocation.clone().add(16, 0, 0),
						arenaLocation.clone().add(-15, 0, 0),
						arenaLocation.clone().add(-10, 0, -9)
				);
		}


		BukkitRunnable waves = getWaves(player, mobSpawns, mode);
		int period = mode == 0 ? 20 * 20 : 60 * 20;
		waves.runTaskTimer(HycraftQuestsAddons.getInstance(), 0, period);
		HycraftQuestsAddons.getInstance().getActiveTasks().put(player.getUniqueId(), waves);
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
		ItemStack ironHelmet = new ItemStack(Material.IRON_HELMET);
		ItemStack ironChestplate = new ItemStack(Material.IRON_CHESTPLATE);
		ItemStack ironLeggings = new ItemStack(Material.IRON_LEGGINGS);
		ItemStack ironBoots = new ItemStack(Material.IRON_BOOTS);

		ironHelmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		ironChestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		ironLeggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		ironBoots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);

		player.getInventory().setArmorContents(new ItemStack[]{ironBoots, ironLeggings, ironChestplate, ironHelmet});

		ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
		ItemMeta swordMeta = sword.getItemMeta();
		swordMeta.setCustomModelData(3000);
		sword.setItemMeta(swordMeta);

		ItemStack bow = new ItemStack(Material.BOW);
		ItemMeta bowMeta = bow.getItemMeta();
		bowMeta.setCustomModelData(3061);
		bow.setItemMeta(bowMeta);

		ItemStack goldenApples = new ItemStack(Material.GOLDEN_APPLE, 5);

		player.getInventory().addItem(sword, bow);
		player.getInventory().setItem(8, goldenApples);
		player.getInventory().setItem(9, new ItemStack(Material.ARROW));
	}

	public static void freezeBoss(ActiveMob boss, int durationTicks) {
		LivingEntity bossEntity = (LivingEntity) boss.getEntity().getBukkitEntity();
		HycraftQuestsAddons.getInstance().getFrozenBosses().add(boss);

		bossEntity.setAI(false);
		bossEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(0);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (bossEntity.isValid()) {
					bossEntity.setAI(true);
					bossEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(20);
					HycraftQuestsAddons.getInstance().getFrozenBosses().remove(boss);
				}
			}
		}.runTaskLater(HycraftQuestsAddons.getInstance(), durationTicks);
	}

	public static void invokeSpirit(Player player) {
		Location arenaLocation = HycraftQuestsAddons.getInstance().getArenaLocations().get(HycraftQuestsAddons.getInstance().getBossPlayers().get(player.getUniqueId()));
		arenaLocation.setWorld(Bukkit.getWorld("BossFight1"));

		Location cristalLocation = ArrowListener.getRandomCristal(arenaLocation);
		HycraftQuestsAddons.getInstance().getActiveCristalPos().put(player.getUniqueId(), cristalLocation);

		cristalLocation.getBlock().setType(Material.SHROOMLIGHT);
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
		HycraftQuestsAddons.getInstance().getActiveTasks().put(player.getUniqueId(),task);
		task.runTaskTimer(HycraftQuestsAddons.getInstance(), 0,20L);
	}
}