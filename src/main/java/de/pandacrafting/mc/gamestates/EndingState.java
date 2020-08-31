package de.pandacrafting.mc.gamestates;

import de.pandacrafting.mc.countdowns.EndingCountdown;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EndingState extends GameState {

    private final GameStateManager gameStateManager;
    private EndingCountdown endingCountdown;


    public EndingState(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        endingCountdown = new EndingCountdown(gameStateManager.getInstance());
    }

    @Override
    public void start() {
        endingCountdown.run();
    }

    @Override
    public void stop() {
        Bukkit.getOnlinePlayers().parallelStream().forEach((Player player) -> player.kickPlayer("Das Spiel ist abgeschlossen"));
        endingCountdown.cancel();
    }

}
