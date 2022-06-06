package net.plazmix.bedwars.upgrader.product;

import lombok.NonNull;
import net.plazmix.PlazmixApi;
import net.plazmix.bedwars.component.TeamUpgrade;
import net.plazmix.bedwars.upgrader.UpgradeProduct;
import net.plazmix.bedwars.util.GameConst;
import net.plazmix.game.user.GameUser;
import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.inventory.button.impl.ActionInventoryButton;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public final class TrapUpgradeProduct extends UpgradeProduct {

    public TrapUpgradeProduct(@NonNull ItemStack item, int price, boolean soldOff) {
        super(item, price, soldOff);
    }

    @Override
    public BaseInventoryButton buildButton() {
        return new ActionInventoryButton(PlazmixApi.newItemBuilder(getIcon()).setAmount(1).build(), (player, event) -> {
            int trap = GameUser.from(player).getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, () -> new HashMap<TeamUpgrade, Integer>()).getOrDefault(TeamUpgrade.TRAP, 0);

            if(trap == 1) {
                player.sendMessage(GameConst.PREFIX + "§cПредмет уже полностью прокачан!");
                return;
            }

            if (!checkAndRemoveCurrency(player)) return;

            GameUser.from(player).getCurrentTeam().getPlayers().forEach(gameUser -> gameUser.getBukkitHandle().sendMessage(GameConst.PREFIX + ChatColor.GREEN + event.getWhoClicked().getName() + " купил Ловушку " + (trap + 1)));

            HashMap<TeamUpgrade, Integer> trapHash = GameUser.from(player).getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, HashMap::new);
            trapHash.put(TeamUpgrade.TRAP, trap + 1);
            GameUser.from(player).getCurrentTeam().getCache().set(GameConst.TEAM_UPGRADES, trapHash);
        });
    }
}
