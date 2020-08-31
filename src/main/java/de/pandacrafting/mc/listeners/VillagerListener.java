package de.pandacrafting.mc.listeners;

import com.google.inject.Inject;
import de.pandacrafting.mc.gamestates.IngameState;
import de.pandacrafting.mc.main.Main;
import de.pandacrafting.mc.villager.ShopVillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class VillagerListener implements Listener {

    @Inject
    private final Main instance;

    @Inject
    public VillagerListener(Main instance) {
        this.instance = instance;
    }

    @EventHandler
    public void handleVillagerInteract(PlayerInteractEntityEvent event) {
        if(!(instance.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        var ingameState = (IngameState) instance.getGameStateManager().getCurrentGameState();
        var shop = (Villager) event.getRightClicked();
        if(!shop.getCustomName().equals(ShopVillager.VILLAGER_NAME)) return;
        event.setCancelled(true);
        var player = event.getPlayer();
        player.openInventory(ingameState.getShopVillager().getShopgui());
    }

    @EventHandler
    public void handleVillagerInventoryClick(InventoryClickEvent event) {
        if(!(instance.getGameStateManager().getCurrentGameState() instanceof IngameState)) return;
        if(!(event.getWhoClicked() instanceof Player)) return;
        var player = (Player) event.getWhoClicked();
        if(event.getClickedInventory() == null) return;
        if(!player.getOpenInventory().getTitle().equals(ShopVillager.SHOP_GUI_NAME)) return;
        event.setCancelled(true);
        switch(event.getSlot()) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            case 8:
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void handleVillagerDamage(EntityDamageByEntityEvent event) {
        if(instance.getGameStateManager().getCurrentGameState() instanceof IngameState) return;
        event.setCancelled(true);
    }

}
