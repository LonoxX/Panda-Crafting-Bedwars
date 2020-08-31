package de.pandacrafting.mc.teamhandler;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.pandacrafting.mc.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.Map;

public class TeamHandler {

    public static final String SELECTOR_GUI_NAME = "Teamauswahlmenü";
    public static final String SELECTOR_ITEM_NAME = "§a§lTeamauswahl";
    public static final ItemStack SELECTOR = new ItemBuilder(Material.GREEN_BED).setDisplayName(SELECTOR_ITEM_NAME).build();

    private static BiMap<String, Team8x1> teamWrapper;
    private final int[] invSequence;

    public TeamHandler() {
        teamWrapper = HashBiMap.create();
        invSequence = new int[]{0, 1, 2, 3, 5, 6, 7, 8};
    }

    public Inventory getSelectorGui() {
        Inventory selectorGui = Bukkit.createInventory(null, 9, SELECTOR_GUI_NAME);
        int i = 0;
        for(Team8x1 team : Team8x1.values()) {
            ItemStack hat = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta meta = (LeatherArmorMeta) hat.getItemMeta();
            if(teamWrapper.inverse().containsKey(team)) {
                meta.setLore(Arrays.asList("", team.getChatColor() + "+ §7" + teamWrapper.inverse().get(team), "", "§7Für einen Teambeitritt klicken!"));
            } else {
                meta.setLore(Arrays.asList("", "§7-", "", "§7Für einen Teambeitritt klicken!"));
            }
            meta.setColor(team.getColor());
            meta.setDisplayName(team.getColoredName());
            meta.addItemFlags(ItemFlag.values());
            hat.setItemMeta(meta);
            selectorGui.setItem(invSequence[i], hat);
            i++;
        }
        return selectorGui;
    }

    public Team8x1 getPlayerTeam(Player player) {
        return teamWrapper.containsKey(player.getName()) ? teamWrapper.get((player.getName())) : null;
    }

    public void setPlayerTeam(Player player, Team8x1 team) {
        teamWrapper.put(player.getName(), team);
        player.setPlayerListName(team.getColoredName() + " §7| " + team.getChatColor() + player.getName());
    }

    public Map<String, Team8x1> getTeamWrapper() {
        return teamWrapper;
    }

    public static class BedHandler {

        private static BiMap<Player, Location> topBedWrapper;
        private static BiMap<Player, Location> bottomBedWrapper;

        public BedHandler() {
            topBedWrapper = HashBiMap.create();
            bottomBedWrapper = HashBiMap.create();
        }

        public void setPlayerTopBed(Player player, Location location) {
            topBedWrapper.put(player, location);
        }

        public void setPlayerBottomBed(Player player, Location location) {
            bottomBedWrapper.put(player, location);
        }

        public Location getPlayerTopBed(Player player) {
            return topBedWrapper.getOrDefault(player, null);
        }

        public Location getPlayerBottomBed(Player player) {
            return bottomBedWrapper.getOrDefault(player, null);
        }

        public BiMap<Player, Location> getBottomBedWrapper() {
            return bottomBedWrapper;
        }

        public BiMap<Player, Location> getTopBedWrapper() {
            return topBedWrapper;
        }

    }

}
