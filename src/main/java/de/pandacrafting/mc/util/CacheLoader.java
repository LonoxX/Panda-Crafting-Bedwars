package de.pandacrafting.mc.util;

public interface CacheLoader {

    void loadConfigData();

    int getMinPlayers();

    int getLobbyCountdownLength();

    int getEndingCountdownLength();

    int getIdleDelay();

    int getForceSeconds();

}
