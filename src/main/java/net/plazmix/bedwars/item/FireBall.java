package net.plazmix.bedwars.item;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import net.plazmix.PlazmixApi;
import net.plazmix.actionitem.AbstractActionItem;
import net.plazmix.actionitem.ActionItem;
import net.plazmix.bedwars.PlazmixBedWars;
import net.plazmix.bedwars.util.Functions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class FireBall extends AbstractActionItem implements Listener {

    public static final FireBall instance = new FireBall();

    private FireBall() {
        super(PlazmixApi.newItemBuilder(Material.FIREBALL).setName(ChatColor.GOLD + "Огненный шар").build());

        Bukkit.getPluginManager().registerEvents(this, PlazmixBedWars.getPlugin(PlazmixBedWars.class));
    }

    @Override
    public void handle(@NonNull ActionItem item) {
        item.setInteractHandler(event -> {
            event.setCancelled(true);

            Player player = event.getPlayer();

            Location loc = player.getLocation();

            Entity f = player.getWorld().spawnEntity(loc.add(loc.getDirection()), EntityType.FIREBALL);

            f.setVelocity(loc.getDirection().multiply(0.7));

            if(player.getItemInHand().getAmount() > 1) {
                player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
            } else {
                player.setItemInHand(new ItemStack(Material.AIR));
            }
        });
    }
}