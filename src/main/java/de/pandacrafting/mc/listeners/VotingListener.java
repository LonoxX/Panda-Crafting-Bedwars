package de.pandacrafting.mc.listeners;

import com.google.inject.Inject;
import de.pandacrafting.mc.gamestates.LobbyState;
import de.pandacrafting.mc.main.Main;
import de.pandacrafting.mc.voting.Voting;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class VotingListener implements Listener {

    @Inject
    private final Main instance;
    private final Voting voting;

    @Inject
    public VotingListener(Main instance) {
        this.instance = instance;
        voting = instance.getVoting();
    }

    @EventHandler
    public void handleVotingGui(PlayerInteractEvent event) {
        if(!(instance.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        if(!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) return;
        var player = event.getPlayer();
        var cp = (CraftPlayer)player;
        var item = cp.getItemInHand();
        if(item.getItemMeta() == null) return;
        if(item.getItemMeta().getDisplayName().equals(Voting.ITEM_NAME)) {
            player.openInventory(Voting.GUI);
        }
    }

    @EventHandler
    public void handleVoteClick(InventoryClickEvent event) {
        if(!(instance.getGameStateManager().getCurrentGameState() instanceof LobbyState)) return;
        if(!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        if(event.getClickedInventory() == null) return;
        if(!player.getOpenInventory().getTitle().equals(Voting.GUI_NAME)) return;
        event.setCancelled(true);
        for(int i = 0; i < voting.getVoteInvSequence().length; i++) {
            if(voting.getVoteInvSequence()[i] == event.getSlot()) {
                voting.vote(player, i);
            }
        }
    }

}
