package net.plazmix.bedwars.shop.product;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import net.plazmix.bedwars.component.ArmorType;
import net.plazmix.bedwars.component.TeamUpgrade;
import net.plazmix.bedwars.util.GameConst;
import net.plazmix.game.user.GameUser;
import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.inventory.button.impl.ActionInventoryButton;
import net.plazmix.utility.ItemUtil;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static net.plazmix.bedwars.util.ItemUtil.unbreakable;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ArmorShopProduct extends ShopProduct {

    ArmorType type;

    public ArmorShopProduct(@NonNull ArmorType type, @NonNull GameUser user) {
        super(user.getCache().getOrDefault(GameConst.PLAYER_ARMOR_TYPE, () -> ArmorType.LEATHER).ordinal() >= type.ordinal() ? BARRIER :
                ItemUtil.getNamedItemStack(new ItemStack(type.getChestplateType()), type.getTitle()), type.getCurrency(), type.getPrice());

        this.type = type;
    }

    @Override
    public BaseInventoryButton buildButton() {
        return new ActionInventoryButton(icon, (player, event) -> {
            GameUser user = GameUser.from(player);

            if(user.getCache().getOrDefault(GameConst.PLAYER_ARMOR_TYPE, () -> ArmorType.LEATHER).equals(type)) return;

            if (!checkAndRemoveCurrency(player)) return;

            user.getCache().set(GameConst.PLAYER_ARMOR_TYPE, type);

            int protection = user.getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, () -> new HashMap<TeamUpgrade, Integer>())
                    .getOrDefault(TeamUpgrade.PROTECTION, 0);

            ItemStack chestplate = unbreakable(type.getChestplateType());
            ItemStack boots = unbreakable(type.getBootsType());

            if (protection > 0) {
                chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
                boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, protection);
            }

            player.getInventory().setChestplate(chestplate);
            player.getInventory().setBoots(boots);
        });
    }
}