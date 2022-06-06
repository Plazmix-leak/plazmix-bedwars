package net.plazmix.bedwars.util;

import net.plazmix.bedwars.component.Resource;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerItemTransaction {

    private final Player player;
    private final Resource resource;

    public PlayerItemTransaction(Player player, Resource resource) {
        this.player = player;
        this.resource = resource;
    }

    public Resource getResource() {
        return resource;
    }

    public int getTotalAmount() {
        int total = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && isSimilarTo(itemStack))
                total += itemStack.getAmount();
        }
        return total;
    }

    public boolean purchase(int price) {
        if (getTotalAmount() < price)
            return false;
        int needed = price;
        ItemStack[] contents = player.getInventory().getContents();
        ItemStack current;

        for (int i = 0; i < contents.length; i++) {

            current = contents[i];

            if (current != null && isSimilarTo(current)) {
                if (current.getAmount() > needed) {
                    current.setAmount(current.getAmount() - needed);
                    return true;
                } else {
                    needed -= current.getAmount();
                    player.getInventory().setItem(i, new ItemStack(Material.AIR));
                }
            }

            if (needed <= 0)
                return true;
        }

        return false;
    }

    private boolean isSimilarTo(ItemStack itemStack) {
        ItemStack resourceItem = resource.asItem();
        return itemStack.getType() == resourceItem.getType() && itemStack.getItemMeta().getDisplayName().equals(resourceItem.getItemMeta().getDisplayName());
    }
}