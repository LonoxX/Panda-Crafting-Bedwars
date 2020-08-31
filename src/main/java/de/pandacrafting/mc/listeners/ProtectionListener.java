package de.pandacrafting.mc.listeners;

import com.google.inject.Inject;
import de.pandacrafting.mc.gamestates.IngameState;
import de.pandacrafting.mc.main.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class ProtectionListener implements Listener {

    @Inject
    private final Main instance;

    @Inject
    public ProtectionListener(Main instance) {
        this.instance = instance;
    }

    @EventHandler
    public void handleDamage(EntityDamageEvent event) {
        if(instance.getGameStateManager().getCurrentGameState() instanceof IngameState) return;
        if(event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleInventoryDrag(InventoryDragEvent event) {
        if(instance.getGameStateManager().getCurrentGameState() instanceof IngameState) return;
        event.setCancelled(true);
        event.setResult(Result.DENY);
    }

    @EventHandler
    public void handleDestruction(BlockBreakEvent event) {
        if(instance.getGameStateManager().getInstance().getBuilder().contains(event.getPlayer().getName())) {
            if(instance.getGameStateManager().getCurrentGameState() instanceof IngameState) return;
            event.setDropItems(false);
            event.setCancelled(false);
        } else {
            event.setDropItems(false);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleWeather(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void handleBlockPlace(BlockPlaceEvent event) {
        if(instance.getGameStateManager().getInstance().getBuilder().contains(event.getPlayer().getName())) {
            event.setBuild(true);
            event.setCancelled(false);
        } else {
            event.setBuild(false);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleFoodLevel(FoodLevelChangeEvent event) {
        if(instance.getGameStateManager().getCurrentGameState() instanceof IngameState) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void handleCreatureSpawn(CreatureSpawnEvent event) {
        if(event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handleBedEnter(PlayerBedEnterEvent event) {
        event.setCancelled(true);
        event.setUseBed(Result.DENY);
    }

    @EventHandler
    public void handleItemDrop(PlayerDropItemEvent event) {
        if(instance.getGameStateManager().getCurrentGameState() instanceof IngameState) return;
        event.setCancelled(true);
    }

}
