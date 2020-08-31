package de.pandacrafting.mc.listeners;

import com.google.inject.Inject;
import de.pandacrafting.mc.gamestates.IngameState;
import de.pandacrafting.mc.gamestates.LobbyState;
import de.pandacrafting.mc.main.Main;
import de.pandacrafting.mc.teamhandler.Team8x1;
import de.pandacrafting.mc.teamhandler.TeamHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TeamControlListener implements Listener {

    @Inject
    private final Main instance;
    private final TeamHandler teamHandler;
    private final Map<Player, Integer> lore;

    @Inject
    public TeamControlListener(Main instance) {
        this.instance = instance;
        teamHandler = new TeamHandler();
        lore = new HashMap<>();
    }

    @EventHandler
    public void handlePlayerChat(AsyncPlayerChatEvent event) {
        var player = event.getPlayer();
        var team = teamHandler.getPlayerTeam(player);
        var message = event.getMessage();
        event.setCancelled(true);
        if(!(instance.getGameStateManager().getCurrentGameState() instanceof IngameState)) {
            Bukkit.broadcastMessage(ChatColor.GOLD+player.getDisplayName() + " §r>> " + message);
        } else {
            Bukkit.broadcastMessage("§7[" + team.getColoredName() + "§7] " + team.getChatColor() + player.getDisplayName() + "§r: " + message);
        }
    }

    @EventHandler
    public void handleSelectionInteract(PlayerInteractEvent event) {
        if(!(instance.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        if(!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) return;
        var player = event.getPlayer();
        var cp = (CraftPlayer)player;
        var item = cp.getItemInHand();
        if(item.getItemMeta() != null) {
            if(item.getItemMeta().getDisplayName().equals(TeamHandler.SELECTOR_ITEM_NAME)) {
                player.openInventory(teamHandler.getSelectorGui());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handleSelectionClick(InventoryClickEvent event) {
        if(!(instance.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        if(!(event.getWhoClicked() instanceof Player)) return;
        var player = (Player) event.getWhoClicked();
        if(event.getClickedInventory() == null) return;
        if(!player.getOpenInventory().getTitle().equals(TeamHandler.SELECTOR_GUI_NAME)) return;
        event.setCancelled(true);
        Team8x1 team;
        switch(event.getSlot()) {
        case 4:
            break;
        case 5: case 6: case 7: case 8:
            team = Team8x1.values()[event.getSlot() - 1];
            enterTeam(player, event.getSlot(), event.getClickedInventory(), event.getCurrentItem(), team);
            break;
        default:
            team = Team8x1.values()[event.getSlot()];
            enterTeam(player, event.getSlot(), event.getClickedInventory(), event.getCurrentItem(), team);
            break;
        }
    }

    private void enterTeam(Player player, int slot, Inventory inventory, ItemStack currentstack, Team8x1 team) {
        if(!teamHandler.getTeamWrapper().containsValue(team)) {
            teamHandler.setPlayerTeam(player, team);
            var current = (LeatherArmorMeta) currentstack.getItemMeta();
            current.setLore(Arrays.asList("", team.getChatColor() + "§l+ §7" + player.getName(), "", "§7Für einen Teambeitritt klicken!"));
            currentstack.setItemMeta(current);
            player.sendMessage(Main.PREFIX + "Du bist nun in Team "+team.getColoredName());
            if(lore.containsKey(player)) {
                var meta = (LeatherArmorMeta) inventory.getItem(lore.get(player)).getItemMeta();
                meta.setLore(Arrays.asList("", "", "", "§7Für einen Teambeitritt klicken!"));
                inventory.getItem(lore.get(player)).setItemMeta(meta);
            }
        } else {
            player.sendMessage(Main.PREFIX + "§6Das ausgewählte Team ist bereits besetzt!");
            player.closeInventory();
        }
        lore.put(player, slot);
        instance.getPlayerList().forEach(Player::updateInventory);
    }

}
