package de.pandacrafting.mc.countdowns;

import de.pandacrafting.mc.gamestates.GameState;
import de.pandacrafting.mc.gamestates.GameStateManager;
import de.pandacrafting.mc.gamestates.LobbyState;
import de.pandacrafting.mc.main.Main;
import de.pandacrafting.mc.voting.Map;
import de.pandacrafting.mc.voting.Voting;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class LobbyCountdown extends Countdown {

    private final GameStateManager gameStateManager;
    private final LobbyIdle lobbyIdle;
    private final int countdown;
    private final int delay;
    private volatile ScheduledExecutorService scheduler;
    private int seconds;
    private boolean idling;
    private boolean running;

    public LobbyCountdown(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        lobbyIdle = new LobbyIdle();
        countdown = gameStateManager.getInstance().getCacheContainer().getLobbyCountdownLength();
        delay = gameStateManager.getInstance().getCacheContainer().getIdleDelay();
        seconds = countdown;
    }

    @Override
    public void run() {
        if(!running) {
            running = true;
            Runnable countdown = () -> {
                switch(seconds) {
                    case 60: case 45: case 30: case 25: case 20: case 15: case 10: case 4: case 3: case 2:
                        Bukkit.broadcastMessage(Main.PREFIX + "Das Spiel startet in §e" + seconds + " §rSekunden!");
                        break;
                    case 5:
                        Voting voting = null;
                        try {
                            voting = gameStateManager.getInstance().getVoting();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                        Map winningMap;
                        if(voting != null) {
                            winningMap = voting.getWinnerMap();
                        } else {
                            List<Map> maps = null;
                            try {
                                maps = gameStateManager.getInstance().getMaps();
                            } catch(Exception e) {
                                e.printStackTrace();
                            }
                            Collections.shuffle(Objects.requireNonNull(maps));
                            winningMap = maps.get(0);
                        }
                        for(Player player : gameStateManager.getInstance().getPlayerList()) {
                            CraftPlayer cp = (CraftPlayer)player;
                            cp.sendTitle("§a" + winningMap.getName().toLowerCase() + " §rhat gewonnen", "§eBuilt by §r" + winningMap.getBuilder(), 0, 40, 0);
                            player.getInventory().clear();
                        }
                        Bukkit.broadcastMessage(Main.PREFIX + "Das Spiel startet in §e" + seconds + " §rSekunden!");
                        break;
                    case 1:
                        Bukkit.broadcastMessage(Main.PREFIX + "Das Spiel startet in §eeiner Sekunde!");
                        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
                        ScheduledFuture<?> task = service.schedule((Callable<Object>) () -> {
                            gameStateManager.setGameState(GameState.INGAME_STATE);
                            return true;
                        }, 1, TimeUnit.SECONDS);
                        scheduler.shutdownNow();
                        break;
                    /**
                    case 0:
                        gameStateManager.setGameState(GameState.INGAME_STATE);
                        break;
                     */
                }
                seconds--;
            };
            scheduler.scheduleWithFixedDelay(countdown, 0, 1, TimeUnit.SECONDS);
        }
    }

    @Override
    public void cancel() {
        if(running) {
            if(!scheduler.isShutdown()) {
                scheduler.shutdownNow();
                running = false;
                seconds = countdown;
            }
        }
    }

    public LobbyIdle getLobbyIdle() {
        return lobbyIdle;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isIdling() {
        return idling;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public class LobbyIdle implements Idle {

        private volatile ScheduledExecutorService idleScheduler;
        private ScheduledFuture<?> task;

        public LobbyIdle() {
            idleScheduler = Executors.newSingleThreadScheduledExecutor();
        }

        @Override
        public void runIdle() {
            if(!idling) {
                idling = true;
                Runnable idleTask = () -> {
                    if(LobbyState.minPlayers - gameStateManager.getInstance().getPlayerList().size() == 1) {
                        Bukkit.broadcastMessage(Main.PREFIX + "Bis zum Spielstart wird noch §eein Spieler §rbenötigt.");
                    } else {
                        Bukkit.broadcastMessage(Main.PREFIX + "Bis zum Spielstart fehlen noch §e" + (LobbyState.minPlayers - gameStateManager.getInstance().getPlayerList().size()) + " §rSpieler.");
                    }
                };
                idleScheduler.scheduleWithFixedDelay(idleTask, 0, delay, TimeUnit.SECONDS);
            }
        }

        @Override
        public void cancelIdle() {
            if(idling) {
                if(!idleScheduler.isShutdown()) {
                    idleScheduler.shutdownNow();
                    idling = false;
                }
            }
        }

    }

}
