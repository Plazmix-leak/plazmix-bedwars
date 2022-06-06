package net.plazmix.bedwars.item;

import lombok.NonNull;
import net.plazmix.PlazmixApi;
import net.plazmix.actionitem.AbstractActionItem;
import net.plazmix.actionitem.ActionItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public final class VampireSword extends AbstractActionItem {

    public static final VampireSword instance = new VampireSword();

    private VampireSword() {
        super(PlazmixApi.newItemBuilder(Material.DIAMOND_SWORD).setName("§cМеч вампира").setUnbreakable(true).build());
    }

    @Override
    public void handle(@NonNull ActionItem item) {
        item.setAttackHandler(event -> {
            if(!(event.getDamager() instanceof Player)) return;

           if(ThreadLocalRandom.current().nextDouble() <= 0.2) {
              Player player = (Player) event.getDamager();

              player.setHealth(player.getHealth() + Math.min(player.getMaxHealth() - player.getHealth(), 2.0));
           }
        });
    }
}