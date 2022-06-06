package net.plazmix.bedwars.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.plazmix.game.event.GameGhostChangeEvent;
import net.plazmix.game.user.GameUser;
import net.plazmix.utility.BukkitPotionUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Iterator;

@UtilityClass
public class PlayerUtil {
    private final HashMap<Player, Boolean> ghosts = new HashMap<>();

    public void setInvulnerable(@NonNull Player player, boolean flag) {
        if (flag) {
            player.setNoDamageTicks(999);
        } else {
            player.setNoDamageTicks(0);
        }
    }

    public void hideForAll(@NonNull Player player) {
        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> onlinePlayer.hidePlayer(player));
    }

    public void clearPotionEffects(@NonNull Player player) {
        player.getActivePotionEffects().stream().map(PotionEffect::getType).forEach(player::removePotionEffect);
    }


    public String formatMessageToChat(String sender, String message) {
        String formattedMessage;
        return formattedMessage = sender + ChatColor.RESET + " §8§l↪ " + ChatColor.RESET + message;
    }
}