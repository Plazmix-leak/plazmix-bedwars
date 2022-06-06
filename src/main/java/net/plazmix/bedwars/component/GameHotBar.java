package net.plazmix.bedwars.component;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import net.plazmix.PlazmixApi;
import net.plazmix.bedwars.util.Functions;
import net.plazmix.bedwars.util.GameConst;
import net.plazmix.bedwars.util.ItemUtil;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.hotbar.GameHotbar;
import net.plazmix.game.utility.hotbar.GameHotbarBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum GameHotBar {

    WAITING(builder -> {
        builder.setMoveItems(false)
                .addItem(9, PlazmixApi.newItemBuilder(Material.BARRIER).setName("§cВыйти в лобби").build(), PlazmixCoreApi::redirectToLobby);
    }),

    INGAME(builder -> {
        builder.setAllowInteraction(true).setMoveItems(true)
                .addItem(1, ItemUtil.unbreakable(Material.WOOD_SWORD));
    });

    GameHotbar hotBar;

    GameHotBar(Consumer<GameHotbarBuilder> hotBarInitializer) {
        hotBar = Functions.accept(GameHotbarBuilder.newBuilder(), hotBarInitializer).build();
    }

    public void setFor(@NonNull Player player) {
        hotBar.setHotbarTo(player);
    }
}