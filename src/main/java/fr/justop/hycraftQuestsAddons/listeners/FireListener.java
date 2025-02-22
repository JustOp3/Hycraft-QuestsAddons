package fr.justop.hycraftQuestsAddons.listeners;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import fr.justop.hycraftQuestsAddons.HycraftQuestsAddons;
import fr.justop.hycraftQuestsAddons.objects.CuboidRegion;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.players.PlayerAccount;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.lib.jooq.Meta;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Objects;

public class FireListener implements Listener {

	@EventHandler
	public void onPlayerClickBlock(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		CuboidRegion rg = new CuboidRegion(new Location(Bukkit.getWorld("Prehistoire"),-300,-8,-77),
				new Location(Bukkit.getWorld("Prehistoire"), -324,5,-100),null);

		if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			QuestsAPI questsAPI = HycraftQuestsAddons.getQuestsAPI();
			PlayerAccount acc = questsAPI.getPlugin().getPlayersManager().getAccount(player);

			if (!(acc.getQuestDatas(Objects.requireNonNull(questsAPI.getQuestsManager().getQuest(126))).getStage() == 1)) return;

			Block block = event.getClickedBlock();
			if(!(block.getType() == Material.NOTE_BLOCK)) return;
			if(!(rg.isInside(block.getLocation()))) return;

			ItemStack item = new ItemStack(Material.PAPER);
			ItemMeta im = item.getItemMeta();
			im.setCustomModelData(504);
			im.setDisplayName("§9§lFragment de météorite");
			im.setLore(Arrays.asList("§b§oVous avez trouvé ce minerai", "§b§osur la météorite"));
			im.addEnchant(Enchantment.DAMAGE_ALL, 1,false);
			im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(im);

			if(player.getInventory().contains(item))
			{
				player.sendMessage(HycraftQuestsAddons.PREFIX + "§cVous avez déjà un extrait de météorite en votre possession!");
				return;
			}

			player.getInventory().addItem(item);
			player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1.0f,1.0f);
			player.sendMessage(HycraftQuestsAddons.PREFIX + "§aVous avez extrait le minerai avec succès! Rapportez le à Darek.");
		}

	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location location = player.getLocation();

		if (location.getBlock().getType().name().contains("FIRE")) {
			if (isPlayerInRegion(player, "fire_region")) {
				Location teleportLocation = new Location(player.getWorld(), 100, 65, 200); // Coordonnées de téléportation
				player.teleport(teleportLocation);
				player.sendMessage("\u00a7cVous avez touché le feu dans la région ! Téléportation...");
			}
		}
	}

	private boolean isPlayerInRegion(Player player, String regionId) {
		RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
		RegionManager regions = container.get((World) player.getWorld());

		if (regions != null) {
			BlockVector3 playerVector = BlockVector3.at(
					player.getLocation().getBlockX(),
					player.getLocation().getBlockY(),
					player.getLocation().getBlockZ()
			);

			ApplicableRegionSet regionSet = regions.getApplicableRegions(playerVector);

			for (ProtectedRegion region : regionSet) {
				if (region.getId().equalsIgnoreCase(regionId)) {
					return true;
				}
			}
		}
		return false;
	}


}

