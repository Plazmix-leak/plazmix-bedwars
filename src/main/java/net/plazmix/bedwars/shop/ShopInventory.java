package net.plazmix.bedwars.shop;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import net.plazmix.PlazmixApi;
import net.plazmix.bedwars.component.GameInventory;
import net.plazmix.bedwars.shop.product.ShopProduct;
import net.plazmix.game.user.GameUser;
import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.inventory.impl.BaseSimpleInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class ShopInventory extends BaseSimpleInventory {

    Map<Integer, Function<GameUser, BaseInventoryButton>> buttonFunctions = new LinkedHashMap<>();

    public ShopInventory(@NonNull String title) {
        super(String.format("§6§lМагазин §7| %s", title), 6);
    }

    public ShopInventory addProduct(int slot, @NonNull Function<GameUser, ShopProduct> buttonFunction) {
        buttonFunctions.put(slot, user -> buttonFunction.apply(user).buildButton());

        return this;
    }

    public ShopInventory addProduct(int slot, @NonNull ShopProduct product) {
        return addProduct(slot, user -> product);
    }

    @Override
    public void drawInventory(Player player) {
        update(player);

        ItemStack glassPane = PlazmixApi.newItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).build();

        for(int i = 1; i < 10; i++) {
            setOriginalItem(i, glassPane);
        }

        for(int i = 18; i < 29; i++) {
            setOriginalItem(i, glassPane);
        }

        setOriginalItem(36, glassPane);
        setOriginalItem(37, glassPane);
        setOriginalItem(45, glassPane);
        setOriginalItem(46, glassPane);
        setOriginalItem(54, glassPane);

        setClickItem(10, PlazmixApi.newItemBuilder(Material.NETHER_STAR).setName("§cБыстрая закупка").build(), (p, event) -> GameInventory.SHOP_FAVOURITES.createAndOpen(p));

        int slot = 11;

        for (ShopCategory category : ShopCategory.values()) {
            setClickItem(slot++, category.getCategoryIcon(), (p, event) -> category.createAndOpen(p));
        }
    }

    private void update(Player player) {
        buttonFunctions.forEach((key, value) -> setItem(key + 1, value.apply(GameUser.from(player))));
    }
}