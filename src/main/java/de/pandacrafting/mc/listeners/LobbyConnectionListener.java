package de.pandacrafting.mc.listeners;

import com.google.inject.Inject;
import de.pandacrafting.mc.gamestates.LobbyState;
import de.pandacrafting.mc.main.Main;
import de.pandacrafting.mc.teamhandler.TeamHandler;
import de.pandacrafting.mc.util.ConfigLocUtil;
import de.pandacrafting.mc.util.UUIDFetcher;
import de.pandacrafting.mc.voting.Voting;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_13_R2.PlayerConnection;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.RenderType;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LobbyConnectionListener implements Listener {

    @Inject
    private final Main instance;

    @Inject
    public LobbyConnectionListener(Main instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handlePlayerJoin(PlayerJoinEvent event) {
        if(!(instance.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        var player = event.getPlayer();
        var cp = (CraftPlayer) player;
        var lobbyState = (LobbyState) instance.getGameStateManager().getCurrentGameState();
        var lobbyCountdown = lobbyState.getLobbyCountdown();

        instance.getPlayerList().add(player);
        event.setJoinMessage(Main.PREFIX + "§e" + player.getDisplayName() + " §rist dem Spiel beigetreten! §e[" + instance.getPlayerList().size() + "/" + LobbyState.MAX_PLAYERS + "]");
        setBoard(player);

        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.submit(() -> {
            player.getWorld().setTime(1000);
            player.setHealth(20.0);
            player.setFoodLevel(20);
            player.getWorld().setThundering(false);
            player.getWorld().setStorm(false);
            player.setGameMode(GameMode.SURVIVAL);
            Bukkit.getOnlinePlayers().parallelStream().forEach((Player current) -> {
                current.showPlayer(instance, player);
                player.showPlayer(instance, current);
            });
        });

        executor.submit(() -> {
            cp.sendTitle("Joined §aPandaBedWars", null, 10, 60, 20);
            setTabDetails(player);
            player.getInventory().clear();
            player.getInventory().setItem(0, TeamHandler.SELECTOR);
            player.getInventory().setItem(8, Voting.VOTE_ITEM);
        });

        executor.submit(() -> {
           Validate.notNull((new ConfigLocUtil(instance, "lobby")).loadLocation(), "Die Location der Lobby muss gesetzt werden");
           ConfigLocUtil locUtil = new ConfigLocUtil(instance, "lobby");
           if(locUtil.loadLocation() != null) {
               player.teleport(Objects.requireNonNull(locUtil.loadLocation()));
           }
           if(instance.getPlayerList().size() >= LobbyState.minPlayers) {
               if(!lobbyCountdown.isRunning()) {
                   lobbyCountdown.getLobbyIdle().cancelIdle();
                   lobbyCountdown.run();
               }
           }
        });
        executor.shutdown();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void handlePlayerQuit(PlayerQuitEvent event) {
        if(!(instance.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        var player = event.getPlayer();
        var lobbyState = (LobbyState) instance.getGameStateManager().getCurrentGameState();
        var lobbyCountdown = lobbyState.getLobbyCountdown();
        instance.getPlayerList().remove(player);
        event.setQuitMessage(Main.PREFIX + "§e" + player.getDisplayName() + " §rhat das Spiel verlassen! §e[" + instance.getPlayerList().size() + "/" + LobbyState.MAX_PLAYERS + "]");
        if(instance.getPlayerList().size() < LobbyState.minPlayers) {
            if(lobbyCountdown.isRunning()) {
                lobbyCountdown.cancel();
                lobbyCountdown.getLobbyIdle().runIdle();
            }
        }
        if(instance.getVoting() != null) {
            Voting voting = instance.getVoting();
            if(voting.getVoteWrapper().containsKey(player.getCustomName())) {
                voting.getVotingMaps()[voting.getVoteWrapper().get(player.getCustomName())].removeVote();
                voting.init();
            }
        }
    }

    @EventHandler
    public void handleAccessibility(PlayerLoginEvent event) throws Exception {
        if(instance.getGameStateManager().getCurrentGameState() instanceof LobbyState || instance.getPlayerList().size() != LobbyState.MAX_PLAYERS) {
            var jedis = instance.getJedis();
            var name = event.getPlayer().getName();
            var fetcher = new UUIDFetcher(Collections.singletonList(name));
            var uuid = fetcher.getUUIDOf(name);
            var address = event.getRealAddress();
            var ip = String.valueOf(address);
        } else {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage("Du kannst das Spiel derzeit nicht betreten");
        }

    }

    private void setTabDetails(Player player) {
        var headerComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + "§aPanda§eCrafting §bBedWars\n§7Wende dich bei Fragen an unsere Supporter!" + "\"}");
        var footerComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + "§7Besuche unsere Website unter §6panda-crafting.de\n§7Ready for take off D33VIL#0974" + "\"}");

        var packet = new PacketPlayOutPlayerListHeaderFooter();
        packet.footer = footerComponent;
        packet.header = headerComponent;

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        connection.sendPacket(packet);
    }

    private void setBoard(Player player) {
        var scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        var objective = scoreboard.registerNewObjective("bedwars", "dummy", "§bPandaBW");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setRenderType(RenderType.HEARTS);
        objective.getScore("placeholder").setScore(3);
        objective.getScore("§7Teams: "+ChatColor.GOLD+"8x1").setScore(2);
        objective.getScore("placeholder 1").setScore(1);
        objective.getScore("placeholder 2").setScore(0);
        player.setScoreboard(scoreboard);
    }

}
