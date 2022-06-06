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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public final class ArmorUpgradeProduct extends UpgradeProduct {

    public ArmorUpgradeProduct(@NonNull ItemStack item, int price, boolean soldOff) {
        super(item, price, soldOff);
    }

    @Override
    public BaseInventoryButton buildButton() {
        return new ActionInventoryButton(PlazmixApi.newItemBuilder(getIcon()).setAmount(1).build(), (player, event) -> {
            int protection = GameUser.from(player).getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, () -> new HashMap<TeamUpgrade, Integer>()).getOrDefault(TeamUpgrade.PROTECTION, 0);

            if(protection == 4) {
                player.sendMessage(GameConst.PREFIX + "§cПредмет уже полностью прокачан!");
                return;
            }

            if (!checkAndRemoveCurrency(player)) return;
            GameUser.from(player).getCurrentTeam().getPlayers().forEach(players -> {
                players.getBukkitHandle().getInventory().getBoots().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection + 1);
                players.getBukkitHandle().getInventory().getLeggings().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection + 1);
                players.getBukkitHandle().getInventory().getChestplate().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection + 1);
                players.getBukkitHandle().getInventory().getHelmet().addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection + 1);
                players.getBukkitHandle().sendMessage(GameConst.PREFIX + ChatColor.GREEN + event.getWhoClicked().getName() + " купил Защиту " + (protection + 1));
            });

            HashMap<TeamUpgrade, Integer> protHash = GameUser.from(player).getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, HashMap::new);
            protHash.put(TeamUpgrade.PROTECTION, protection + 1);
            GameUser.from(player).getCurrentTeam().getCache().set(GameConst.TEAM_UPGRADES, protHash);
        });
    }
}
