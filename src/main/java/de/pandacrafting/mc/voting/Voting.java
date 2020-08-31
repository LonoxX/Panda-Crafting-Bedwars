package de.pandacrafting.mc.voting;

import de.pandacrafting.mc.main.Main;
import de.pandacrafting.mc.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Voting {

    public static final int MAP_AMOUNT = 3;
    public static final String GUI_NAME = "Mapvotingmenü";
    public static final String ITEM_NAME = "§e§lVoting";
    public static final Inventory GUI = Bukkit.createInventory(null, 9, GUI_NAME);
    public static final ItemStack VOTE_ITEM = new ItemBuilder(Material.BLAZE_ROD).setDisplayName(ITEM_NAME).build();

    private final List<Map> maps;
    private final Map[] votingMaps;
    private final int[] voteInvSequence;
    private final java.util.Map<String, Integer> voteWrapper;

    public Voting(List<Map> maps) {
        this.maps = maps;
        voteInvSequence = new int[]{2,4,6};
        votingMaps = new Map[MAP_AMOUNT];
        voteWrapper = new HashMap<>();
        choose();
        init();
    }

    private void choose() {
        for(int i = 0; i < votingMaps.length; i++) {
            Collections.shuffle(maps);
            votingMaps[i] = maps.remove(0);
        }
    }

    public void init() {
        for(int i = 0; i < votingMaps.length; i++) {
            Map currentMap = votingMaps[i];
            GUI.setItem(voteInvSequence[i], new ItemBuilder(Material.PAPER).setDisplayName("Map:§a " + currentMap.getName().toLowerCase()).setLore(" ", "§fBuilt by §e" + currentMap.getBuilder()).build());
        }
    }

    public Map getWinnerMap() {
        Map winnerMap = votingMaps[0];
        for(int i = 1; i < votingMaps.length; i++) {
            if(votingMaps[i].getVotes() >= winnerMap.getVotes()) {
                winnerMap = votingMaps[i];
            }
        }
        return winnerMap;
    }

    public void vote(Player player, int mapIndex) {
        if(!voteWrapper.containsKey(player.getDisplayName())) {
            votingMaps[mapIndex].addVote();
            player.closeInventory();
            player.sendMessage(Main.PREFIX + "Du hast für die Map §e" + votingMaps[mapIndex].getName().toLowerCase() + " §rabgestimmt");
            voteWrapper.put(player.getDisplayName(), mapIndex);
            init();
        } else {
            player.sendMessage(Main.PREFIX + "§6Du hast bereits für eine Map abgestimmt");
        }
    }

    public java.util.Map<String, Integer> getVoteWrapper() {
        return voteWrapper;
    }

    public List<Map> getMaps() {
        return maps;
    }

    public Map[] getVotingMaps() {
        return votingMaps;
    }

    public int[] getVoteInvSequence() {
        return voteInvSequence;
    }

}
