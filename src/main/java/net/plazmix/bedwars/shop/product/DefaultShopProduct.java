package net.plazmix.bedwars.shop.product;

import lombok.NonNull;
import net.plazmix.PlazmixApi;
import net.plazmix.bedwars.component.Resource;
import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.inventory.button.impl.ActionInventoryButton;
import org.bukkit.inventory.ItemStack;

public final class DefaultShopProduct extends ShopProduct {

    public DefaultShopProduct(@NonNull ItemStack item, @NonNull Resource currency, int price) {
        super(item, currency, price);
    }

    @Override
    public BaseInventoryButton buildButton() {
        return new ActionInventoryButton(PlazmixApi.newItemBuilder(getIcon()).build(), (player, event) -> {
            if (!checkAndRemoveCurrency(player)) return;

            player.getInventory().addItem(getItem());
        });
    }
}