package fr.justop.hycraftQuestsAddons.commands;

import fr.justop.hycraftQuestsAddons.BossQuestUtils;
import fr.justop.hycraftQuestsAddons.HycraftQuestsAddons;
import fr.justop.hycraftQuestsAddons.listeners.ComposterLaunch;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.options.description.DescriptionSource;
import fr.skytasul.quests.api.players.PlayerAccount;
import fr.skytasul.quests.api.players.PlayerQuestDatas;
import fr.skytasul.quests.api.pools.QuestPool;
import fr.skytasul.quests.api.quests.Quest;
import fr.skytasul.quests.api.quests.branches.QuestBranch;
import fr.skytasul.quests.api.quests.branches.QuestBranchesManager;
import fr.skytasul.quests.api.stages.StageController;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlayerCommand implements CommandExecutor {
	@Override
	public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
		if(!(commandSender instanceof Player player)) return false;
		HycraftQuestsAddons instance = HycraftQuestsAddons.getInstance();
		QuestsAPI questsAPI = HycraftQuestsAddons.getQuestsAPI();
		PlayerAccount acc = questsAPI.getPlugin().getPlayersManager().getAccount(player);

		if (acc == null) {
			player.sendMessage(HycraftQuestsAddons.PREFIX + "§cAucun compte de quête trouvé. (contactez un membre du staff)");
			return false;
		}

		switch(strings[0]){
			case "interrupt":
				if (instance.getPhase1().containsKey(player.getUniqueId()))
				{
					instance.getPhase1().put(player.getUniqueId(), "inactive");
					player.setGameMode(GameMode.SURVIVAL);
					player.teleport(new Location(Bukkit.getWorld("Prehistoire"), -270, 13, 200, -90f, -15));
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§eVous avez interrompu la quête §b§lUne descente furtive§r§e. Utilisez §6/q rejoin §e à tout moment pour reprendre la où vous en étiez");

				}else if(instance.getPhase2().containsKey(player.getUniqueId())) {

					instance.getPhase2().put(player.getUniqueId(), "inactive");
					player.setGameMode(GameMode.SURVIVAL);
					player.teleport(new Location(Bukkit.getWorld("Prehistoire"), -270, 13, 200, -90f, -15));
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§eVous avez interrompu la quête §b§lUne descente furtive§r§e. Utilisez §6/q rejoin §e à tout moment pour reprendre la où vous en étiez");
				}
				break;

			case "rejoin":
				if (instance.getPhase1().containsKey(player.getUniqueId()))
				{

					acc.getQuestDatas(Objects.requireNonNull(questsAPI.getQuestsManager().getQuest(115))).setStage(1);
					instance.getPhase1().put(player.getUniqueId(), "active");
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§eVous avez rejoint la quête avec succès la où vous en étiez.");
					player.teleport(ComposterLaunch.TARGET_LOCATION);
					player.setGameMode(GameMode.ADVENTURE);
					ComposterLaunch.slowFall(player);

				}else if(instance.getPhase2().containsKey(player.getUniqueId()))
				{
					instance.getPhase2().put(player.getUniqueId(), "active");
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§eVous avez rejoint la quête avec succès la où vous en étiez. Xandros ayant perdu le contact avec vous, vous devez trouver un moyen par vous même pour échapper au diplodocus.");
					player.teleport(new Location(Bukkit.getWorld("Prehistoire"), -35, 215, 207, 90f, 0f));
					player.setGameMode(GameMode.ADVENTURE);
				}
				break;

			case "info":
				boolean hasActiveQuest = false;

				for (Quest quest : questsAPI.getQuestsManager().getQuests()) {
					PlayerQuestDatas questData = acc.getQuestDatas(quest);

					if (questData.hasStarted() && !questData.isFinished()) {
						hasActiveQuest = true;

						int stage = questData.getStage();
						QuestBranch playerBranch = quest.getBranchesManager().getPlayerBranch(acc);

						if (playerBranch != null) {
							StageController currentStage = playerBranch.getRegularStage(stage);

                            String desc = currentStage.getDescriptionLine(acc, DescriptionSource.FORCELINE);

                            player.sendMessage("§6§l➤ Quête en cours : §e" + quest.getName());
                            player.sendMessage("§7-------------------------------");

                            if (desc != null && !desc.isEmpty()) {
                                String[] descLines = desc.split("\n");
                                for (String line : descLines) {
                                    player.sendMessage("§f▪ " + line);
                                }
                            } else {
                                player.sendMessage(HycraftQuestsAddons.PREFIX + "§eCette étape n'a pas de description.");
                            }

                            player.sendMessage("§7-------------------------------");
                        } else {
							player.sendMessage(HycraftQuestsAddons.PREFIX + "§cImpossible de récupérer la branche active pour la quête : §e" + quest.getName());
						}
					}
				}

				if (!hasActiveQuest) {
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§eVous n'avez aucune quête en cours.");
				}
				break;

			case "enigme":

				if(acc.getQuestDatas(Objects.requireNonNull(questsAPI.getQuestsManager().getQuest(125))).getStage() == 1)
				{
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§aLors du temps d’angoisse, le brave guerrier brandira l’arc céleste et frappera en leur cœur les gardien du sanctuaire sacré du plus faible au plus puissant. On raconte que la taille de leur insigne déterminait leur puissance... ");
					player.sendMessage("§aUtilisez §b/q indice §asi vous avez du mal à comprendre la signification de ces mots.");
				}
				else{
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§cVous n'avez aucune énigme à résoudre!");
				}
				break;

			case "indice":
				if(acc.getQuestDatas(Objects.requireNonNull(questsAPI.getQuestsManager().getQuest(125))).getStage() == 1)
				{
					player.sendMessage("§7-------------------------------");

					player.sendMessage("§f▪ §aChaque gardien du sanctuaire détient une insigne.");
					player.sendMessage("§f▪ §aLeur insigne se trouve à leur pieds");
					player.sendMessage("§f▪ §aVous devrez compter un nombre de §eShroom lights §apour déterminer la taille d'une insigne");
					player.sendMessage("§f▪ §aL'arc célèste n'est pas un arc comme les autres...");

					player.sendMessage("§7-------------------------------");
				}
				else{
					player.sendMessage(HycraftQuestsAddons.PREFIX + "§cVous n'avez aucune énigme à résoudre!");
				}
				break;

			case "retry":
				if(acc.getQuestDatas(Objects.requireNonNull(questsAPI.getQuestsManager().getQuest(125))).getStage() == 1)
				{
					if(HycraftQuestsAddons.getInstance().getBossPlayers().containsKey(player.getUniqueId())){
						player.sendMessage(HycraftQuestsAddons.PREFIX + "§cVous êtes déjà en combat!");
						return true;
					}

					player.sendMessage(HycraftQuestsAddons.PREFIX + "§aVous rééssayez le combat de boss.");
					BossQuestUtils.startBossFight(player);

				}
		}

		return true;
	}
}
