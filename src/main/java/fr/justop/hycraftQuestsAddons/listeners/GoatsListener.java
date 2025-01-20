package fr.justop.hycraftQuestsAddons.listeners;

import fr.justop.hycraftQuestsAddons.HycraftQuestsAddons;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.players.PlayerAccount;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;
import java.util.Objects;

public class GoatsListener implements Listener
{
	@EventHandler
	public void onRightClickMobHead(PlayerInteractEvent event) {
		if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		Block clickedBlock = event.getClickedBlock();
		if (clickedBlock == null) return;
		if (clickedBlock.getType() != Material.PLAYER_HEAD) return;
		Location targetLocation = new Location(Bukkit.getWorld("Prehistoire"), -435, 2, 302);
		if (!clickedBlock.getLocation().equals(targetLocation)) return;

		if(HycraftQuestsAddons.getInstance().getGoatsCounter().get(event.getPlayer().getUniqueId()).contains(94))
		{
			event.getPlayer().sendMessage(HycraftQuestsAddons.PREFIX + "§cVous avez déjà examiné cette tête de chèvre!");
			event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
			return;
		}

		HycraftQuestsAddons.getInstance().getGoatsCounter().get(event.getPlayer().getUniqueId()).add(94);
		event.getPlayer().sendMessage(HycraftQuestsAddons.PREFIX + "§aVous avez trouvé euh... Ce qu'il reste de l'une des chèvres de M.Sevin! §e(" +
				HycraftQuestsAddons.getInstance().getGoatsCounter().get(event.getPlayer().getUniqueId()).size() +
				"/11)");
		event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_FIRECHARGE_USE,1.0F, 1.0F);

		if(HycraftQuestsAddons.getInstance().getGoatsCounter().get(event.getPlayer().getUniqueId()).size() == 11)
		{
			LuckPerms luckPerms = LuckPermsProvider.get();
			addPermission(luckPerms.getPlayerAdapter(Player.class).getUser(event.getPlayer()), "hycraft.questsaddons.hasfoundgoats");
			event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
			event.getPlayer().sendMessage(HycraftQuestsAddons.PREFIX + "§aVous avez retrouvé toutes les chèvres de §eM.Sevin §a. Allez lui apprendre la nouvelle!");

		}

	}
	@EventHandler
	public void onClicNpc(NPCRightClickEvent event){
		NPC npc = event.getNPC();
		Player player = event.getClicker();
		if(npc.getId() <= 93 && npc.getId() >= 84)
		{
			QuestsAPI questsAPI = HycraftQuestsAddons.getQuestsAPI();
			PlayerAccount acc = questsAPI.getPlugin().getPlayersManager().getAccount(player);
			List<Integer> playerList = HycraftQuestsAddons.getInstance().getGoatsCounter().get(player.getUniqueId());

			if(acc.getQuestDatas(Objects.requireNonNull(questsAPI.getQuestsManager().getQuest(114))).getStage() == 1)
			{
				if(!(playerList.contains(npc.getId())))
				{
					playerList.add(npc.getId());
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§aVous avez retrouvé §e" +
							playerList.size() +
							"/11 §achèvres de M.Sevin");
					player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE,1.0F, 1.0F);

					if(playerList.size() == 11)
					{
						LuckPerms luckPerms = LuckPermsProvider.get();
						addPermission(luckPerms.getPlayerAdapter(Player.class).getUser(player), "hycraft.questsaddons.hasfoundgoats");
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
						player.sendMessage(HycraftQuestsAddons.PREFIX + "§aVous avez retrouvé toutes les chèvres de §eM.Sevin §a. Allez lui apprendre la nouvelle!");

					}
					return;
				}
				player.sendMessage(HycraftQuestsAddons.PREFIX + "§cVous avez déjà trouvé cette chèvre!");
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
			}

		}
	}

	public void addPermission(User user, String permission) {
		LuckPerms luckPerms = LuckPermsProvider.get();
		user.data().add(Node.builder(permission).build());
		luckPerms.getUserManager().saveUser(user);
	}
}
