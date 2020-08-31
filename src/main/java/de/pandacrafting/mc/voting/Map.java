package de.pandacrafting.mc.voting;

import com.google.inject.Inject;
import de.pandacrafting.mc.gamestates.LobbyState;
import de.pandacrafting.mc.main.Main;
import de.pandacrafting.mc.util.ConfigLocUtil;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Map {

    @Inject
    private final Main instance;
    private final String name;
    private final Location[] spawnLocations;
    private final Location[] villagerLocations;
    private final Location[] bedLocations;
    private final Location[] goldLocations;
    private final Location[] ironLocations;
    private final Location[] bronzeLocations;
    private String builder;
    private Location specLocation;
    private int votes;

    @Inject
    public Map(@NotNull Main instance, @NotNull String name) {
        this.instance = instance;
        this.name = name.toUpperCase();
        spawnLocations = new Location[LobbyState.MAX_PLAYERS];
        villagerLocations = new Location[LobbyState.MAX_PLAYERS];
        bedLocations = new Location[LobbyState.MAX_PLAYERS];
        bronzeLocations = new Location[LobbyState.MAX_PLAYERS];
        ironLocations = new Location[LobbyState.MAX_PLAYERS];
        goldLocations = new Location[4];
        if(exists()) {
            builder = instance.getConfig().getString("Maps." + name + ".Builder");
        }
    }

    public boolean exists() {
        return (instance.getConfig().getString("Maps." + name + ".Builder") != null);
    }

    public void create(@NotNull String builder) {
        this.builder = builder;
        instance.getConfig().set("Maps." + name + ".Builder", builder);
        instance.saveConfig();
    }

    public void load() {
        for(int i = 0; i < spawnLocations.length; i++) {
            spawnLocations[i] = new ConfigLocUtil(instance, "Maps." + name + "." + (i + 1)).loadLocation();
            villagerLocations[i] = new ConfigLocUtil(instance, "Maps." + name + ".Villager." + (i + 1)).loadLocation();
            bedLocations[i] = new ConfigLocUtil(instance, "Maps." + name + ".Beds." + (i + 1)).loadLocation();
            bronzeLocations[i] = new ConfigLocUtil(instance, "Maps." + name + ".Spawner.Bronze." + (i + 1)).loadLocation();
            ironLocations[i] = new ConfigLocUtil(instance, "Maps." + name + ".Spawner.Eisen." + (i + 1)).loadLocation();
        }
        for(int i = 0; i < goldLocations.length; i++) {
            goldLocations[i] = new ConfigLocUtil(instance, "Maps." + name + ".Spawner.Gold" + (i + 1)).loadLocation();
        }
        specLocation = new ConfigLocUtil(instance, "Maps." + name + "Spectator").loadLocation();
    }

    public boolean playable() {
        ConfigurationSection section = instance.getConfig().getConfigurationSection("Maps." + name);
        if(!Objects.requireNonNull(section).contains(".Spectator")) return false;
        if(!section.contains(".Builder")) return false;
        for(int i = 1; i < LobbyState.MAX_PLAYERS; i++) {
            if(!section.contains(Integer.toString(i))) return false;
            if(!section.contains(".Villager." + i)) return false;
            if(!section.contains(".Beds." + i)) return false;
        }
        for(int i = 1; i < bronzeLocations.length; i++) {
            if(!section.contains(".Spawner.Bronze." + i)) return false;
            if(!section.contains(".Spawner.Iron." + i)) return false;
        }
        for(int i = 1; i < goldLocations.length; i++) {
            if(!section.contains(".Spawner.Gold." + i)) return false;
        }
        return true;
    }

    public void setSpecLocation(@NotNull Location location) {
        specLocation = location;
        new ConfigLocUtil(instance, "Maps." + name + ".Spectator", location).saveLocation();
    }

    public void setSpawnLocation(@NotNull int spawnIndex, @NotNull Location location) {
        spawnLocations[spawnIndex - 1] = location;
        new ConfigLocUtil(instance, "Maps." + name + "." + spawnIndex, location).saveLocation();
    }

    public void setVillegerLocations(@NotNull int spawnIndex, @NotNull Location location) {
        villagerLocations[spawnIndex - 1] = location;
        new ConfigLocUtil(instance, "Maps." + name + ".Villager." + spawnIndex, location).saveLocation();
    }

    public void setBedLocation(@NotNull int spawnIndex, @NotNull Location location, @NotNull BlockFace face) {
        bedLocations[spawnIndex - 1] = location;
        FileConfiguration config = instance.getConfig();
        config.set("Maps." + name + ".Beds." + spawnIndex + ".World", location.getWorld().getName());
        config.set("Maps." + name + ".Beds." + spawnIndex + ".X", location.getX());
        config.set("Maps." + name + ".Beds." + spawnIndex + ".Y", location.getY());
        config.set("Maps." + name + ".Beds." + spawnIndex + ".Z", location.getZ());
        config.set("Maps." + name + ".Beds." + spawnIndex + ".Pitch", location.getPitch());
        config.set("Maps." + name + ".Beds." + spawnIndex + ".Yaw", location.getYaw());
        config.set("Maps." + name + ".Beds." + spawnIndex + ".Face", face.name());
        instance.saveConfig();
    }

    public void setGoldLocations(@NotNull int spawnIndex, @NotNull Location location) {
        goldLocations[spawnIndex - 1] = location;
        new ConfigLocUtil(instance, "Maps." + name + ".Spawner.Gold." + spawnIndex, location).saveLocation();
    }

    public void setIronLocations(@NotNull int spawnIndex, @NotNull Location location) {
        ironLocations[spawnIndex - 1] = location;
        new ConfigLocUtil(instance, "Maps." + name + ".Spawner.Iron." + spawnIndex, location).saveLocation();
    }

    public void setBronzeLocations(@NotNull int spawnIndex, @NotNull Location location) {
        bronzeLocations[spawnIndex - 1] = location;
        new ConfigLocUtil(instance, "Maps." + name + ".Spawner.Bronze." + spawnIndex, location).saveLocation();
    }

    public void addVote() {
        votes++;
    }

    public void removeVote() {
        votes--;
    }

    public String getName() {
        return name;
    }

    public String getBuilder() {
        return builder;
    }

    public Location[] getBedLocations() {
        return bedLocations;
    }

    public Location[] getBronzeLocations() {
        return bronzeLocations;
    }

    public Location[] getSpawnLocations() {
        return spawnLocations;
    }

    public Location[] getVillagerLocations() {
        return villagerLocations;
    }

    public Location[] getGoldLocations() {
        return goldLocations;
    }

    public Location[] getIronLocations() {
        return ironLocations;
    }

    public Location getSpecLocation() {
        return specLocation;
    }

    public int getVotes() {
        return votes;
    }

}
