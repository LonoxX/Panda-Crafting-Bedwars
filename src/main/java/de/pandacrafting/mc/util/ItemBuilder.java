package de.pandacrafting.mc.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta itemMeta;

    public ItemBuilder(@NotNull Material material) {
        item = new ItemStack(material, 1);
        itemMeta = item.getItemMeta();
    }

    public ItemBuilder setDisplayName(@NotNull String name) {
        itemMeta.setDisplayName(name);
        return this;
    }

    public ItemBuilder setLore(@NotNull String... lore) {
        itemMeta.setLore(Arrays.asList(lore));
        return this;
    }

    public ItemBuilder setAmount(@NotNull int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(itemMeta);
        return item;
    }

}
