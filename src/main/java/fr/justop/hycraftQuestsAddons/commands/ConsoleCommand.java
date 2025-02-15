package fr.justop.hycraftQuestsAddons.commands;

import fr.justop.hycraftQuestsAddons.HycraftQuestsAddons;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.bukkit.Bukkit.getLogger;

public class ConsoleCommand implements CommandExecutor {
	private final List<String> stage4Dialog = Arrays.asList("",
			"§6§lXandros §r§e: Je sens déjà son aura depuis ma position. C'est impressionant... Dès que je saurais en sa possession, je redeviendrai le mage que j'ai été...",
			"",
			"§6§lXandros §r§e: Voici ce que nous allons faire: tu ... et ensuite ... je .. ok?",
			"",
			"§6§lXandros §r§e: Je crois que... je...",
			"§6§lXandros §r§e: perds...",
			"§6§lXandros §r§e: le...",
			"§6§lXandros §r§e: signal...",
			"§6§lXandros §r§e: ...");

	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
		if(commandSender instanceof Player) return false;
		if(command.getName().equalsIgnoreCase("registerPlayerGoats"))
		{
			Player player = Bukkit.getPlayer(args[0]);
			HycraftQuestsAddons.getInstance().getGoatsCounter().put(player.getUniqueId(), new ArrayList<>());
			return true;
		}
		if (command.getName().equalsIgnoreCase("endDiplo"))
		{
			Player player = Bukkit.getPlayer(args[0]);
            HycraftQuestsAddons.getInstance().getPhase2().remove(player.getUniqueId());
			return true;
		}

		if(command.getName().equals("startStage4"))
		{
			Player player = Bukkit.getPlayer(args[0]);
			addBlock();

			new BukkitRunnable()
			{
				int count = 0;
				@Override
				public void run()
				{
					if(count == 2)
					{
						player.sendMessage(stage4Dialog.get(0));
						player.sendMessage(stage4Dialog.get(1));
						player.playSound(player.getLocation().add(0, 5, 0), Sound.ENTITY_VILLAGER_AMBIENT, 1.0f, 1.0f);
					} else if (count == 5) {
						player.sendMessage(stage4Dialog.get(2));
						player.sendMessage(stage4Dialog.get(3));
						player.playSound(player.getLocation().add(0, 5, 0), Sound.ENTITY_VILLAGER_AMBIENT, 0.7f, 1.0f);
					} else if (count == 7) {
						player.sendMessage(stage4Dialog.get(4));
						player.sendMessage(stage4Dialog.get(5));
						player.playSound(player.getLocation().add(0, 5, 0), Sound.ENTITY_VILLAGER_AMBIENT, 0.4f, 0.8f);
					} else if (count > 7) {
						player.sendMessage(stage4Dialog.get(count - 2));
						player.playSound(player.getLocation().add(0, 5, 0), Sound.ENTITY_VILLAGER_AMBIENT, 0.1f, 0.6f);
					}
					if(count == 11)
					{
						this.cancel();
						removeBlock();
						player.sendMessage("");
						player.sendMessage(HycraftQuestsAddons.PREFIX + "§eIl semblerait que vous ayez perdu contact avec votre locuteur Xandros. Il vous faudra trouvez un autre moyen de vous extirper de cet endroit... Rappellez vous: si vous ne voulez pas attiser" +
								"la colère du diplodocus vous devez absolument éviter de marcher sur les blocs autres que §6§lle béton bleu §r§eou bien le §6§lcorail§r§e!");
						player.sendMessage("");
						player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.0f, 1.0f);

						if (HycraftQuestsAddons.getInstance().getPhase1().containsKey(player.getUniqueId())) HycraftQuestsAddons.getInstance().getPhase1().remove(player.getUniqueId());
						HycraftQuestsAddons.getInstance().getPhase2().put(player.getUniqueId(), "active");
					}
					count ++;
				}

			}.runTaskTimer(HycraftQuestsAddons.getInstance(), 0, 20);
			return true;
		}
		if(command.getName().equalsIgnoreCase("arena"))
		{
			Player player = Bukkit.getPlayer(args[0]);
			startArenaChallenge(player);
			return true;
		}
		return false;
	}

	private void removeBlock()
	{
		Location loc = new Location(Bukkit.getWorld("Prehistoire"), -37, 215, 207);
		Block block = Objects.requireNonNull(Bukkit.getWorld("Prehistoire")).getBlockAt(loc);
		Block block2 = Objects.requireNonNull(Bukkit.getWorld("Prehistoire")).getBlockAt(loc.add(0, 1, 0));
		block.setType(Material.AIR);
		block2.setType(Material.AIR);
	}

	private void addBlock()
	{
		Location loc = new Location(Bukkit.getWorld("Prehistoire"), -37, 215, 207);
		Block block = Objects.requireNonNull(Bukkit.getWorld("Prehistoire")).getBlockAt(loc);
		Block block2 = Objects.requireNonNull(Bukkit.getWorld("Prehistoire")).getBlockAt(loc.add(0, 1, 0));
		block.setType(Material.BARRIER);
		block2.setType(Material.BARRIER);

	}

	private void startArenaChallenge(Player player) {
		saveInventory(player);
		player.getInventory().clear();

		ItemStack stoneSword = new ItemStack(Material.STONE_SWORD);
		ItemMeta meta = stoneSword.getItemMeta();
		if (meta != null) {
			meta.setUnbreakable(true);
			stoneSword.setItemMeta(meta);
		}
		player.getInventory().addItem(stoneSword);

		Location arenaLocation = getAvailableArena();
		if (arenaLocation == null) {
			player.sendMessage("\u00a7cAucune arène disponible.");
			HycraftQuestsAddons.getInstance().restoreInventory(player);
			return;
		}

		arenaLocation.setWorld(Bukkit.getWorld("Challenge"));
		player.teleport(arenaLocation);
		HycraftQuestsAddons.getInstance().getActivePlayers().put(player.getUniqueId(), HycraftQuestsAddons.getInstance().getArenaLocations().indexOf(arenaLocation));
		HycraftQuestsAddons.getInstance().getRemainingMobs().put(player.getUniqueId(), 0);
		HycraftQuestsAddons.getInstance().getMobsKilled().put(player.getUniqueId(), 0);

		BossBar bossBar = Bukkit.createBossBar("§eProgression: 0/0", BarColor.YELLOW, BarStyle.SOLID);
		bossBar.addPlayer(player);
		bossBar.setVisible(true);
		HycraftQuestsAddons.getInstance().getBossBars().put(player.getUniqueId(), bossBar);

		player.setGameMode(GameMode.ADVENTURE);

		BukkitRunnable task = new BukkitRunnable() {
			int countdown = 5;
			@Override
			public void run() {
				if (countdown > 0) {
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§aDébut dans §e" + countdown + "§a secondes...");
					player.sendTitle("§e§l" + countdown, null);
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 1.0f, 1.0f);
					countdown--;
				} else {
					startMobWaves(player, arenaLocation);
					cancel();
				}
			}
		};
		task.runTaskTimer(HycraftQuestsAddons.getInstance(), 0, 20);
		HycraftQuestsAddons.getInstance().getActiveTasks().put(player.getUniqueId(), task);
	}

	private void saveInventory(Player player) {
		HycraftQuestsAddons.getInstance().getSavedInventories().put(player.getUniqueId(), player.getInventory().getContents().clone());
	}

	private Location getAvailableArena() {
		int max = HycraftQuestsAddons.getInstance().getActivePlayers().isEmpty() ? -1 : Collections.max(HycraftQuestsAddons.getInstance().getActivePlayers().values());
		if (max <= 6) {
            return HycraftQuestsAddons.getInstance().getArenaLocations().get(max + 1);
		}
		return null;
	}


	private void startMobWaves(Player player, Location arenaLocation) {
		List<Location> mobSpawns = Arrays.asList(
				arenaLocation.clone().add(11, 0, 9),
				arenaLocation.clone().add(11, 0, -9),
				arenaLocation.clone().add(-10, 0, 9),
				arenaLocation.clone().add(-10, 0, -9)
		);

		BukkitRunnable task = new BukkitRunnable() {
			int wave = 0;
			@Override
			public void run() {
				if (wave >= 5) {
					return;
				}

				int mobCount = 0;
				for (Location loc : mobSpawns) {
					MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob("Plante_mutante").orElse(null);
					if(mob != null){
						mob.spawn(BukkitAdapter.adapt(loc),1);
						mobCount++;
					}
				}

				player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
				player.sendMessage(HycraftQuestsAddons.PREFIX + "§aLa vague §e" + (wave+1) + "§a est apparue. §e(+4)");
				HycraftQuestsAddons.getInstance().getRemainingMobs().put(player.getUniqueId(), HycraftQuestsAddons.getInstance().getRemainingMobs().get(player.getUniqueId()) + mobCount);
				wave++;
			}
		};
		task.runTaskTimer(HycraftQuestsAddons.getInstance(), 0, 20 * 20);
		HycraftQuestsAddons.getInstance().getActiveTasks().put(player.getUniqueId(), task);
	}
}


