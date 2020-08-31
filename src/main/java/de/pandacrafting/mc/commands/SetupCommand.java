package de.pandacrafting.mc.commands;

import com.google.inject.Inject;
import de.pandacrafting.mc.gamestates.LobbyState;
import de.pandacrafting.mc.main.Main;
import de.pandacrafting.mc.util.ConfigLocUtil;
import de.pandacrafting.mc.voting.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand implements CommandExecutor {

    @Inject
    private final Main instance;

    @Inject
    public SetupCommand(Main instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            var player = (Player)sender;
            if(args[0].equalsIgnoreCase("lobby")) {
                if(args.length == 1) {
                    new ConfigLocUtil(instance, "lobby", player.getLocation()).saveLocation();
                    player.sendMessage(Main.PREFIX + "Die Location der Lobby wurde gesetzt.");
                } else {
                    player.sendMessage(Main.PREFIX + "§6Es konnte kein command gefunden werden!");
                }
            } else if(args[0].equalsIgnoreCase("create")) {
                if(args.length == 3) {
                    var map = new Map(instance, args[1]);
                    if(!map.exists()) {
                        map.create(args[2]);
                        player.sendMessage(Main.PREFIX + "Die Map §e" + map.getName()+ " §rwurde erfolgreich erstellt.");
                    } else {
                        player.sendMessage(Main.PREFIX + "§6Es existiert bereits eine Map unter diesem Namen!");
                    }
                } else {
                    player.sendMessage(Main.PREFIX + "§6Es konnte kein command gefunden werden!");
                }
            } else if(args[0].equalsIgnoreCase("set")) {
                if(args.length == 3) {
                    var map = new Map(instance, args[1]);
                    if(map.exists()) {
                        try {
                            int index = Integer.parseInt(args[2]);
                            if(index > 0 && index <= LobbyState.MAX_PLAYERS) {
                                map.setSpawnLocation(index, player.getLocation());
                                player.sendMessage(Main.PREFIX+ "Die Location §e" + index + " §rwurde für die Map §e" + map.getName() + " §rgespeichert.");
                            } else {
                                player.sendMessage(Main.PREFIX+ "§6Es werden auschließlich Zahlen zwischen §e1 §6und §e" + LobbyState.MAX_PLAYERS + " §6akzeptiert!");
                            }
                        } catch(NumberFormatException e) {
                            if(args[2].equalsIgnoreCase("spectator")) {
                                map.setSpecLocation(player.getLocation());
                                player.sendMessage(Main.PREFIX+ "Du hast die Location der Spectator für die Map §e" + map.getName() + " §rgesetzt.");
                            } else {
                                player.sendMessage(Main.PREFIX+ "§6Es konnte kein command gefunden werden!");
                            }
                        }
                    } else {
                        player.sendMessage(Main.PREFIX + "§6Eine Map unter diesem Namen existiert nicht!");
                    }
                } else if(args.length == 4) {
                    var map = new Map(instance, args[1]);
                    try {
                        int index  = Integer.parseInt(args[2]);
                        if(index > 0 && index <= LobbyState.MAX_PLAYERS && args[3].equalsIgnoreCase("villager")) {
                            map.setVillegerLocations(index, player.getLocation());
                            player.sendMessage(Main.PREFIX + "Du hast die Location des Villagers §e" + index + " §rfür die Map §e" + map.getName() + " §rgesetzt.");
                        } else if(index > 0 && index <= LobbyState.MAX_PLAYERS && args[3].equalsIgnoreCase("bed")) {
                            map.setBedLocation(index, player.getLocation(), player.getFacing());
                            player.sendMessage(Main.PREFIX + "Du hast die Location für das Bett §e" + index + " §rauf der Map §e" + map.getName() + " §rgespeichert.");
                        } else if(index > 0 && index <= map.getBronzeLocations().length && args[3].equalsIgnoreCase("bronze")) {
                            map.setBronzeLocations(index, player.getLocation());
                            player.sendMessage(Main.PREFIX + "Du hast den Bronzespawner §e" + index + " §rfür die Map §e" + map.getName() + " §rgesetzt.");
                        } else if(index > 0 && index <= map.getGoldLocations().length && args[3].equalsIgnoreCase("gold")) {
                            map.setGoldLocations(index, player.getLocation());
                            player.sendMessage(Main.PREFIX + "Du hast den Goldspawner §e" + index +  " §rfür die Map §e" + map.getName() + " §rgesetzt.");
                        } else if(index > 0 && index <= map.getIronLocations().length && args[3].equalsIgnoreCase("eisen")) {
                            map.setIronLocations(index, player.getLocation());
                            player.sendMessage(Main.PREFIX + "Du hast den Eisenspawner §e" + index + " §rfür die Map §e" + map.getName() + " §rgesetzt.");
                        } else {
                            player.sendMessage(Main.PREFIX + "§6Es werden auschließlich Zahlen zwischen §e1 §6und §e" + LobbyState.MAX_PLAYERS + " §6akzeptiert!");
                        }
                    } catch(NumberFormatException e) {
                        player.sendMessage(Main.PREFIX + "§6Es konnte kein command gefunden werden!");
                    }
                } else {
                    player.sendMessage(Main.PREFIX + "§6Es konnte kein command gefunden werden!");
                }
            }
        }
        return false;
    }

}
