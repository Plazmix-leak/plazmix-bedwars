package net.plazmix.bedwars.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum BedWarsMode {

    SOLO("Одиночный", 1, 8, 1.0, "BedwarsSolo"),
    DOUBLE("Двойной",2, 8, 1.5, "BedwarsDouble"),
    TRIO("Трио", 3, 4, 2.0, "BedwarsTrio"),
    QUADRO("Квадро", 4, 4, 2.5, "BedwarsQuadro");

    @NonNull String format;
    int teamSize;
    int teamCount;
    double resourceGenerationMultiplier;
    String sqlTable;
}