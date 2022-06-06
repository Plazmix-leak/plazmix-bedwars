package net.plazmix.bedwars.component.inventory;

import net.plazmix.bedwars.shop.ShopInventory;
import net.plazmix.bedwars.shop.favorites.PlayerFavorites;
import net.plazmix.bedwars.shop.product.ShopProduct;
import net.plazmix.game.user.GameUser;
import org.bukkit.entity.Player;

import java.util.List;

import static net.plazmix.bedwars.util.MathUtil.getSlot;

public final class ShopFavourites extends ShopInventory {

    public ShopFavourites() {
        super("Быстрая закупка");
    }

    @Override
    public void drawInventory(Player player) {
        PlayerFavorites playerFavorites = GameUser.from(player).getCache().get(PlayerFavorites.KEY, PlayerFavorites.class);
        if (playerFavorites == null) {
            playerFavorites = PlayerFavorites.DEFAULT;
        }
        List<Integer> entries = playerFavorites.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            if (i < 7)
                addProduct(getSlot(4, 1 + i), ShopProduct.PRODUCT_MAP.get(entries.get(i)));
            else if (i < 14)
                addProduct(getSlot(5, 1 + (i - 7)), ShopProduct.PRODUCT_MAP.get(entries.get(i)));
            else if (i < 21)
                addProduct(getSlot(6, 1 + (i - 14)), ShopProduct.PRODUCT_MAP.get(entries.get(i)));
        }

        super.drawInventory(player);
    }
}