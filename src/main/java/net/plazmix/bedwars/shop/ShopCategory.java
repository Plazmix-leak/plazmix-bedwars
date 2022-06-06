package net.plazmix.bedwars.shop;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.plazmix.bedwars.shop.product.*;
import net.plazmix.bedwars.util.Functions;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.function.Consumer;

import static net.plazmix.PlazmixApi.newItemBuilder;
import static net.plazmix.bedwars.util.MathUtil.getSlot;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum ShopCategory {

    BLOCKS("§f§lБлоки", newItemBuilder(Material.WOOL).setName("§f§lБлоки").build(), inventory -> inventory
            .addProduct(getSlot(5, 1), ShopProduct.PRODUCT_MAP.get(1))
            .addProduct(getSlot(5, 2), ShopProduct.PRODUCT_MAP.get(2))
            .addProduct(getSlot(5, 3), ShopProduct.PRODUCT_MAP.get(3))
            .addProduct(getSlot(5, 5), ShopProduct.PRODUCT_MAP.get(4))
            .addProduct(getSlot(5, 6), ShopProduct.PRODUCT_MAP.get(5))
            .addProduct(getSlot(5, 7), ShopProduct.PRODUCT_MAP.get(6))),

    WEAPON("§c§lОружие", newItemBuilder(Material.IRON_SWORD).setName("§c§lОружие").build(), inventory -> inventory
            .addProduct(getSlot(4, 1), ShopProduct.PRODUCT_MAP.get(7))
            .addProduct(getSlot(4, 2), ShopProduct.PRODUCT_MAP.get(8))
            .addProduct(getSlot(4, 3), ShopProduct.PRODUCT_MAP.get(9))
            .addProduct(getSlot(4, 4), ShopProduct.PRODUCT_MAP.get(10))
            .addProduct(getSlot(4, 5), ShopProduct.PRODUCT_MAP.get(11))
            .addProduct(getSlot(4, 6), ShopProduct.PRODUCT_MAP.get(12))
            .addProduct(getSlot(4, 7), ShopProduct.PRODUCT_MAP.get(13))
            .addProduct(getSlot(5, 3), ShopProduct.PRODUCT_MAP.get(14))
            .addProduct(getSlot(5, 4), ShopProduct.PRODUCT_MAP.get(15))
            .addProduct(getSlot(5, 5), ShopProduct.PRODUCT_MAP.get(16))
            .addProduct(getSlot(6, 4), ShopProduct.PRODUCT_MAP.get(17))
            .addProduct(getSlot(5, 2), ShopProduct.PRODUCT_MAP.get(33))
            .addProduct(getSlot(5, 6), ShopProduct.PRODUCT_MAP.get(33))),

    ARMOR("§c§lЭкипировка", newItemBuilder(Material.IRON_CHESTPLATE).setName("§c§lЭкипировка").build(), inventory -> {
        int slot = 3;
        for (int i = 18; i <= 20; i++) {
            inventory.addProduct(getSlot(5, slot++), ShopProduct.PRODUCT_MAP.get(i));
        }
    }),

    TOOLS("§e§lИнструменты", newItemBuilder(Material.DIAMOND_PICKAXE).setName("§e§lИнструменты").build(), inventory -> inventory
            .addProduct(getSlot(5, 3), ShopProduct.PRODUCT_MAP.get(21))
            .addProduct(getSlot(5, 4), ShopProduct.PRODUCT_MAP.get(22))
            .addProduct(getSlot(5, 5), ShopProduct.PRODUCT_MAP.get(23))),

    POTIONS("§d§lЗелья", newItemBuilder(Material.POTION).setMainPotionEffect(PotionEffectType.INCREASE_DAMAGE).setName("§d§lЗелья").build(), inventory -> inventory
            .addProduct(getSlot(5, 3), ShopProduct.PRODUCT_MAP.get(24))
            .addProduct(getSlot(5, 4), ShopProduct.PRODUCT_MAP.get(25))

            .addProduct(getSlot(5, 5), ShopProduct.PRODUCT_MAP.get(26))),

    OTHER("§e§lОстальное", newItemBuilder(Material.GOLDEN_APPLE).setName("§e§lОстальное").build(), inventory -> inventory
            .addProduct(getSlot(5, 2), ShopProduct.PRODUCT_MAP.get(27))
            .addProduct(getSlot(5, 3), ShopProduct.PRODUCT_MAP.get(28))
            .addProduct(getSlot(5, 4), ShopProduct.PRODUCT_MAP.get(29))
            .addProduct(getSlot(5, 5), ShopProduct.PRODUCT_MAP.get(30))
            .addProduct(getSlot(5, 6), ShopProduct.PRODUCT_MAP.get(31))
            .addProduct(getSlot(5, 7), ShopProduct.PRODUCT_MAP.get(32)));

    @NonNull String title;
    @NonNull ItemStack categoryIcon;
    @NonNull Consumer<ShopInventory> pageEditor;

    public void createAndOpen(@NonNull Player player) {
        Functions.accept(new ShopInventory(title), pageEditor).openInventory(player);
    }
}