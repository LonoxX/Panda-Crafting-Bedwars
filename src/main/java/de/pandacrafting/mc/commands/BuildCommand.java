package de.pandacrafting.mc.commands;

import com.google.inject.Inject;
import de.pandacrafting.mc.main.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildCommand implements CommandExecutor {

    @Inject
    private final Main instance;

    @Inject
    public BuildCommand(Main instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            var player = (Player) sender;
            if(player.hasPermission("pandabedwars.build")) {
                if(args.length == 0) {
                    if(!instance.getBuilder().contains(player.getName())) {
                        instance.getBuilder().add(player.getName());
                        player.sendMessage(Main.PREFIX + "Du kannst nun bauen.");
                    } else {
                        instance.getBuilder().remove(player.getName());
                        player.sendMessage(Main.PREFIX + "Du kannst nun nicht mehr bauen.");
                    }
                } else {
                    player.sendMessage(Main.PREFIX + "§6Es konnte kein command gefunden werden!");
                }
            } else {
                player.sendMessage(Main.PREFIX + "§6Du hast keinen Zugriff auf diesen command!");
            }
        }
        return false;
    }

}
