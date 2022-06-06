package net.plazmix.bedwars.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.plazmix.bedwars.PlazmixBedWars;
import net.plazmix.bedwars.component.Resource;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

@UtilityClass
public class MathUtil {

    public long getResourceGenerationDelay(@NonNull Resource resource) {
        return (long) (resource.getGenerationDelay() *
                        PlazmixBedWars.getInstance().getBedWarsMode().getResourceGenerationMultiplier() *
                        PlazmixBedWars.getInstance().getConfig().getDouble("map-generator-multiplier"));
    }

    public static int getSlot(int line, int slot) {
        return (line - 1) * 9 + slot;
    }

    public int asYaw(@NonNull String direction) {
        switch (direction) {
            case "EAST": return -90;
            case "NORTH": return -180;
            case "WEST": return 90;
            case "SOUTH": return 0;
            default: throw new IllegalArgumentException();
        }
    }
}