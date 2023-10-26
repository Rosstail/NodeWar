package fr.rosstail.nodewar.commands.subcommands.team.teamsubcommands;

import fr.rosstail.nodewar.commands.CommandManager;
import fr.rosstail.nodewar.commands.subcommands.team.TeamSubCommand;
import fr.rosstail.nodewar.player.PlayerData;
import fr.rosstail.nodewar.player.PlayerDataManager;
import fr.rosstail.nodewar.storage.StorageManager;
import fr.rosstail.nodewar.team.NwTeam;
import fr.rosstail.nodewar.team.TeamDataManager;
import fr.rosstail.nodewar.team.TeamMemberModel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TeamJoinCommand extends TeamSubCommand {
    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "Desc join nodewar team";
    }

    @Override
    public String getSyntax() {
        return "nodewar team join <name>";
    }

    @Override
    public String getHelp() {
        return super.getHelp();
    }

    @Override
    public String getPermission() {
        return "nodewar.command.team.join";
    }

    @Override
    public void perform(CommandSender sender, String[] args, String[] arguments) {
        Player senderPlayer;
        String teamName;
        NwTeam nwTeam;
        PlayerData playerData;
        TeamMemberModel teamMemberModel;
        if (!CommandManager.canLaunchCommand(sender, this)) {
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Must be sent by player");
            return;
        }
        senderPlayer = ((Player) sender).getPlayer();
        playerData = PlayerDataManager.getPlayerDataMap().get(senderPlayer.getName());

        if (playerData.getTeam() != null) {
            sender.sendMessage("You are already on a team");
            return;
        } else if (args.length < 3) {
            sender.sendMessage("TeamCreateCommand - Not enough args");
            return;
        }

        teamName = args[2];
        nwTeam = TeamDataManager.getTeamDataManager().getStringTeamMap().get(teamName);

        if (nwTeam == null) {
            sender.sendMessage("TeamJoinCommand - This team does not exist");
            return;
        }

        sender.sendMessage("TODO joining " + teamName + " team.");
        teamMemberModel = new TeamMemberModel(nwTeam.getTeamModel().getId(), playerData.getId(), 5, new Timestamp(System.currentTimeMillis()));
        StorageManager.getManager().insertTeamMemberModel(teamMemberModel);
        nwTeam.getMemberModelMap().put(playerData.getId(), teamMemberModel);
        playerData.setTeam(nwTeam);
    }

    @Override
    public List<String> getSubCommandsArguments(Player sender, String[] args, String[] arguments) {
        return new ArrayList<>(TeamDataManager.getTeamDataManager().getStringTeamMap().keySet());
    }
}
