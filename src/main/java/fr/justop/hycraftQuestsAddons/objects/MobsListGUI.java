package fr.justop.hycraftQuestsAddons.objects;

import fr.skytasul.quests.api.editors.TextEditor;
import fr.skytasul.quests.api.editors.parsers.NumberParser;
import fr.skytasul.quests.api.gui.ItemUtils;
import fr.skytasul.quests.api.gui.LoreBuilder;
import fr.skytasul.quests.api.gui.templates.ListGUI;
import fr.skytasul.quests.api.localization.Lang;
import fr.skytasul.quests.api.mobs.LeveledMobFactory;
import fr.skytasul.quests.api.utils.CountableObject;
import org.bukkit.DyeColor;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

public class MobsListGUI extends ListGUI<CountableObject.MutableCountableObject<Mob<?>>> {

	private Consumer<List<CountableObject.MutableCountableObject<Mob<?>>>> end;

	public MobsListGUI(Collection<CountableObject.MutableCountableObject<Mob<?>>> objects,
					   Consumer<List<CountableObject.MutableCountableObject<Mob<?>>>> end) {
		super(Lang.INVENTORY_MOBS.toString(), DyeColor.ORANGE, objects);
		this.end = end;
	}

	@Override
	public void finish(List<CountableObject.MutableCountableObject<Mob<?>>> objects) {
		end.accept(objects);
	}

	@Override
	public void clickObject(CountableObject.MutableCountableObject<Mob<?>> mob, ItemStack item, ClickType click) {
		super.clickObject(mob, item, click);
		if (click == ClickType.RIGHT) {
			Lang.MOB_NAME.send(player);
			new TextEditor<>(player, super::reopen, name -> {
				mob.getObject().setCustomName((String) name);
				setItems();
				reopen();
			}).passNullIntoEndConsumer().start();
		} else if (click == ClickType.LEFT) {
			Lang.MOB_AMOUNT.send(player);
			new TextEditor<>(player, super::reopen, amount -> {
				mob.setAmount(amount);
				setItems();
				reopen();
			}, NumberParser.INTEGER_PARSER_STRICT_POSITIVE).start();
		} else if (click == ClickType.SHIFT_RIGHT) {
			if (mob.getObject().getFactory() instanceof LeveledMobFactory) {
				new TextEditor<>(player, super::reopen, level -> {
					mob.getObject().setMinLevel(level);
					setItems();
					reopen();
				}, new NumberParser<>(Double.class, true, false)).start();
			}
		}
	}

	@Override
	public void createObject(Function<CountableObject.MutableCountableObject<Mob<?>>, ItemStack> callback) {
		new MobSelectionGUI(mob -> {
			if (mob == null)
				reopen();
			else
				callback.apply(CountableObject.createMutable(UUID.randomUUID(), mob, 1));
		}).open(player);
	}

	@Override
	public ItemStack getObjectItemStack(CountableObject.MutableCountableObject<Mob<?>> mob) {
		LoreBuilder loreBuilder = createLoreBuilder(mob)
				.addDescription(Lang.Amount.format(mob))
				.addClick(ClickType.LEFT, Lang.editAmount.toString())
				.addClick(ClickType.RIGHT, Lang.editMobName.toString())
				.addClick(ClickType.SHIFT_RIGHT, (mob.getObject().getFactory() instanceof LeveledMobFactory ? "" : "§8§m")
						+ Lang.setLevel.toString());
		ItemStack item = ItemUtils.item(mob.getObject().getMobItem(), mob.getObject().getName(), loreBuilder.toLoreArray());
		item.setAmount(Math.min(mob.getAmount(), 64));
		return item;
	}
}
