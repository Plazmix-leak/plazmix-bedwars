package net.plazmix.bedwars.component;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.Material;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ArmorType {

    LEATHER(Material.LEATHER_CHESTPLATE, Material.LEATHER_BOOTS, Resource.BRONZE, 0, ""),
    GOLD(Material.GOLD_CHESTPLATE, Material.GOLD_BOOTS, Resource.BRONZE, 40, "§6§lКомплект золотой брони"),
    IRON(Material.IRON_CHESTPLATE, Material.IRON_BOOTS, Resource.RUBY, 12, "§f§lКомплект железной брони"),
    DIAMOND(Material.DIAMOND_CHESTPLATE, Material.DIAMOND_BOOTS, Resource.OPAL, 6, "§b§lКомплект алмазной брони");

    @NonNull Material chestplateType;
    @NonNull Material bootsType;
    @NonNull Resource currency;
    int price;
    @NonNull String title;
}