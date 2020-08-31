package de.pandacrafting.mc.gamestates;

import de.pandacrafting.mc.countdowns.LobbyCountdown;
import de.pandacrafting.mc.main.Main;
import de.pandacrafting.mc.teamhandler.Team8x1;
import de.pandacrafting.mc.teamhandler.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.Random;

public class LobbyState extends GameState {

    public static final int MAX_PLAYERS = 8;

    public static int minPlayers;
    private final GameStateManager gameStateManager;
    private final LobbyCountdown lobbyCountdown;
    private final TeamHandler teamHandler;

    public LobbyState(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        lobbyCountdown = new LobbyCountdown(gameStateManager);
        teamHandler = new TeamHandler();
        minPlayers = gameStateManager.getInstance().getCacheContainer().getMinPlayers();
    }

    @Override
    public void start() {
        if(!Objects.requireNonNull(Bukkit.getWorld("world")).getEntities().isEmpty()) {
            (Bukkit.getWorld("world")).getEntities().forEach((Entity entity) -> {
                if(entity.getType().equals(EntityType.VILLAGER)) {
                    entity.remove();
                }
            });
        }
        lobbyCountdown.getLobbyIdle().runIdle();
    }

    @Override
    public void stop() {
        for(Player player : gameStateManager.getInstance().getPlayerList()) {
            if(teamHandler.getPlayerTeam(player) == null) {
                teamHandler.setPlayerTeam(player, Team8x1.values()[new Random().nextInt(Team8x1.values().length)]);
                player.sendMessage(Main.PREFIX + "Du wurdest Team " + teamHandler.getPlayerTeam(player).getColoredName() + " §rzugewiesen");
                player.setDisplayName(teamHandler.getPlayerTeam(player).getChatColor() + player.getDisplayName());
            }
        }
    }

    public LobbyCountdown getLobbyCountdown() {
        return lobbyCountdown;
    }

}
