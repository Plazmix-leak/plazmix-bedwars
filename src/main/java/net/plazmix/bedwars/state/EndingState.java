package net.plazmix.bedwars.state;

import lombok.NonNull;
import net.plazmix.bedwars.component.BedwarsGameStatsMysqlDatabase;
import net.plazmix.bedwars.util.GameConst;
import net.plazmix.bedwars.util.PlayerUtil;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.setting.GameSetting;
import net.plazmix.game.state.type.StandardEndingState;
import net.plazmix.game.team.GameTeam;
import net.plazmix.game.user.GameUser;
import net.plazmix.game.utility.GameSchedulers;
import net.plazmix.game.utility.hotbar.GameHotbar;
import net.plazmix.game.utility.hotbar.GameHotbarBuilder;
import net.plazmix.game.utility.worldreset.GameWorldReset;
import net.plazmix.utility.ItemUtil;
import net.plazmix.utility.NumberUtil;
import org.bukkit.*;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.FireworkMeta;

public class EndingState extends StandardEndingState {
    private final GameHotbar gameHotbar = GameHotbarBuilder.newBuilder()
            .setMoveItems(true)

            .addItem(5, ItemUtil.newBuilder(Material.PAPER)
                            .setName("§aСыграть еще раз")
                            .build(),

                    player -> GamePlugin.getInstance().getService().playAgain(player))

            .addItem(9, ItemUtil.newBuilder(Material.MAGMA_CREAM)
                            .setName("§aПокинуть арену")
                            .build(),

                    PlazmixCoreApi::redirectToLobby)

            .build();

    private GameTeam winnerTeam;

    public EndingState(@NonNull GamePlugin plugin) {
        super(plugin, "Перезагрузка");
    }

    @Override
    protected String getWinnerPlayerName() {
        return null;
    }

    @Override
    protected void handleStart() {
        GameSetting.setAll(plugin.getService(), false);

        boolean isTie = getPlugin().getCache().get(GameConst.IS_TIE);

        GameWorldReset.resetAllWorlds();

        getPlugin().getService().getLoadedTeams().forEach(gameTeam -> {
            if(gameTeam.getCache().get(GameConst.IS_TEAM_ALIVE))
                plugin.getCache().set(GameConst.WIN_TEAM, gameTeam);
        });

        GameTeam winnerTeam = plugin.getCache().get(GameConst.WIN_TEAM);


        if (winnerTeam == null && !isTie) {
            plugin.broadcastMessage(ChatColor.RED + "Произошли техничекие неполадки, из-за чего игра была принудительно остановлена!");

            forceShutdown();
            return;
        }

        if(!isTie) {
            // Run fireworks spam.
            winnerTeam.getPlayers().forEach(gameUser -> {
                GameSchedulers.runTimer(0, 20, () -> {

                    if (gameUser.getBukkitHandle() == null) {
                        return;
                    }

                    Firework firework = gameUser.getBukkitHandle().getWorld().spawn(gameUser.getBukkitHandle().getLocation(), Firework.class);
                    FireworkMeta fireworkMeta = firework.getFireworkMeta();

                    fireworkMeta.setPower(1);
                    fireworkMeta.addEffect(FireworkEffect.builder()
                            .with(FireworkEffect.Type.STAR)
                            .withColor(Color.RED)
                            .withColor(Color.GREEN)
                            .withColor(Color.WHITE)
                            .build());

                    firework.setFireworkMeta(fireworkMeta);
                    gameUser.getCache().increment(GameConst.WINS_PLAYER_DATA);
                    gameUser.getCache().increment(GameConst.GAMES_PLAYED_PLAYER_DATA);
                });
            });
        }


        for (Player player : Bukkit.getOnlinePlayers()) {
            GameUser gameUser = GameUser.from(player);

            // Announcements.
            player.playSound(player.getLocation(), Sound.ENDERDRAGON_DEATH, 2, 0);

            if(!isTie) {
                if (winnerTeam.hasPlayer(player.getName())) {
                    player.sendTitle("§6§lПОБЕДА", "§fВы победили в этой игре!");

                    player.sendMessage("§e+250 монет (победа)");
                    gameUser.getPlazmixHandle().addCoins(250);
                    player.sendMessage("§3+50 опыта (победа)");
                    gameUser.getPlazmixHandle().addExperience(50);
                }

            } else if(isTie) {
                player.sendTitle("§e§lНИЧЬЯ", "§fЭта игра ничейная!");

                player.sendMessage("§e+150 монет (ничья)");
                gameUser.getPlazmixHandle().addCoins(150);
                player.sendMessage("§3+25 опыта (ничья)");
                gameUser.getPlazmixHandle().addExperience(25);

            } else if(!isTie && !winnerTeam.hasPlayer(player.getName())) {
                player.sendTitle("§c§lПОРАЖЕНИЕ", "§fВ этой игре победила команда " + winnerTeam.getTeamName());
                player.sendMessage("§e+40 монет (поражение)");
                gameUser.getPlazmixHandle().addCoins(40);
                player.sendMessage("§3+10 опыта (поражение)");
                gameUser.getPlazmixHandle().addExperience(10);
            }

            int totalKills = GameUser.from(player).getCache().getInt(GameConst.PLAYER_KILLS) + GameUser.from(player).getCache().getInt(GameConst.PLAYER_FINAL_KILLS);
            int brookedBeds = GameUser.from(player).getCache().getInt(GameConst.PLAYER_BEDS_BROKEN);

            if(totalKills > 0) {
                player.sendMessage("§e+" + (10 * totalKills) + " монет (" + NumberUtil.formattingSpaced(totalKills, "убийство", "убийства", "убийств") + ")");
                gameUser.getPlazmixHandle().addCoins(100);
                player.sendMessage("§3+" + (totalKills*5) + " опыта (" + NumberUtil.formattingSpaced(totalKills, "убийство", "убийства", "убийств") + ")");
                gameUser.getPlazmixHandle().addExperience(5*totalKills);
            }

            if(brookedBeds > 0) {
                player.sendMessage("§3+" + (20 * brookedBeds) + " опыта (" + NumberUtil.formattingSpaced(brookedBeds, "кровать", "кровати", "кроватей") + ")");
                gameUser.getPlazmixHandle().addExperience(20*brookedBeds);
            }

            // Set hotbar items.
            gameHotbar.setHotbarTo(player);

            // Update player data in database.
            GameMysqlDatabase statsMysqlDatabase = plugin.getService().getGameDatabase(BedwarsGameStatsMysqlDatabase.class);
            statsMysqlDatabase.insert(false, GameUser.from(player));
        }
    }

    @Override
    protected void handleScoreboardSet(@NonNull Player player) {

    }

    @Override
    protected Location getTeleportLocation() {
        return null;
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setFormat(PlayerUtil.formatMessageToChat(GameUser.from(event.getPlayer()).getPlazmixHandle().getDisplayName(), event.getMessage()));
    }
}
