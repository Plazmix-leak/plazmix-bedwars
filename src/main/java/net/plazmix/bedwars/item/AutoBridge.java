package net.plazmix.bedwars.item;

import lombok.NonNull;
import net.plazmix.PlazmixApi;
import net.plazmix.actionitem.AbstractActionItem;
import net.plazmix.actionitem.ActionItem;
import net.plazmix.bedwars.PlazmixBedWars;
import net.plazmix.bedwars.util.ColorUtil;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.GameSchedulers;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitTask;

public final class AutoBridge extends AbstractActionItem implements Listener {

    public static final AutoBridge instance = new AutoBridge();

    private AutoBridge() {
        super(PlazmixApi.newItemBuilder(Material.EGG).setName(ChatColor.YELLOW + "Автоматический мост").build());

        Bukkit.getPluginManager().registerEvents(this, PlazmixBedWars.getPlugin(PlazmixBedWars.class));
    }

    @Override
    public void handle(@NonNull ActionItem actionItem) {
        actionItem.setProjectileHitHandler(event -> {
            if (event.getEntity().getType() != EntityType.EGG)
                return;

            Player player = (Player) event.getEntity().getShooter();
            Entity egg = event.getEntity();

            BukkitTask task = GameSchedulers.runTimer(0, 20, () -> {
                while (egg.getLocation().distance(player.getLocation()) < 36) {
                    if (egg.isOnGround())
                        break;

                    Block block = egg.getLocation().getBlock();
                    block.setType(Material.WOOL);
                    ((Wool) block).setColor(ColorUtil.asDyeColor(GameUser.from(player).getCurrentTeam().getChatColor()));
                }
            });
            GameSchedulers.runLater(100, task::cancel);
        });
    }
}