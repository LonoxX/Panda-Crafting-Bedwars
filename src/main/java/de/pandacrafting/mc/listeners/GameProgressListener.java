package de.pandacrafting.mc.listeners;

import com.google.inject.Inject;
import de.pandacrafting.mc.gamestates.IngameState;
import de.pandacrafting.mc.main.Main;
import de.pandacrafting.mc.teamhandler.TeamHandler;
import net.minecraft.server.v1_13_R2.PacketPlayInClientCommand;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;

public class GameProgressListener implements Listener {

    @Inject
    private final Main instance;
    private final TeamHandler teamHandler;
    private List<Player> destroyed;

    @Inject
    public GameProgressListener(Main instance) {
        this.instance = instance;
        teamHandler = new TeamHandler();
        destroyed = new ArrayList<>();
    }

    @EventHandler
    public void handlePlayerDeath(PlayerDeathEvent event) {
        if(!(instance.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        var ingameState = (IngameState) instance.getGameStateManager().getCurrentGameState();
        var victim = event.getEntity();
        var victimTeam = teamHandler.getPlayerTeam(victim);
        if(victim.getKiller() == null) return;
        var killer = victim.getKiller();
        var killerTeam = teamHandler.getPlayerTeam(killer);
        if(!destroyed.contains(victim)) {
            var spawn = ingameState.getSpawns().get(victim);
            Bukkit.broadcastMessage(Main.PREFIX + killerTeam.getChatColor() + killer.getName() + " §rhat den Spieler " + victimTeam.getChatColor() + victim.getName() + " §rgetötet!");
            victim.teleport(spawn);
        } else {
            var packet = new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN);
            ((CraftPlayer) victim).getHandle().playerConnection.a(packet);

            victim.getInventory().setChestplate(null);
            victim.getInventory().setBoots(null);
            victim.getInventory().setHelmet(null);
            victim.getInventory().setLeggings(null);
            victim.getInventory().clear();

            ingameState.setSpectators(victim);

            Bukkit.broadcastMessage(Main.PREFIX + victimTeam.getColoredName() + " §cwurde eliminiert!");
        }
    }

    @EventHandler
    public void handleBedDestroy(BlockBreakEvent event) {
        if(!(instance.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        event.setDropItems(false);
        var ingameState = (IngameState) instance.getGameStateManager().getCurrentGameState();
        var player = event.getPlayer();
        var playerTeam = teamHandler.getPlayerTeam(player);
        var bed = event.getBlock();
        if(bed.getLocation().equals(ingameState.getBedHandler().getBottomBedWrapper().get(player))) {
            player.sendMessage(Main.PREFIX + "§6Du kannst dein eigenes Bett nicht zerstören!");
            event.setCancelled(true);
        } else if(bed.getLocation().equals(ingameState.getBedHandler().getPlayerTopBed(player))) {
            player.sendMessage(Main.PREFIX + "§6Du kannst dein eigenes Bett nicht zerstören!");
            event.setCancelled(true);
        } else {
            if(ingameState.getBedHandler().getTopBedWrapper().inverse().get(bed) != null) {
                var enemy = ingameState.getBedHandler().getTopBedWrapper().inverse().get(bed);
                var enemyTeam = teamHandler.getPlayerTeam(enemy);
                Bukkit.broadcastMessage(Main.PREFIX + "§cDas Bett von " + playerTeam.getColoredName() + " §cwurde von " + enemyTeam.getChatColor() + enemy.getName() + " §cabgebaut!");
                destroyed.add(player);
            } else if(ingameState.getBedHandler().getBottomBedWrapper().inverse().get(bed) != null) {
                var enemy = ingameState.getBedHandler().getBottomBedWrapper().inverse().get(bed);
                var enemyTeam = teamHandler.getPlayerTeam(enemy);
                Bukkit.broadcastMessage(Main.PREFIX + "§cDas Bett von " + playerTeam.getColoredName() + " §cwurde von " + enemyTeam.getChatColor() + enemy.getName() + " §cabgebaut!");
                destroyed.add(player);
            }
        }
    }

    @EventHandler
    public void handlePlayerQuit(PlayerQuitEvent event) {
        if(!(instance.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        var ingameState = (IngameState) instance.getGameStateManager().getCurrentGameState();
        var player = event.getPlayer();
        var team = teamHandler.getPlayerTeam(player);
        if(instance.getPlayerList().contains(event.getPlayer())) {
            instance.getPlayerList().remove(player);
            ingameState.watchGameEnd(teamHandler);
            event.setQuitMessage(team.getChatColor() + player.getDisplayName() + " §7hat das Spiel verlassen!");
        }
    }

}
