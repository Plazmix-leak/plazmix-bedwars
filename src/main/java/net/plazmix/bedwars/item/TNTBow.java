package net.plazmix.bedwars.item;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import net.plazmix.PlazmixApi;
import net.plazmix.actionitem.AbstractActionItem;
import net.plazmix.actionitem.ActionItem;
import net.plazmix.bedwars.PlazmixBedWars;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;

@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public final class TNTBow extends AbstractActionItem implements Listener {

    public static final TNTBow instance = new TNTBow();

    private TNTBow() {
        super(PlazmixApi.newItemBuilder(Material.BOW).addEnchantment(Enchantment.DURABILITY, 3).setName("§cДинамитный лук").build());

        Bukkit.getPluginManager().registerEvents(this, PlazmixBedWars.getPlugin(PlazmixBedWars.class));
    }

    @Override
    public void handle(@NonNull ActionItem item) {
        item.setShootBowHandler(event -> {});
        item.setProjectileHitHandler(event -> {
            event.getEntity().getLocation().getWorld().spawnEntity(event.getEntity().getLocation(), EntityType.PRIMED_TNT);
            event.getEntity().remove();
        });
    }
}