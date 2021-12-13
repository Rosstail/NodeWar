package fr.rosstail.nodewar.empires;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fr.rosstail.nodewar.required.lang.AdaptMessage;
import fr.rosstail.nodewar.territory.zonehandlers.WorldTerritoryManager;
import fr.rosstail.nodewar.territory.zonehandlers.Territory;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Empire
{
    private static final Map<String, Empire> getSets;
    private static Empire noEmpire;
    private final String name;
    private final String display;
    private final BarColor barColor;
    private final Map<World, ArrayList<Territory>> worldTerritories;
    private final boolean friendlyFire;
    
    Empire(final FileConfiguration config, final String key) {
        this.name = key;
        String display = config.getString(key + ".display");
        if (display == null) {
            display = "&7" + key;
        }
        this.display = ChatColor.translateAlternateColorCodes('&', display);
        if (config.getString(key + ".boss-bar-color") != null) {
            this.barColor = BarColor.valueOf(config.getString(key + ".boss-bar-color"));
        }
        else {
            this.barColor = BarColor.WHITE;
        }
        this.worldTerritories = new HashMap<>();
        if (config.getString(key + ".friendly-fire") != null) {
            this.friendlyFire = config.getBoolean(key + ".firendly-fire");
        }
        else {
            this.friendlyFire = true;
        }
    }
    
    private Empire() {
        this.name = null;
        this.display = ChatColor.translateAlternateColorCodes('&', "&7None");
        this.barColor = BarColor.WHITE;
        this.worldTerritories = new HashMap<>();
        this.friendlyFire = true;
    }
    
    public static Empire gets(final FileConfiguration config, final String key) {
        if (!Empire.getSets.containsKey(key)) {
            Empire.getSets.put(key, new Empire(config, key));
        }
        return Empire.getSets.get(key);
    }
    
    public static Map<String, Empire> getEmpires() {
        return Empire.getSets;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDisplay() {
        return this.display;
    }
    
    public ArrayList<Territory> getWorldTerritories(final World world) {
        if (this.worldTerritories.containsKey(world)) {
            return new ArrayList<>(this.worldTerritories.get(world));
        }
        return new ArrayList<>();
    }
    
    public BarColor getBarColor() {
        return this.barColor;
    }
    
    public void applyTerritories() {
        final List<World> worlds = new ArrayList<>(WorldTerritoryManager.getUsedWorlds().keySet());
        for (final World world : worlds) {
            final ArrayList<Territory> territories = new ArrayList<>();
            for (final WorldTerritoryManager manager : WorldTerritoryManager.getUsedWorlds().values()) {
                for (final Territory territory : manager.getTerritories().values()) {
                    if (territory.getWorld().equals(world) && territory.getEmpire() != null && territory.getEmpire().equals(this)) {
                        if (!worldTerritories.containsKey(world)) {
                            worldTerritories.put(world, new ArrayList<>());
                        }
                        worldTerritories.get(world).add(territory);
                        territories.add(territory);
                        final ProtectedRegion region = territory.getRegion();
                        region.getMembers().removeAll();
                        region.getMembers().addGroup(name);
                    }
                }
            }
            if (this.worldTerritories.containsKey(world)) {
                worldTerritories.replace(world, territories);
            } else {
                this.worldTerritories.put(world, territories);
            }
        }
    }
    
    public boolean isFriendlyFire() {
        return this.friendlyFire;
    }
    
    public static void setNoEmpire() {
        Empire.noEmpire = new Empire();
    }
    
    public static Empire getNoEmpire() {
        return Empire.noEmpire;
    }
    
    static {
        getSets = new HashMap<>();
    }

    public static void init(final fr.rosstail.nodewar.Nodewar plugin) {
        Empire.setNoEmpire();
        final File folder = new File(plugin.getDataFolder(), "empires/");
        if (folder.listFiles() != null) {
            for (final File empireFile : Objects.requireNonNull(folder.listFiles())) {
                if (empireFile.isFile()) {
                    addEmpire(empireFile);
                }
                else {
                    AdaptMessage.print(empireFile + " is not a file", AdaptMessage.prints.WARNING);
                }
            }
        }
    }

    private static void addEmpire(final File empireFile) {
        final FileConfiguration customConfig = new YamlConfiguration();
        try {
            customConfig.load(empireFile);
            for (final String key : customConfig.getKeys(false)) {
                Empire.gets(customConfig, key);
            }
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}