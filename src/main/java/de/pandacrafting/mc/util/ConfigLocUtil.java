package de.pandacrafting.mc.util;

import com.google.inject.Inject;
import de.pandacrafting.mc.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class ConfigLocUtil {

    @Inject
    private final Main instance;
    private final Location location;
    private final String root;

    @Inject
    public ConfigLocUtil(@NotNull Main instance, @NotNull String root, @Nullable Location location) {
        this.instance = instance;
        this.root = root;
        this.location = location;
    }

    public ConfigLocUtil(@NotNull Main instance, @NotNull String root) {
        this(instance, root, null);
    }

    public final void saveLocation() {
        var config = instance.getConfig();
        config.set(root + ".World", Objects.requireNonNull(location.getWorld()).getName());
        config.set(root + ".X", location.getX());
        config.set(root + ".Y", location.getY());
        config.set(root + ".Z", location.getZ());
        config.set(root + ".Pitch", location.getPitch());
        config.set(root + ".Yaw", location.getYaw());
        instance.saveConfig();
    }

    public final Location loadLocation() {
        var config = instance.getConfig();
        if(config.contains(root)) {
            var world = Bukkit.getWorld(Objects.requireNonNull(config.getString(root + ".World")));
            double x = config.getDouble(root + ".X"),
                    y = config.getDouble(root + ".Y"),
                    z = config.getDouble(root + ".Z");
            float pitch = (float) config.getDouble(root + ".Pitch");
            float yaw = (float) config.getDouble(root + ".Yaw");
            return new Location(world, x, y, z, yaw, pitch);
        } else {
            return null;
        }
    }

}
