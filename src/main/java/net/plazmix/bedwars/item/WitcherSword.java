package net.plazmix.bedwars.item;

import lombok.NonNull;
import net.plazmix.PlazmixApi;
import net.plazmix.actionitem.AbstractActionItem;
import net.plazmix.actionitem.ActionItem;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

public final class WitcherSword extends AbstractActionItem {

    public static final WitcherSword instance = new WitcherSword();

    private WitcherSword() {
        super(PlazmixApi.newItemBuilder(Material.IRON_SWORD).setName("§dМеч ведьмака").setUnbreakable(true).build());
    }

    @Override
    public void handle(@NonNull ActionItem item) {
        item.setAttackHandler(event -> {
            if(event.getEntityType() != EntityType.PLAYER) return;

            if(ThreadLocalRandom.current().nextDouble() <= 0.3) {
                ((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.POISON, 3, 1, false, true), true);
            }
        });
    }
}