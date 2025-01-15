package fr.justop.hycraftQuestsAddons;

import fr.justop.hycraftQuestsAddons.listeners.DropperListener;
import fr.justop.hycraftQuestsAddons.listeners.StageItemFrameClick;
import fr.justop.hycraftQuestsAddons.objects.CuboidRegion;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.QuestsAPIProvider;
import fr.skytasul.quests.api.gui.ItemUtils;
import fr.skytasul.quests.api.localization.Lang;
import fr.skytasul.quests.api.players.PlayerAccount;
import fr.skytasul.quests.api.players.PlayersManager;
import fr.skytasul.quests.api.stages.StageController;
import fr.skytasul.quests.api.stages.StageType;
import fr.skytasul.quests.api.stages.StageTypeRegistry;
import fr.skytasul.quests.api.stages.creation.StageCreation;
import fr.skytasul.quests.api.utils.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static fr.skytasul.quests.api.gui.ItemUtils.item;

public final class HycraftQuestsAddons extends JavaPlugin {

    private static QuestsAPI questsAPI;
    public static String PREFIX = "ꕭ §r";
    public static HycraftQuestsAddons instance;

    private final List<Location> triggerLocations = new ArrayList<>();
    private final HashMap<String, CuboidRegion> regions = new HashMap<>();
    private final HashMap<UUID, Boolean> playerConstraints = new HashMap<>();

    @Override
    public void onEnable() {

        instance = this;
        questsAPI = QuestsAPI.getAPI();
        initializeItemFramesStage();

        regions.put("TeleportRegion", new CuboidRegion(
                new Location(Bukkit.getWorld("prehistoire"), 382, 83, 390),
                new Location(Bukkit.getWorld("prehistoire"), 397, -61, 402),
                new Location(Bukkit.getWorld("prehistoire"), 386.0D, 91.0D, 392.0D, -45.0F, 0.0F)
        ));

        regions.put("ResetRegion", new CuboidRegion(
                new Location(Bukkit.getWorld("prehistoire"), 390.0D, 89.0D, 396.0D),
                new Location(Bukkit.getWorld("prehistoire"), 388.0D, 88.0D, 394.0D),
                null
        ));

        this.triggerLocations.add(new Location(getServer().getWorld("prehistoire"), 390.0D, 89.0D, 396.0D));
        this.triggerLocations.add(new Location(getServer().getWorld("prehistoire"), 390.0D, 89.0D, 395.0D));
        this.triggerLocations.add(new Location(getServer().getWorld("prehistoire"), 390.0D, 89.0D, 394.0D));
        this.triggerLocations.add(new Location(getServer().getWorld("prehistoire"), 389.0D, 89.0D, 396.0D));
        this.triggerLocations.add(new Location(getServer().getWorld("prehistoire"), 389.0D, 89.0D, 395.0D));
        this.triggerLocations.add(new Location(getServer().getWorld("prehistoire"), 389.0D, 89.0D, 394.0D));
        this.triggerLocations.add(new Location(getServer().getWorld("prehistoire"), 388.0D, 89.0D, 396.0D));
        this.triggerLocations.add(new Location(getServer().getWorld("prehistoire"), 388.0D, 89.0D, 395.0D));
        this.triggerLocations.add(new Location(getServer().getWorld("prehistoire"), 388.0D, 89.0D, 394.0D));

        onListeners();

        Bukkit.getConsoleSender().sendMessage("Plugin initialisé");
    }

    private void onListeners()
    {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new DropperListener(), this);
    }

    private void initializeItemFramesStage()
    {
        StageTypeRegistry stages = QuestsAPI.getAPI().getStages();
        stages.register(new StageType<>("INTERACT_ITEM_FRAME", StageItemFrameClick.class,"§aItemFrame clic",
                StageItemFrameClick::deserialize, item(XMaterial.ITEM_FRAME, "§eItemFrame clic"),
                StageItemFrameClick.Creator::new));
    }

    public List<Location> getTriggerLocations() {
        return this.triggerLocations;
    }

    public static QuestsAPI getQuestsAPI() {
        return questsAPI;
    }

    public static  HycraftQuestsAddons getInstance(){
        return instance;
    }

    public HashMap<String, CuboidRegion> getRegions()
    {
        return regions;
    }


    public HashMap<UUID, Boolean> getPlayerConstraints() {
        return playerConstraints;
    }
}
