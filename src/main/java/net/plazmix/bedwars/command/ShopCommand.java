package net.plazmix.bedwars.command;

import net.plazmix.bedwars.component.GameInventory;
import net.plazmix.command.BaseCommand;
import org.bukkit.entity.Player;

public final class ShopCommand extends BaseCommand<Player> {

    public ShopCommand() {
        super("shop", "шоп");
    }

    @Override
    protected void onExecute(Player player, String[] args) {
        GameInventory.SHOP_FAVOURITES.createAndOpen(player);
    }
}