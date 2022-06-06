package net.plazmix.bedwars.upgrader;

import lombok.Data;
import lombok.NonNull;
import net.plazmix.bedwars.util.GameConst;
import net.plazmix.game.user.GameUser;
import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.utility.NumberUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static net.plazmix.PlazmixApi.newItemBuilder;

@Data
public abstract class UpgradeProduct {
    ItemStack item;
    int price;
    ItemStack icon;

    public UpgradeProduct(@NonNull ItemStack item, int price, boolean soldOff) {
        this.item = item;
        this.price = price;
        String currencyName = NumberUtil.formattingSpaced(price, "Поинт", "Поинта", "Поинтов");

        this.icon = newItemBuilder(item.clone()).addLore("§a").addLore("  §e§lЦена:§b " + currencyName).build();

        if(soldOff)
            this.icon = newItemBuilder(item.clone()).addLore("§a").addLore("  §с§lПредмет полностью прокачан!").build();
    }

    public abstract BaseInventoryButton buildButton();

    protected boolean checkAndRemoveCurrency(@NonNull Player player) {
        int points = GameUser.from(player).getCache().getInt(GameConst.PLAYER_EARNED_POINTS);
        boolean result = price <= points;


        if (!result)
            player.sendMessage(GameConst.PREFIX + "§cНедостаточно поинтов для покупки данной прокачки");
        else
            GameUser.from(player).getCache().set(GameConst.PLAYER_EARNED_POINTS, GameUser.from(player).getCache().getInt(GameConst.PLAYER_EARNED_POINTS) - price);

        return result;
    }
}
