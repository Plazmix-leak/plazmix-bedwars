package net.plazmix.bedwars.shop.product;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import net.plazmix.bedwars.component.GameTool;
import net.plazmix.bedwars.util.GameConst;
import net.plazmix.game.user.GameUser;
import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.inventory.button.impl.ActionInventoryButton;
import net.plazmix.inventory.button.impl.SimpleInventoryButton;

import java.util.HashMap;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ToolShopProduct extends ShopProduct {

    GameTool.IGameTool current;

    public ToolShopProduct(@NonNull GameTool.IGameTool current) {
        super(current.isLast() ? BARRIER : current.getItem(), current.getCurrency(), current.getPrice());

        this.current = current;
    }

    @Override
    public BaseInventoryButton buildButton() {
        return current.isLast() ? new SimpleInventoryButton(BARRIER) : new ActionInventoryButton(icon, (player, event) -> {
            if (!checkAndRemoveCurrency(player)) return;

            GameTool.IGameTool next = current.next();
            if(!player.getInventory().contains(current.getItem()))
                player.getInventory().addItem(current.getItem());
            else
                player.getInventory().setItem(player.getInventory().first(current.getItem()), current.getItem());

            GameUser.from(player).getCache().compute(GameConst.PLAYER_TOOL_LEVELS, () -> new HashMap<GameTool.Type, GameTool.IGameTool>()).put(next.getType(), next);
        });
    }
}