package net.plazmix.bedwars.upgrader;

import lombok.NonNull;
import net.plazmix.bedwars.component.TeamUpgrade;
import net.plazmix.bedwars.upgrader.product.ArmorUpgradeProduct;
import net.plazmix.bedwars.upgrader.product.FastDiggerUpgradeProduct;
import net.plazmix.bedwars.upgrader.product.SharpnessUpgradeProduct;
import net.plazmix.bedwars.upgrader.product.TrapUpgradeProduct;
import net.plazmix.bedwars.util.GameConst;
import net.plazmix.game.user.GameUser;
import net.plazmix.inventory.button.BaseInventoryButton;
import net.plazmix.inventory.impl.BaseSimpleInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Function;

import static net.plazmix.PlazmixApi.newItemBuilder;

public final class UpgradeInventory extends BaseSimpleInventory {
    Map<Integer, Function<GameUser, BaseInventoryButton>> buttonFunctions = new LinkedHashMap<>();

    public UpgradeInventory() {
        super("§6§lУлучшение", 5);
    }

    public UpgradeInventory addProduct(int slot, @NonNull Function<GameUser, UpgradeProduct> buttonFunction) {
        buttonFunctions.put(slot, user -> buttonFunction.apply(user).buildButton());

        return this;
    }

    public UpgradeInventory addProduct(int slot, @NonNull UpgradeProduct product) {
        return addProduct(slot, user -> product);
    }

    @Override
    public void drawInventory(Player player) {
        List<Integer> protPrices = Arrays.asList(3, 6, 12, 16);
        List<Integer> fastDiggingPrices = Arrays.asList(3, 6);

        boolean isTrapSold = false;
        boolean isSharpSold = false;
        boolean isProtSold = false;
        boolean isDigSold = false;

        int trap = GameUser.from(player).getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, () -> new HashMap<TeamUpgrade, Integer>()).getOrDefault(TeamUpgrade.TRAP, 0);
        int prot = GameUser.from(player).getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, () -> new HashMap<TeamUpgrade, Integer>()).getOrDefault(TeamUpgrade.PROTECTION, 0);
        int sharp = GameUser.from(player).getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, () -> new HashMap<TeamUpgrade, Integer>()).getOrDefault(TeamUpgrade.SHARPNESS, 0);
        int fastdigging = GameUser.from(player).getCurrentTeam().getCache().getOrDefault(GameConst.TEAM_UPGRADES, () -> new HashMap<TeamUpgrade, Integer>()).getOrDefault(TeamUpgrade.FAST_DIGGING, 0);

        if(trap == 1)
            isTrapSold = true;
        if(sharp == 1)
            isSharpSold = true;
        if(fastdigging == 2)
            isDigSold = true;
        if(prot == 4)
            isProtSold = true;

        ItemStack glassPane = newItemBuilder(Material.STAINED_GLASS_PANE).setDurability(7).build();
        for(int i = 1; i <= 9; i++)
            setOriginalItem(i, glassPane);
        for(int i = 36; i <= 44; i++)
            setOriginalItem(i, glassPane);

        addProduct(20, new SharpnessUpgradeProduct(newItemBuilder(Material.DIAMOND_SWORD).setName("§cОстрота " + (sharp + 1)).build(), 5, isSharpSold));
        addProduct(21, new ArmorUpgradeProduct(newItemBuilder(Material.IRON_CHESTPLATE).setName("§2Броня " + (prot + 1)).build(), protPrices.get(prot), isProtSold));
        addProduct(22, new FastDiggerUpgradeProduct(newItemBuilder(Material.GOLD_PICKAXE).setName("§bСкорость копания " + (fastdigging + 1)).build(), fastDiggingPrices.get(fastdigging), isDigSold));
        addProduct(24, new TrapUpgradeProduct(newItemBuilder(Material.TRIPWIRE_HOOK).setName("§cЛовушка " + (trap + 1)).build(), 2, isTrapSold));
        update(player);
    }

    private void update(Player player) {
        buttonFunctions.forEach((key, value) -> setItem(key + 1, value.apply(GameUser.from(player))));
    }
}
