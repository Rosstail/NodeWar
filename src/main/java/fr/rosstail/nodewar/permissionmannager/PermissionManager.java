package fr.rosstail.nodewar.permissionmannager;

import fr.rosstail.nodewar.ConfigData;
import fr.rosstail.nodewar.lang.AdaptMessage;
import fr.rosstail.nodewar.permissionmannager.types.NwLuckPermsHandler;
import fr.rosstail.nodewar.team.NwITeam;
import fr.rosstail.nodewar.team.NwITeamManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class PermissionManager {
    private static String permissionPlugin;
    public static Map<String, Class<? extends NwIPermissionManagerHandler>> iPermissionManagerMap = new HashMap<>();
    private NwIPermissionManagerHandler iPermissionManager = null;
    private static PermissionManager manager;

    static {
        iPermissionManagerMap.put("luckperms", NwLuckPermsHandler.class); // end failsafe on AUTO
    }

    public static boolean canAddCustomManager(String name) {
        return (!iPermissionManagerMap.containsKey(name));
    }

    /**
     * Add custom objective from add-ons
     *
     * @param name
     * @param customPermisionHandlerClass
     * @return
     */
    public static void addCustomManager(String name, Class<? extends NwIPermissionManagerHandler> customPermisionHandlerClass) {
        iPermissionManagerMap.put(name, customPermisionHandlerClass);
        AdaptMessage.print("[Nodewar] Custom permissionmanager " + name + " added to the list !", AdaptMessage.prints.OUT);
    }

    public static void init() {
        if (manager == null) {
            manager = new PermissionManager();
        }
    }

    public String getUsedSystem() {
        String system = ConfigData.getConfigData().general.defaultPermissionPlugin;
        if (iPermissionManagerMap.containsKey(system) && Bukkit.getServer().getPluginManager().getPlugin(system) != null) {
            return system;
        } else if (system.equalsIgnoreCase("auto")) {
            for (Map.Entry<String, Class<? extends NwIPermissionManagerHandler>> entry : iPermissionManagerMap.entrySet()) {
                String s = entry.getKey();
                if (Bukkit.getServer().getPluginManager().getPlugin(s) != null) {
                    return s;
                }
            }
        }

        return null;
    }

    public void loadManager() {
        String usedSystem = getUsedSystem();

        if (usedSystem != null) {
            Class<? extends NwIPermissionManagerHandler> managerClass = iPermissionManagerMap.get(usedSystem);
            Constructor<? extends NwIPermissionManagerHandler> managerConstructor;

            try {
                managerConstructor = managerClass.getDeclaredConstructor();
                iPermissionManager = managerConstructor.newInstance();
                AdaptMessage.print("[Nodewar] Using " + usedSystem + " perm", AdaptMessage.prints.OUT);
            } catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Missing appropriate constructor in TeamManager class.", e);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            iPermissionManager = new NwLuckPermsHandler();
            AdaptMessage.print("[Nodewar] Using default " + usedSystem + " perm", AdaptMessage.prints.OUT);
        }
    }

    public void createGroup(String teamName) {
        iPermissionManager.createGroup("nw_" + teamName);
    }

    public void deleteGroup(String teamName) {
        iPermissionManager.deleteGroup("nw_" + teamName);
    }

    public void setPlayerGroup(final Player player, final NwITeam nwTeam) {
        iPermissionManager.setPlayerGroup("nw_" + nwTeam.getName(), player);
    }

    public void removePlayerGroup(final Player player, String exceptionGroupName) {
        iPermissionManager.removePlayerGroup(player, exceptionGroupName);
    }

    public static PermissionManager getManager() {
        return manager;
    }
}
