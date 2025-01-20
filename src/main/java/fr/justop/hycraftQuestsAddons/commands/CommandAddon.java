package fr.justop.hycraftQuestsAddons.commands;

import fr.justop.hycraftQuestsAddons.HycraftQuestsAddons;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandAddon implements CommandExecutor {
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
		if(command.getName().equals("registerPlayerGoats"))
		{
			Player player = Bukkit.getPlayer(args[0]);
			HycraftQuestsAddons.getInstance().getGoatsCounter().put(player.getUniqueId(), new ArrayList<>());
			return true;
		}
		if(command.getName().equals("startStage4"))
		{
			Player player = Bukkit.getPlayer(args[0]);
			HycraftQuestsAddons.getInstance().boxPlayer(player);

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
					} else if (count == 5) {
						player.sendMessage(stage4Dialog.get(2));
						player.sendMessage(stage4Dialog.get(3));
					} else if (count == 7) {
						player.sendMessage(stage4Dialog.get(4));
						player.sendMessage(stage4Dialog.get(5));
					} else if (count > 7) {
						player.sendMessage(stage4Dialog.get(count - 2));
					}
					if(count == 11)
					{
						this.cancel();
						player.sendMessage("");
						player.sendMessage(HycraftQuestsAddons.PREFIX + "§eIl semblerait que vous ayez perdu contact avec votre locuteur Xandros. Il vous faudra trouvez un autre moyen de vous extirper de cet endroit...");
						player.playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE, 1.0f, 1.0f);

						HycraftQuestsAddons.getInstance().unboxPlayer(player);
					}
					count ++;
				}

			}.runTaskTimer(HycraftQuestsAddons.getInstance(), 0, 20);
			return true;
		}
		return false;
	}
}
