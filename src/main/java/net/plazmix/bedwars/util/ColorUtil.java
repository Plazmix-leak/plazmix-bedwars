package net.plazmix.bedwars.util;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

@UtilityClass
public class ColorUtil {

    public ChatColor asChatColor(@NonNull DyeColor dyeColor) {
        switch (dyeColor) {
            case RED:
            case ORANGE: return ChatColor.RED;
            case BLUE:
            case LIGHT_BLUE: return ChatColor.BLUE;
            case CYAN: return ChatColor.AQUA;
            case GRAY: return ChatColor.GRAY;
            case LIME:
            case GREEN: return ChatColor.GREEN;
            case PURPLE:
            case PINK: return ChatColor.LIGHT_PURPLE;
            case BLACK:
            case BROWN: return ChatColor.BLACK;
            case WHITE:
            case SILVER: return ChatColor.WHITE;
            case MAGENTA: return ChatColor.DARK_PURPLE;
            case YELLOW: return ChatColor.YELLOW;
            default: throw new IllegalArgumentException();
        }
    }

    public DyeColor asDyeColor(@NonNull ChatColor chatColor) {
        Preconditions.checkArgument(chatColor.isColor());

        switch (chatColor) {
            case BLUE:
            case DARK_BLUE: return DyeColor.BLUE;
            case YELLOW:
            case GOLD: return DyeColor.YELLOW;
            case WHITE: return DyeColor.WHITE;
            case RED:
            case DARK_RED: return DyeColor.RED;
            case BLACK: return DyeColor.BLACK;
            case GREEN:
            case DARK_GREEN: return DyeColor.GREEN;
            case GRAY:
            case DARK_GRAY: return DyeColor.GRAY;
            case DARK_PURPLE: return DyeColor.PURPLE;
            case AQUA:
            case DARK_AQUA: return DyeColor.CYAN;
            case LIGHT_PURPLE: return DyeColor.PINK;
            default: throw new IllegalArgumentException();
        }
    }

    public String asTeamColorName(@NonNull DyeColor dyeColor) {
        switch (dyeColor) {
            case BLUE:  return "Синяя";
            case YELLOW: return "Желтая";
            case WHITE: return "Белая";
            case RED: return "Красная";
            case GREEN: return "Зелёная";
            case GRAY: return "Серая";
            case PINK: return "Розовая";
            case CYAN: return "Голубая";
            case PURPLE: return "Фиолетовая";
            case BLACK: return "Чёрная";
            default: throw new IllegalArgumentException();
        }
    }

    public String format(@NonNull ChatColor color, boolean colored) {
        String raw = color.name().charAt(0) + color.name().substring(1).toLowerCase();

        return colored ? color + raw : raw;
    }
}