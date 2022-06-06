package net.plazmix.bedwars.shop.product;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import net.plazmix.bedwars.component.ArmorType;
import net.plazmix.bedwars.component.GameTool;
import net.plazmix.bedwars.component.Resource;
import net.plazmix.bedwars.item.*;
import net.plazmix.bedwars.util.ColorUtil;
import net.plazmix.bedwars.util.GameConst;
import net.plazmix.bedwars.util.PlayerItemTransaction;
import net.plazmix.game.user.GameUser;
import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.utility.NumberUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.function.Function;

import static net.plazmix.PlazmixApi.newItemBuilder;
import static net.plazmix.bedwars.util.ItemUtil.unbreakable;

@Data
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class ShopProduct {

    protected static final ItemStack BARRIER = newItemBuilder(Material.BARRIER).setName("§cКуплено").build();

    public static final ImmutableMap<Integer, Function<GameUser, ShopProduct>> PRODUCT_MAP = ImmutableMap.<Integer, Function<GameUser, ShopProduct>> builder()
            .put(1, user -> new DefaultShopProduct(newItemBuilder(Material.WOOL).setDyeColor(ColorUtil.asDyeColor(user.getCurrentTeam().getChatColor())).setAmount(16).build(), Resource.BRONZE, 4))
            .put(2, user -> new DefaultShopProduct(new ItemStack(Material.WOOD, 12), Resource.RUBY, 3))
            .put(3, user -> new DefaultShopProduct(new ItemStack(Material.ENDER_STONE, 12), Resource.BRONZE, 24))
            .put(4, user -> new DefaultShopProduct(new ItemStack(Material.LADDER, 8), Resource.BRONZE, 4))
            .put(5, user -> new DefaultShopProduct(newItemBuilder(Material.GLASS).setAmount(8).setDyeColor(ColorUtil.asDyeColor(user.getCurrentTeam().getChatColor())).build(), Resource.BRONZE, 24))
            .put(6, user -> new DefaultShopProduct(new ItemStack(Material.OBSIDIAN, 4), Resource.OPAL, 4))
            .put(7, user -> new WeaponShopProduct(unbreakable(Material.STONE_SWORD), Resource.BRONZE, 10))
            .put(8, user -> new WeaponShopProduct(unbreakable(Material.IRON_SWORD), Resource.RUBY, 7))
            .put(9, user -> new WeaponShopProduct(unbreakable(Material.DIAMOND_SWORD), Resource.OPAL, 3))
            .put(10, user -> new DefaultShopProduct(newItemBuilder(Material.BLAZE_ROD).addEnchantment(Enchantment.KNOCKBACK, 1).build(), Resource.RUBY, 4))
            .put(11, user -> new DefaultShopProduct(VampireSword.instance.getActionItem().getItemStack(), Resource.OPAL, 6))
            .put(12, user -> new DefaultShopProduct(WitcherSword.instance.getActionItem().getItemStack(), Resource.OPAL, 8))
            .put(13, user -> new DefaultShopProduct(newItemBuilder(Material.GOLD_SWORD).setUnbreakable(true).setName("§6§lМеч Таноса").addEnchantment(Enchantment.FIRE_ASPECT, 2).addEnchantment(Enchantment.KNOCKBACK, 1).build(), Resource.OPAL, 32))
            .put(14, user -> new DefaultShopProduct(new ItemStack(Material.BOW), Resource.RUBY, 8))
            .put(15, user -> new DefaultShopProduct(newItemBuilder(Material.BOW).addEnchantment(Enchantment.DURABILITY, 3).addEnchantment(Enchantment.ARROW_KNOCKBACK, 2).build(), Resource.OPAL, 5))
            .put(16, user -> new DefaultShopProduct(TNTBow.instance.getActionItem().getItemStack(), Resource.OPAL, 3))
            .put(17, user -> new DefaultShopProduct(new ItemStack(Material.FISHING_ROD), Resource.OPAL, 5))
            .put(18, user -> new ArmorShopProduct(ArmorType.GOLD, user))
            .put(19, user -> new ArmorShopProduct(ArmorType.IRON, user))
            .put(20, user -> new ArmorShopProduct(ArmorType.DIAMOND, user))
            .put(21, user -> new ToolShopProduct(GameTool.Shears.DEFAULT))
            .put(22, user -> new ToolShopProduct(user.getCache().getOrDefault(GameConst.PLAYER_TOOL_LEVELS, () -> new HashMap<GameTool.Type, GameTool.IGameTool>()).getOrDefault(GameTool.Type.PICKAXE, GameTool.Pickaxe.WOOD)))
            .put(23, user -> new ToolShopProduct(user.getCache().getOrDefault(GameConst.PLAYER_TOOL_LEVELS, () -> new HashMap<GameTool.Type, GameTool.IGameTool>()).getOrDefault(GameTool.Type.AXE, GameTool.Axe.WOOD)))

            .put(24, user -> new DefaultShopProduct(new PotionItem("§bЗелье скорости",
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWRjOWZmMTg0YWU3NjdkM2NiZmQ5YzNhYTJjN2U4OGIxMGY5YjU5MTI5N2ZmNjc2ZGE2MzlmYjQ0NDYyMzhjOCJ9fX0=",
                    new PotionEffect(PotionEffectType.SPEED, (30 * 20) + 40, 5)).getActionItem().getItemStack(), Resource.OPAL, 1))

            .put(25, user -> new DefaultShopProduct(new PotionItem("§aЗелье прыгучести",
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjNkMzU2Yjg2MTE4ZmY4YzQ4YzExOWVmM2QyYWM5MGMxYWQxMzkyODkzMDIyZjgwYjlkYzE2OGMzNmRiMmE1NiJ9fX0=",
                    new PotionEffect(PotionEffectType.JUMP, (40 * 20) + 40, 5)).getActionItem().getItemStack(), Resource.OPAL, 1))

            .put(26, user -> new DefaultShopProduct(new PotionItem("§cЗелье лечения",
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM2MjllODZhZmRiNTgyZGI4MjM1MzI3NDQ5NTczMTNmYzI2YjYyMTk0ODI5YzhkM2Y4MTRjODAyODk2YjIifX19",
                    new PotionEffect(PotionEffectType.HEAL, (4 * 20) + 40, 2)).getActionItem().getItemStack(), Resource.OPAL, 1))

            .put(27, user -> new DefaultShopProduct(new ItemStack(Material.GOLDEN_APPLE), Resource.RUBY, 3))
            .put(28, user -> new DefaultShopProduct(FireBall.instance.getActionItem().getItemStack(), Resource.BRONZE, 35))
            .put(29, user -> new DefaultShopProduct(new ItemStack(Material.TNT), Resource.RUBY, 4))
            .put(30, user -> new DefaultShopProduct(new ItemStack(Material.ENDER_PEARL), Resource.OPAL, 3))
            .put(31, user -> new DefaultShopProduct(new ItemStack(Material.SLIME_BLOCK), Resource.RUBY, 1))
            .put(32, user -> new DefaultShopProduct(AutoBridge.instance.getActionItem().getItemStack(), Resource.OPAL, 2))
            .put(33, user -> new DefaultShopProduct(new ItemStack(Material.ARROW, 8), Resource.RUBY, 4))
            .build();

    ItemStack item;
    Resource currency;
    int price;
    ItemStack icon;

    public ShopProduct(@NonNull ItemStack item, @NonNull Resource currency, int price) {
        this.item = item;
        this.currency = currency;
        this.price = price;
        String whatDoesThisThingCanDo = "§a";
        String currencyName;
        String currencyColor = currency.getName().substring(0, 4);

        if(item.isSimilar(VampireSword.instance.getActionItem().getItemStack()))
            whatDoesThisThingCanDo = whatDoesThisThingCanDo + "Шанс 20% при ударе получить 1 хп";

        else if(item.isSimilar(WitcherSword.instance.getActionItem().getItemStack()))
            whatDoesThisThingCanDo = whatDoesThisThingCanDo + "Шанс 30% при ударе дать противнику отравление 1";

        else if(item.isSimilar(TNTBow.instance.getActionItem().getItemStack()))
            whatDoesThisThingCanDo = whatDoesThisThingCanDo + "Спавнит ТНТ там где прилетела стрела";


        if(currency.getName().equals("§6§lБронза"))
            currencyName = NumberUtil.formattingSpaced(price, currency.getName(), currency.getName().substring(0, currency.getName().length() - 1) + "ы", currency.getName().substring(0, currency.getName().length() - 1) + "ы");
        else
            currencyName = NumberUtil.formattingSpaced(price, currency.getName(), currency.getName() + "а", currency.getName() + "ов");

        this.icon = newItemBuilder(item.clone()).addLore(whatDoesThisThingCanDo).addLore("  §e§lЦена: " + currencyColor + currencyName).build();
    }

    public abstract BaseInventoryButton buildButton();

    protected boolean checkAndRemoveCurrency(@NonNull Player player) {
        PlayerInventory inventory = player.getInventory();

        if (inventory.firstEmpty() == -1) {
            player.sendMessage(GameConst.PREFIX + "§cОсвободите место в инвентаре, чтобы приобрести что-нибудь!");
            player.closeInventory();
            return false;
        }

        PlayerItemTransaction transaction = new PlayerItemTransaction(player, currency);
        boolean result = transaction.purchase(price);

        if (!result) {
            player.sendMessage(GameConst.PREFIX + "§cНедостаточно ресурсов для покупки данного товара");
        }

        return result;
    }
}