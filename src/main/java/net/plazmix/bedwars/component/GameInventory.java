package net.plazmix.bedwars.component;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.plazmix.bedwars.component.inventory.ShopFavourites;
import net.plazmix.bedwars.component.inventory.TeamChangerInventory;
import net.plazmix.bedwars.upgrader.UpgradeInventory;
import net.plazmix.game.user.GameUser;
import net.plazmix.inventory.BaseInventory;
import org.bukkit.entity.Player;

import java.util.function.Function;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum GameInventory {

    TEAM_CHANGER(user -> new TeamChangerInventory()),
    UPGRADER(user -> new UpgradeInventory()),
    SHOP_FAVOURITES(user -> new ShopFavourites());

    @NonNull Function<GameUser, BaseInventory> inventoryCreator;

    public void createAndOpen(@NonNull Player player) {
        inventoryCreator.apply(GameUser.from(player)).openInventory(player);
    }
}