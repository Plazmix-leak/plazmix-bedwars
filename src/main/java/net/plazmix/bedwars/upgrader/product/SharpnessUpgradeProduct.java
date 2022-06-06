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
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public final class SharpnessUpgradeProduct extends UpgradeProduct {

    public SharpnessUpgradeProduct(@NonNull ItemStack item, int price, boolean soldOff) {
        super(item, price, soldOff);
    }

    @Override
    public BaseInventoryButton buildButton() {
        return new ActionInventoryButton(PlazmixApi.newItemBuilder(getIcon()).setAmount(1).build(), (player, event) -> {
            int sharpness = GameUser.from(player).getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, () -> new HashMap<TeamUpgrade, Integer>()).getOrDefault(TeamUpgrade.SHARPNESS, 0);

            if(sharpness == 1) {
                player.sendMessage(GameConst.PREFIX + "§cПредмет уже полностью прокачан!");
                return;
            }

            if (!checkAndRemoveCurrency(player)) return;
            GameUser.from(player).getCurrentTeam().getPlayers().forEach(players -> {
                ItemStack[] itemStacks = players.getBukkitHandle().getInventory().getContents();
                if(players.getBukkitHandle().getInventory().contains(Material.WOOD_SWORD) || players.getBukkitHandle().getInventory().contains(Material.STONE_SWORD) || players.getBukkitHandle().getInventory().contains(Material.IRON_SWORD) || players.getBukkitHandle().getInventory().contains(Material.DIAMOND_SWORD)) {
                    for (ItemStack itemStack : itemStacks) {
                        if (itemStack.getType().name().endsWith("SWORD")) {
                            players.getBukkitHandle().getInventory().getItem(players.getBukkitHandle().getInventory().first(itemStack)).addEnchantment(Enchantment.DAMAGE_ALL, 1);
                            players.getBukkitHandle().sendMessage(GameConst.PREFIX + ChatColor.GREEN + event.getWhoClicked().getName() + " купил Остроту " + (sharpness + 1));
                            break;
                        }
                    }
                }
            });
            HashMap<TeamUpgrade, Integer> sharpHash = GameUser.from(player).getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, HashMap::new);
            sharpHash.put(TeamUpgrade.SHARPNESS, 1);
            GameUser.from(player).getCurrentTeam().getCache().set(GameConst.TEAM_UPGRADES, sharpHash);
        });
    }
}
