package net.plazmix.bedwars.state;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import net.plazmix.PlazmixApi;
import net.plazmix.actionitem.ActionItem;
import net.plazmix.bedwars.PlazmixBedWars;
import net.plazmix.bedwars.component.GameHotBar;
import net.plazmix.bedwars.component.GameInventory;
import net.plazmix.bedwars.component.GameScoreBoard;
import net.plazmix.bedwars.util.PlayerUtil;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.type.StandardWaitingState;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.utility.location.LocationUtil;
import net.plazmix.utility.player.PlazmixUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class WaitingState extends StandardWaitingState {

    private final ActionItem TEAM_CHANGER = ActionItem.create(PlazmixApi.newItemBuilder(Material.NOTE_BLOCK).setName("§6Выбор команды").build())
            .setInteractHandler(event -> {

                if (PlazmixCoreApi.GROUP_API.isDefault(event.getPlayer().getName())) {
                    event.getPlayer().sendMessage("§7Выбор команды доступен игрокам с группой &6&lSTAR &7и выше.");
                    return;
                }

                GameInventory.TEAM_CHANGER.createAndOpen(event.getPlayer());
            });

    @Getter PlazmixBedWars plugin;

    public WaitingState(@NonNull PlazmixBedWars plugin) {
        super(plugin, "Ожидание игроков");

        this.plugin = plugin;
    }

    @Override
    protected void onStart() {
        super.onStart();

        plugin.getService().setSetting(GameSetting.PLAYER_DROP_ITEM, false);
        plugin.getService().setSetting(GameSetting.PLAYER_PICKUP_ITEM, false);
    }

    @Override
    protected Location getTeleportLocation() {
        return LocationUtil.stringToLocation(plugin.getConfig().getString("wait-lobby-spawn"));
    }

    @Override
    protected void handleEvent(@NonNull PlayerJoinEvent event) {
        Player player = event.getPlayer();
        GameUser user = GameUser.from(player);

        GameSchedulers.runLaterAsync(20L, () -> {
            GameScoreBoard.WAITING.createAndChange(user);
            GameHotBar.WAITING.setFor(player);
            PlayerUtil.setInvulnerable(player, true);

            if(PlazmixCoreApi.GROUP_API.getGroupLevel(player.getName()) >= 3) {
                player.getInventory().setItem(4, TEAM_CHANGER.getItemStack());
            }
        });

        plugin.broadcastMessage("§eИгрок " + user.getPlazmixHandle().getDisplayName() + "§e присоединился к игре (§a" + Bukkit.getOnlinePlayers().size() + "§e/§c" + PlazmixBedWars.getInstance().getService().getMaxPlayers() + "§e)");

        if(!timerStatus.isLived() && Bukkit.getOnlinePlayers().size() == (PlazmixBedWars.getInstance().getService().getMaxPlayers() - 2)) {
            timerStatus.runTask(10);
        }
    }

    @Override
    protected void handleEvent(@NonNull PlayerQuitEvent event) {
        plugin.broadcastMessage("§eИгрок " + PlazmixUser.of(event.getPlayer()).getDisplayName() + "§e вышел из игры (§a" + Bukkit.getOnlinePlayers().size() + "§e/§c" + PlazmixBedWars.getInstance().getService().getMaxPlayers() + "§e)");

        if(GameUser.from(event.getPlayer()).getCurrentTeam() != null)
            GameUser.from(event.getPlayer()).getCurrentTeam().removePlayer(event.getPlayer());

        if(timerStatus.isLived()) {
            timerStatus.cancelTask();
            plugin.broadcastMessage("§cТаймер остановлен, недостаточно игроков для начала игры");
        }
    }

    @Override
    protected void handleTimerUpdate(@NonNull TimerStatus timerStatus) {
        long seconds = timerStatus.getLeftSeconds();

        if(seconds == 10) {
            plugin.broadcastMessage("§eТаймер запущен, до начала игры §a10 секунд!");
        } else if(seconds <= 5 && seconds > 3) {
            plugin.broadcastMessage("§eДо начала игры §a5 секунд,§e приготовьтесь!");
        } else if(seconds <= 3) {
            plugin.broadcastMessage("§eИгра начнётся через §e" + seconds);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(event.getPlayer().getLocation().getY() <= 0) {
            event.getPlayer().teleport(getTeleportLocation());
        }
    }

    @EventHandler
    public void onPlayerMessage(AsyncPlayerChatEvent event) {
        if(GameUser.from(event.getPlayer()).getCurrentTeam() == null)
            event.setFormat(PlayerUtil.formatMessageToChat(GameUser.from(event.getPlayer()).getPlazmixHandle().getDisplayName(), event.getMessage()));
        else
            event.setFormat(PlayerUtil.formatMessageToChat(GameUser.from(event.getPlayer()).getCurrentTeam().getChatColor() + event.getPlayer().getName(), event.getMessage()));
    }
}