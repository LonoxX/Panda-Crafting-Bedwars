package de.pandacrafting.mc.teamhandler;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public enum Team8x1 {

    RED("Rot", Color.RED, ChatColor.RED),
    GREEN("Grün", Color.GREEN, ChatColor.DARK_GREEN),
    LIGHT_BLUE("Türkis", Color.AQUA, ChatColor.AQUA),
    GRAY("Grau", Color.SILVER, ChatColor.GRAY),
    ORANGE("Orange", Color.ORANGE, ChatColor.GOLD),
    YELLOW("Gelb", Color.YELLOW, ChatColor.YELLOW),
    PINK("Pink", Color.FUCHSIA, ChatColor.LIGHT_PURPLE),
    BLUE("Blau", Color.BLUE, ChatColor.DARK_BLUE);

    private String teamName;
    private ChatColor chatColor;
    private Color color;

    Team8x1(String teamName, Color color, ChatColor chatColor) {
        this.teamName = teamName;
        this.color = color;
        this.chatColor = chatColor;
    }

    public String getColoredName() {
        return chatColor+teamName;
    }

    public String getTeamName() {
        return teamName;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public Color getColor() {
        return color;
    }

    public void setChatColor(ChatColor chatColor) {
        this.chatColor = chatColor;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

}
