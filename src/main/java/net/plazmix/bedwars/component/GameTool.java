package net.plazmix.bedwars.component;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import net.plazmix.bedwars.util.ItemUtil;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import static net.plazmix.bedwars.util.ItemUtil.unbreakableEnchanted;

@UtilityClass
public class GameTool {

    public interface IGameTool {

        @NonNull Resource getCurrency();

        int getPrice();

        @NonNull ItemStack getItem();

        boolean isDisappears();

        IGameTool previous();

        IGameTool next();

        boolean isLast();

        @NonNull Type getType();
    }

    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public enum Shears implements IGameTool {

        DEFAULT(ItemUtil.unbreakable(Material.SHEARS), false, Resource.BRONZE, 20);

        @NonNull ItemStack item;
        boolean disappears;
        @NonNull Resource currency;
        int price;

        @Override
        public IGameTool previous() {
            throw new UnsupportedOperationException();
        }

        @Override
        public IGameTool next() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isLast() {
            return false;
        }

        @Override
        public @NonNull Type getType() {
            return Type.SHEARS;
        }
    }

    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public enum Pickaxe implements IGameTool {

        WOOD(unbreakableEnchanted(Material.WOOD_PICKAXE, Enchantment.DIG_SPEED, 1), false, Resource.BRONZE, 10),
        IRON(unbreakableEnchanted(Material.IRON_PICKAXE, Enchantment.DIG_SPEED, 2), true, Resource.BRONZE, 10),
        GOLD(unbreakableEnchanted(Material.GOLD_PICKAXE, Enchantment.DIG_SPEED, 2), true, Resource.RUBY, 3),
        DIAMOND(unbreakableEnchanted(Material.DIAMOND_PICKAXE, Enchantment.DIG_SPEED, 3), true, Resource.RUBY, 6);

        @NonNull ItemStack item;
        boolean disappears;
        @NonNull Resource currency;
        int price;

        @Override
        public Pickaxe previous() {
            Preconditions.checkArgument(ordinal() > 0);

            return values()[ordinal() - 1];
        }

        @Override
        public Pickaxe next() {
            Preconditions.checkArgument(!isLast());

            return values()[ordinal() + 1];
        }

        @Override
        public boolean isLast() {
            return this == DIAMOND;
        }

        @Override
        public @NonNull Type getType() {
            return Type.PICKAXE;
        }
    }
    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public enum Axe implements IGameTool {

        WOOD(unbreakableEnchanted(Material.WOOD_AXE, Enchantment.DIG_SPEED, 1), false, Resource.BRONZE, 10),
        STONE(unbreakableEnchanted(Material.STONE_AXE, Enchantment.DIG_SPEED, 1), true, Resource.BRONZE, 10),
        IRON(unbreakableEnchanted(Material.IRON_AXE, Enchantment.DIG_SPEED, 2), true, Resource.RUBY, 3),
        DIAMOND(unbreakableEnchanted(Material.DIAMOND_AXE, Enchantment.DIG_SPEED, 3), true, Resource.RUBY, 6);

        @NonNull ItemStack item;
        boolean disappears;
        @NonNull Resource currency;
        int price;

        @Override
        public Axe previous() {
            Preconditions.checkArgument(ordinal() > 0);

            return values()[ordinal() - 1];
        }

        @Override
        public Axe next() {
            Preconditions.checkArgument(!isLast());

            return values()[ordinal() + 1];
        }

        @Override
        public boolean isLast() {
            return this == DIAMOND;
        }

        @Override
        public @NonNull Type getType() {
            return Type.AXE;
        }
    }

    public enum Type {

        SHEARS,
        PICKAXE,
        AXE
    }
}