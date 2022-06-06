package net.plazmix.bedwars.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.plazmix.PlazmixApi;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class ItemUtil {

    public void decrementAmount(@NonNull Inventory inventory, @NonNull ItemStack item) {
        int amount = item.getAmount();

        if (amount == 1) {
            inventory.remove(item);
        } else {
            item.setAmount(amount - 1);
        }
    }

    public static ItemStack unbreakable(@NonNull Material material, int amount) {
        return PlazmixApi.newItemBuilder(material).setAmount(amount).setUnbreakable(true).addItemFlag(ItemFlag.HIDE_UNBREAKABLE).build();
    }

    public static ItemStack unbreakable(@NonNull Material material) {
        return unbreakable(material, 1);
    }

    public static ItemStack unbreakableEnchanted(@NonNull Material material, @NonNull Enchantment enchantment, int level) {
        return PlazmixApi.newItemBuilder(unbreakable(material)).addEnchantment(enchantment, level).build();
    }
}