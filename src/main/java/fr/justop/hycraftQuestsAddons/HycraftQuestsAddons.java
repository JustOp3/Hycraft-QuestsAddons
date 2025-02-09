package fr.justop.hycraftQuestsAddons;

import fr.justop.hycraftQuestsAddons.commands.ConsoleCommand;
import fr.justop.hycraftQuestsAddons.commands.PlayerCommand;
import fr.justop.hycraftQuestsAddons.listeners.ComposterLaunch;
import fr.justop.hycraftQuestsAddons.listeners.DiploListener;
import fr.justop.hycraftQuestsAddons.listeners.DropperListener;
import fr.justop.hycraftQuestsAddons.listeners.GoatsListener;
import fr.justop.hycraftQuestsAddons.stages.StageItemFrameClick;
import fr.justop.hycraftQuestsAddons.objects.CuboidRegion;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.events.PlayerSetStageEvent;
import fr.skytasul.quests.api.stages.StageType;
import fr.skytasul.quests.api.stages.StageTypeRegistry;
import fr.skytasul.quests.api.utils.XMaterial;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static fr.skytasul.quests.api.gui.ItemUtils.item;

public final class HycraftQuestsAddons extends JavaPlugin {

    private static QuestsAPI questsAPI;
    public static String PREFIX = "ꕭ §r";
    public static HycraftQuestsAddons instance;

    private final List<Location> triggerLocations = new ArrayList<>();
    private final HashMap<String, CuboidRegion> regions = new HashMap<>();
    private final HashMap<UUID, Boolean> playerConstraints = new HashMap<>();
    private final HashMap<UUID, List<Integer>> goatsCounter = new HashMap<>();
    private final Map<UUID, String> phase1 = new HashMap<>();
    private final Map<UUID, String> phase2 = new HashMap<>();
    private final List<Material> allowedBlocks = Arrays.asList(
            Material.TUBE_CORAL_BLOCK,
            Material.BLUE_CONCRETE,
            Material.SLIME_BLOCK,
            Material.AIR,
            Material.WATER
    );


    @Override
    public void onEnable() {

        instance = this;
        questsAPI = QuestsAPI.getAPI();
        initializeItemFramesStage();

        regions.put("TeleportRegion", new CuboidRegion(
                new Location(Bukkit.getWorld("prehistoire"), 380, 83, 385),
                new Location(Bukkit.getWorld("prehistoire"), 399, -61, 402),
                new Location(Bukkit.getWorld("prehistoire"), 386.0D, 91.0D, 392.0D, -45.0F, 0.0F)
        ));

        regions.put("ResetRegion", new CuboidRegion(
                new Location(Bukkit.getWorld("prehistoire"), 390.0D, 89.0D, 396.0D),
                new Location(Bukkit.getWorld("prehistoire"), 388.0D, 88.0D, 394.0D),
                null
        ));

        regions.put("DiploOpenRegion", new CuboidRegion(
                new Location(Bukkit.getWorld("prehistoire"), -50, 219, 208),
                new Location(Bukkit.getWorld("prehistoire"), -50, 217, 206),
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
        onCommands();

        Bukkit.getConsoleSender().sendMessage("Plugin initialisé");
    }

    private void onListeners()
    {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new DropperListener(), this);
        pm.registerEvents(new DiploListener(), this);
        pm.registerEvents(new GoatsListener(), this);
        pm.registerEvents(new ComposterLaunch(), this);
    }

    private void onCommands()
    {
        this.getCommand("registerPlayerGoats").setExecutor(new ConsoleCommand());
        this.getCommand("startStage4").setExecutor(new ConsoleCommand());
        this.getCommand("endDiplo").setExecutor(new ConsoleCommand());
        this.getCommand("q").setExecutor(new PlayerCommand());
    }

    private void initializeItemFramesStage()
    {
        StageTypeRegistry stages = QuestsAPI.getAPI().getStages();
        stages.register(new StageType<>("INTERACT_ITEM_FRAME", StageItemFrameClick.class,"§aItemFrame clic",
                StageItemFrameClick::deserialize, item(XMaterial.ITEM_FRAME, "§eItemFrame clic"),
                StageItemFrameClick.Creator::new));
    }

    public void boxPlayer(Player player)
    {
        player.setGameMode(GameMode.ADVENTURE);
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
    }

    public void unboxPlayer(Player player)
    {
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

    public HashMap<UUID, List<Integer>> getGoatsCounter() {
        return goatsCounter;
    }

    public Map<UUID, String> getPhase2() {
        return phase2;
    }

    public Map<UUID, String> getPhase1() {
        return phase1;
    }

    public List<Material> getAllowedBlocks() {
        return allowedBlocks;
    }
}
