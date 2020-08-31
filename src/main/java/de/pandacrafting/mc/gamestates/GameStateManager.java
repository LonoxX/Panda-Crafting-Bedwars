package de.pandacrafting.mc.gamestates;

import com.google.inject.Inject;
import de.pandacrafting.mc.main.Main;

public class GameStateManager {

    @Inject
    private final Main instance;
    private static GameState[] gameStates;
    private static GameState currentGameState;

    @Inject
    public GameStateManager(Main instance) {
        this.instance = instance;
        gameStates = new GameState[3];
        gameStates[GameState.LOBBY_STATE] = new LobbyState(this);
        gameStates[GameState.INGAME_STATE] = new IngameState(this);
        gameStates[GameState.ENDING_STATE] = new EndingState(this);
    }

    public void setGameState(int stateIndex) {
        if(currentGameState != null) {
            currentGameState.stop();
        }
        currentGameState = gameStates[stateIndex];
        currentGameState.start();
    }

    public void stopGameStates() {
        if(currentGameState != null) {
            currentGameState.stop();
            currentGameState = null;
        }
    }

    public GameState getCurrentGameState() {
        return currentGameState;
    }

    public Main getInstance() {
        return instance;
    }

}
