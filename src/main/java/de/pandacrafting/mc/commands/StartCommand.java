package de.pandacrafting.mc.commands;

import com.google.inject.Inject;
import de.pandacrafting.mc.gamestates.LobbyState;
import de.pandacrafting.mc.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCommand implements CommandExecutor {

    @Inject
    private final Main instance;
    private final int forceSeconds;

    @Inject
    public StartCommand(Main instance) {
        this.instance = instance;
        forceSeconds = instance.getCacheContainer().getForceSeconds();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            var player = (Player)sender;
            if(args.length == 0) {
                if(instance.getGameStateManager().getCurrentGameState() instanceof LobbyState) {
                    var lobbyState = (LobbyState)instance.getGameStateManager().getCurrentGameState();
                    if(!lobbyState.getLobbyCountdown().isIdling()) {
                        if(lobbyState.getLobbyCountdown().isRunning() && (lobbyState.getLobbyCountdown().getSeconds() > forceSeconds)) {
                            lobbyState.getLobbyCountdown().setSeconds(forceSeconds);
                            Bukkit.broadcastMessage(Main.PREFIX + "Der Spielstart wurde von §e" + player.getDisplayName() + " §rbeschleunigt!");
                        } else {
                            player.sendMessage(Main.PREFIX + "§6Das Spiel hat bereits begonnen!");
                        }
                    } else {
                        player.sendMessage(Main.PREFIX + "§6Es sind noch nicht ausreichend Spieler vorhanden!");
                    }
                } else {
                    player.sendMessage(Main.PREFIX + "§6Ein forcestart ist nur in der Lobbyphase möglich!");
                }
            } else {
                player.sendMessage(Main.PREFIX + "§6Es konnte kein command gefunden werden!");
            }
        }
        return false;
    }

}
