package de.pandacrafting.mc.gamestates;

import de.pandacrafting.mc.main.Main;
import de.pandacrafting.mc.teamhandler.Team8x1;
import de.pandacrafting.mc.teamhandler.TeamHandler;
import de.pandacrafting.mc.teamhandler.TeamHandler.BedHandler;
import de.pandacrafting.mc.villager.ShopVillager;
import de.pandacrafting.mc.voting.Map;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class IngameState extends GameState {

    private final BedHandler bedHandler;
    private final ShopVillager shopVillager;
    private final GameStateManager gameStateManager;
    private Map map;
    private List<Player> players;
    private List<Player> spectators;
    private Team8x1 winningTeam;
    private java.util.Map<Player, Location> spawns;

    public IngameState(GameStateManager gameStateManager) {
        this.gameStateManager = gameStateManager;
        bedHandler = new BedHandler();
        players = new ArrayList<>();
        shopVillager = new ShopVillager();
        spectators = new ArrayList<>();
        spawns = new HashMap<>();
    }

    @Override
    public void start() {
        Bukkit.broadcastMessage("test");
        Collections.shuffle(gameStateManager.getInstance().getPlayerList());
        players = gameStateManager.getInstance().getPlayerList();
        map = gameStateManager.getInstance().getVoting().getWinnerMap();
        map.load();

        players.forEach((Player player) -> {
            if(gameStateManager.getInstance().getBuilder().contains(player)) {
                gameStateManager.getInstance().getBuilder().remove(player);
            }
        });

        for(int i = 0; i < 8; i++) {
            Location villagerLoc = map.getVillagerLocations()[i];
            shopVillager.spawnShop(villagerLoc);
        }

        for(int i = 0; i < players.size(); i++) {
            var bedLoc = map.getBedLocations()[i];
            var config = gameStateManager.getInstance().getConfig();
            var face = config.getString("Maps." + map.getName().toUpperCase() + ".Beds." + (i + 1) + ".Face");
            var blockFace = BlockFace.valueOf(face);
            var foot = bedLoc.getBlock();
            var head = foot.getRelative(blockFace).getLocation().getBlock();
            foot.setBlockData(Bukkit.createBlockData(Material.YELLOW_BED, "[facing=" + face.toLowerCase() + ",occupied=false,part=foot]"));
            head.setBlockData(Bukkit.createBlockData(Material.YELLOW_BED, "[facing=" + face.toLowerCase() + ",occupied=false,part=head]"));
            foot.getState().update();
            head.getState().update();
            bedHandler.setPlayerTopBed(players.get(i), head.getLocation());
            bedHandler.setPlayerBottomBed(players.get(i), foot.getLocation());
        }

        for(int i = 0; i < players.size(); i++) {
            Location playerLoc = map.getSpawnLocations()[i];
            players.get(i).teleport(playerLoc);
            spawns.put(players.get(i), playerLoc);
        }
    }

    @Override
    public void stop() {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.execute(() -> Bukkit.broadcastMessage(Main.PREFIX + "§bDas Spiel ist beendet. Gewonnen hat " + winningTeam.getChatColor() + "Team " + winningTeam.getColoredName()));
        executorService.execute(() -> shopVillager.getVillager().parallelStream().forEach(Entity::remove));
        executorService.shutdown();
    }

    public void setSpectators(Player player) {
        spectators.add(player);
        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(map.getSpecLocation());
        Bukkit.getOnlinePlayers().forEach((Player spec) -> spec.hidePlayer(gameStateManager.getInstance(), player));
    }

    public void watchGameEnd(TeamHandler teamHandler) {
        if(gameStateManager.getInstance().getPlayerList().size() <= 1) {
            gameStateManager.getInstance().getGameStateManager().setGameState(GameState.ENDING_STATE);
            winningTeam = teamHandler.getPlayerTeam(gameStateManager.getInstance().getPlayerList().get(0));
        }
    }

    public BedHandler getBedHandler() {
        return bedHandler;
    }

    public ShopVillager getShopVillager() {
        return shopVillager;
    }

    public List<Player> getSpectators() {
        return spectators;
    }

    public java.util.Map<Player, Location> getSpawns() {
        return spawns;
    }

}
