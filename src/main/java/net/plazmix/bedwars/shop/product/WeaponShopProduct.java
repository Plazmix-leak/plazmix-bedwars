package net.plazmix.bedwars.shop.product;

import lombok.NonNull;
import net.plazmix.bedwars.component.Resource;
import net.plazmix.bedwars.component.TeamUpgrade;
import net.plazmix.bedwars.util.GameConst;
import net.plazmix.bedwars.util.ItemUtil;
import net.plazmix.game.user.GameUser;
import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.inventory.button.impl.ActionInventoryButton;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class WeaponShopProduct  extends ShopProduct {
    public WeaponShopProduct(@NonNull ItemStack item, @NonNull Resource currency, int price) {
        super(item, currency, price);
    }

    @Override
    public BaseInventoryButton buildButton() {
        return new ActionInventoryButton(icon, (player, event) -> {
            if (!checkAndRemoveCurrency(player)) return;

            int sharpness = GameUser.from(player).getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, () -> new HashMap<TeamUpgrade, Integer>()).getOrDefault(TeamUpgrade.SHARPNESS, 0);
            ItemStack[] itemStacks = player.getInventory().getContents();
            if(player.getInventory().contains(Material.WOOD_SWORD) || player.getInventory().contains(Material.STONE_SWORD) || player.getInventory().contains(Material.IRON_SWORD) || player.getInventory().contains(Material.DIAMOND_SWORD)) {
                for (ItemStack itemStack : itemStacks)
                    if (itemStack.getType().name().endsWith("SWORD")) {
                        ItemStack itemToGive = new ItemStack(ItemUtil.unbreakable(icon.getType()));
                        if (sharpness > 0)
                            itemToGive.addEnchantment(Enchantment.DAMAGE_ALL, sharpness);
                        if(itemToGive.isSimilar(itemStack))
                            player.getInventory().addItem(itemToGive);
                        else
                            player.getInventory().setItem(player.getInventory().first(itemStack.getType()), itemToGive);
                    }
            }

        });
    }
}
