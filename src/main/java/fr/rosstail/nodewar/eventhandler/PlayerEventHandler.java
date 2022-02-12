package fr.rosstail.nodewar.eventhandler;

import fr.rosstail.nodewar.Nodewar;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import fr.rosstail.nodewar.datahandlers.PlayerInfo;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;

public class PlayerEventHandler implements Listener
{
    
    public PlayerEventHandler(final Nodewar plugin) {
        final FileConfiguration config = plugin.getCustomConfig();
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final PlayerInfo playerInfo = PlayerInfo.gets(player);
        playerInfo.loadInfo();
        playerInfo.setPlayerGroup(playerInfo.getEmpire());
    }
    
    @EventHandler
    public void onPlayerLeave(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final PlayerInfo playerInfo = PlayerInfo.gets(player);
        playerInfo.updateAll(true);
    }
}
