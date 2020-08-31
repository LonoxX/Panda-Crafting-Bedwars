package de.pandacrafting.mc.villager;

import de.pandacrafting.mc.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ShopVillager {

    public static final String VILLAGER_NAME = "§eShop";
    public static final String SHOP_GUI_NAME = "Villagershop";

    private static List<Entity> villager;

    public ShopVillager() {
        villager = new ArrayList<>();
    }

    public void spawnShop(Location location) {
        Villager shop = (Villager) location.getWorld().spawnEntity(location, EntityType.VILLAGER);
        villager.add(shop);
        shop.setCustomName(VILLAGER_NAME);
        shop.setCustomNameVisible(true);
        shop.setAI(false);
        shop.setAdult();
        shop.setCanPickupItems(false);
        shop.setInvulnerable(true);
        shop.setSilent(true);
    }

    public Inventory getShopgui() {
        var shopgui = Bukkit.createInventory(null, 9, SHOP_GUI_NAME);
        shopgui.setItem(0, new ItemBuilder(Material.SANDSTONE)
                .setDisplayName(ChatColor.GOLD + "Blöcke").setLore("§7Erwerbe Blöcke um deine Basis zu erweitern und zu Gegnern zu gelangen").build());
        shopgui.setItem(1, new ItemBuilder(Material.IRON_BOOTS)
                .setDisplayName(ChatColor.GOLD + "Rüstung").setLore("§7Stärke dich im Krieg um die Betten, durch das Tragen von Rüstung").build());
        shopgui.setItem(2, new ItemBuilder(Material.STONE_AXE)
                .setDisplayName(ChatColor.GOLD + "Werkzeug").setLore("§7Beschaffe dir hier Werkzeug, um den Weg zum Sieg frei zu machen").build());
        shopgui.setItem(3, new ItemBuilder(Material.IRON_SWORD)
                .setDisplayName(ChatColor.GOLD + "Waffen").setLore("§7Gehe in die Offensive mit den stärksten Waffen, welche du hier kaufen kannst").build());
        shopgui.setItem(4, new ItemBuilder(Material.BOW)
                .setDisplayName(ChatColor.GOLD + "Bögen").setLore("§7Schütze dich vor feindlichen Angriffen durch Bögen und unendlich Pfeile").build());
        shopgui.setItem(5, new ItemBuilder(Material.PORKCHOP)
                .setDisplayName(ChatColor.GOLD + "Nahrung").setLore("§7Regeneriere Herzen, indem du diverse Nahrungsmittel zu dir nimmst").build());
        shopgui.setItem(6, new ItemBuilder(Material.BEACON)
                .setDisplayName(ChatColor.GOLD + "Tränke").setLore("§7Gelangst du in eine enge Situation, nehme einen Trank konter Feinde aus").build());
        shopgui.setItem(7, new ItemBuilder(Material.TRAPPED_CHEST)
                .setDisplayName(ChatColor.GOLD + "Kisten").setLore("§7Erweitere dein Lager, durch die Benutzung von Kisten").build());
        shopgui.setItem(8, new ItemBuilder(Material.BLAZE_ROD)
                .setDisplayName(ChatColor.GOLD + "Besonderes").setLore("§7Kaufe dir hier magische Items und genieße die Vorteile").build());
        for(int i = 0; i < shopgui.getSize(); i++) {
            ItemMeta meta = shopgui.getItem(i).getItemMeta();
            meta.addItemFlags(ItemFlag.values());
            shopgui.getItem(i).setItemMeta(meta);
        }
        return shopgui;
    }

    public List<Entity> getVillager() {
        return villager;
    }

}
