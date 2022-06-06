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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public final class FastDiggerUpgradeProduct extends UpgradeProduct {
    public FastDiggerUpgradeProduct(@NonNull ItemStack item, int price, boolean soldOff) {
        super(item, price, soldOff);
    }

    @Override
    public BaseInventoryButton buildButton() {
        return new ActionInventoryButton(PlazmixApi.newItemBuilder(getIcon()).setAmount(1).build(), (player, event) -> {
            int fastdigging = GameUser.from(player).getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, () -> new HashMap<TeamUpgrade, Integer>()).getOrDefault(TeamUpgrade.FAST_DIGGING, 0);

            if(fastdigging == 2) {
                player.sendMessage(GameConst.PREFIX + "§cПредмет уже полностью прокачан!");
                return;
            }

            if (!checkAndRemoveCurrency(player)) return;
            GameUser.from(player).getCurrentTeam().getPlayers().forEach(players -> {
                players.getBukkitHandle().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, fastdigging));
                players.getBukkitHandle().sendMessage(GameConst.PREFIX + ChatColor.GREEN + event.getWhoClicked().getName() + " купил Скорость копания " + (fastdigging + 1));
            });

            HashMap<TeamUpgrade, Integer> fastHash = GameUser.from(player).getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, HashMap::new);
            fastHash.put(TeamUpgrade.FAST_DIGGING, fastdigging + 1);
            GameUser.from(player).getCurrentTeam().getCache().set(GameConst.TEAM_UPGRADES, fastHash);
        });
    }
}
