package de.pandacrafting.mc.main;

import de.pandacrafting.mc.commands.BuildCommand;
import de.pandacrafting.mc.commands.SetupCommand;
import de.pandacrafting.mc.commands.StartCommand;
import de.pandacrafting.mc.gamestates.GameState;
import de.pandacrafting.mc.gamestates.GameStateManager;
import de.pandacrafting.mc.listeners.*;
import de.pandacrafting.mc.util.CacheLoader;
import de.pandacrafting.mc.voting.Map;
import de.pandacrafting.mc.voting.Voting;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends JavaPlugin {

    public static final String PREFIX = "§7[§eBed§aWars§7] §r";

    private static Jedis jedis;
    private CacheContainer cacheContainer;
    private Voting voting;
    private PluginManager pluginManager;
    private GameStateManager gameStateManager;
    private List<Player> playerList;
    private List<Map> maps;
    private List<String> builder;

    @Override
    public void onEnable() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            long oldTime = System.currentTimeMillis();
            Bukkit.getConsoleSender().sendMessage("§b[BW] §rEnabling the plugin...");
            pluginManager = Bukkit.getPluginManager();
            playerList = new ArrayList<>();
            maps = new ArrayList<>();
            builder = new ArrayList<>();
            connectRedis();
            cacheContainer = new CacheContainer();
            cacheContainer.loadConfigData();
            gameStateManager = new GameStateManager(this);
            init();
            long newTime = System.currentTimeMillis();
            Bukkit.getConsoleSender().sendMessage("§b[BW] §rReady for take off (" + (newTime - oldTime) + "ms)");
        });
        executor.shutdown();
    }

    @Override
    public void onDisable() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            long oldTime = System.currentTimeMillis();
            Bukkit.getConsoleSender().sendMessage("§b[BW] §rDisabling the plugin...");
            Bukkit.getConsoleSender().sendMessage("§b[Redis] §rDisconnecting from Redis database...");
            jedis.disconnect();
            saveConfig();
            long newTime = System.currentTimeMillis();
            Bukkit.getConsoleSender().sendMessage("§b[BW] §rPlease take your seats (" + (newTime - oldTime) + "ms)");
        });
        executor.shutdown();
    }

    private void connectRedis() {
        Bukkit.getConsoleSender().sendMessage("§b[Redis] §rConnecting to Redis database...");
        jedis = new Jedis("localhost", 6379);
        jedis.clientSetname("D33VIL");
        Bukkit.getConsoleSender().sendMessage("§b[Redis] §rRedis respond: §b" + jedis.ping());
        Validate.notNull(jedis.getClient());
        Bukkit.getConsoleSender().sendMessage("§b[Redis] §rConnected client: §b" + jedis.clientGetname());
    }

    private void init() {
        Bukkit.getConsoleSender().sendMessage("§b[BW] §rInitializing internal instances...");
        getCommand("setup").setExecutor(new SetupCommand(this));
        initVoting();
        pluginManager.registerEvents(new ProtectionListener(this), this);
        pluginManager.registerEvents(new TeamControlListener(this), this);
        getCommand("start").setExecutor(new StartCommand(this));
        gameStateManager.setGameState(GameState.LOBBY_STATE);
        pluginManager.registerEvents(new LobbyConnectionListener(this), this);
        pluginManager.registerEvents(new GameProgressListener(this), this);
        pluginManager.registerEvents(new VotingListener(this), this);
        pluginManager.registerEvents(new VillagerListener(this), this);
        getCommand("build").setExecutor(new BuildCommand(this));
    }

    private void initVoting() {
        for(String current : Objects.requireNonNull(getConfig().getConfigurationSection("Maps")).getKeys(false)) {
            var map = new Map(this, current);
            if(map.playable()) {
                maps.add(map);
            } else {
                System.out.print(map.getName());
            }
        }
        if(maps.size() >= Voting.MAP_AMOUNT) {
            voting = new Voting(maps);
        } else {
            Bukkit.getConsoleSender().sendMessage(PREFIX + "At least " + Voting.MAP_AMOUNT+ " Maps are needed to invoke a voting");
            voting = null;
        }
    }

    public List<String> getBuilder() {
        return builder;
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public Voting getVoting() {
        return voting;
    }

    public List<Map> getMaps() {
        return maps;
    }

    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }

    public Jedis getJedis() {
        return jedis;
    }

    public synchronized CacheContainer getCacheContainer() {
        return cacheContainer;
    }

    public static class CacheContainer implements CacheLoader {

        @Override
        public void loadConfigData() {
            File settingsFile = new File("plugins\\settings.yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(settingsFile);
            if(!settingsFile.exists()) {
                try {
                    settingsFile.createNewFile();
                    config.set("MIN_PLAYERS", 1);
                    config.set("LOBBYCOUNTDOWN_LENGTH", 60);
                    config.set("ENDINGCOUNTDOWN_LENGTH", 10);
                    config.set("IDLE_DELAY", 8);
                    config.set("FORCE_SECONDS", 5);
                    config.save(settingsFile);
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
            jedis.set("MIN_PLAYERS", config.getString("MIN_PLAYERS"));
            jedis.set("LOBBYCOUNTDOWN_LENGTH", config.getString("LOBBYCOUNTDOWN_LENGTH"));
            jedis.set("ENDINGCOUNTDOWN_LENGTH", config.getString("ENDINGCOUNTDOWN_LENGTH"));
            jedis.set("IDLE_DELAY", config.getString("IDLE_DELAY"));
            jedis.set("FORCE_SECONDS", config.getString("FORCE_SECONDS"));
        }

        @Override
        public int getMinPlayers() {
            return Integer.parseInt(jedis.get("MIN_PLAYERS"));
        }

        @Override
        public int getLobbyCountdownLength() {
            return Integer.parseInt(jedis.get("LOBBYCOUNTDOWN_LENGTH"));
        }

        @Override
        public int getEndingCountdownLength() {
            return Integer.parseInt(jedis.get("ENDINGCOUNTDOWN_LENGTH"));
        }

        @Override
        public int getIdleDelay() {
            return Integer.parseInt(jedis.get("IDLE_DELAY"));
        }

        @Override
        public int getForceSeconds() {
            return Integer.parseInt(jedis.get("FORCE_SECONDS"));
        }

    }

}
