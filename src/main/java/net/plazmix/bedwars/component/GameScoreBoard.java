package net.plazmix.bedwars.component;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import net.plazmix.PlazmixApi;
import net.plazmix.bedwars.PlazmixBedWars;
import net.plazmix.bedwars.state.WaitingState;
import net.plazmix.bedwars.util.Functions;
import net.plazmix.bedwars.util.GameConst;
import net.plazmix.core.PlazmixCoreApi;
import net.plazmix.game.state.GameState;
import net.plazmix.game.state.type.StandardWaitingState;
import net.plazmix.game.team.GameTeam;
import net.plazmix.game.user.GameUser;
import net.plazmix.scoreboard.BaseScoreboardBuilder;
import net.plazmix.scoreboard.BaseScoreboardScope;
import net.plazmix.utility.DateUtil;
import net.plazmix.utility.NumberUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum GameScoreBoard {

    WAITING((builder, user) -> {
        GameState currentState = PlazmixBedWars.getInstance().getService().getCurrentState();
        Preconditions.checkArgument(currentState instanceof WaitingState);

        StandardWaitingState.TimerStatus timerStatus = ((WaitingState) currentState).getTimerStatus();

        builder.scoreboardLine(
                "§7" + DateUtil.formatPattern(DateUtil.DEFAULT_DATETIME_PATTERN),
                "",
                "§fКарта: §a" + PlazmixBedWars.getInstance().getService().getMapName(),
                "§fИгроки: §a0§f/§c" + PlazmixBedWars.getInstance().getService().getMaxPlayers(),
                "",
                "§cОжидание игроков...",
                "",
                "§fРежим: §a" + PlazmixBedWars.getInstance().getBedWarsMode().getFormat(),
                "§fСервер: §a" + PlazmixCoreApi.getCurrentServerName(),
                "",
                "§dwww.plazmix.net"
        )
                .scoreboardUpdater(((sccoreboard, player) -> {
                    sccoreboard.updateScoreboardLine(8, player, "§fИгроки: §a" + Bukkit.getOnlinePlayers().size() + "§f/§c" + PlazmixBedWars.getInstance().getService().getMaxPlayers());
                    sccoreboard.updateScoreboardLine(6, player, (!timerStatus.isLived() ? " §cОжидание игроков..." : "§eИгра начнётся через §a" + NumberUtil.formattingSpaced(timerStatus.getLeftSeconds(), "секунду", "секунды", "секунд")));
                }), 20L);
    }),

    INGAME((builder, user) -> builder
            .scoreboardLine(
                    "§7" + DateUtil.formatPattern(DateUtil.DEFAULT_DATETIME_PATTERN),
                    "",
                    "",
                    "§fИнформация о командах:",
                    "",
                    "",
                    "§fФинальные убийства: §c" + user.getCache().getInt(GameConst.PLAYER_FINAL_KILLS),
                    "§fКровати: §c" + user.getCache().getInt(GameConst.PLAYER_BEDS_BROKEN),
                    "",
                    "§fПоинты: §b" + user.getCache().getInt(GameConst.PLAYER_EARNED_POINTS),
                    "",
                    "§fРежим: §6" + PlazmixBedWars.getInstance().getBedWarsMode().getFormat(),
                    "§fКарта: §a" + PlazmixBedWars.getInstance().getService().getMapName(),
                    "§fСервер: §e" + PlazmixCoreApi.getCurrentServerName(),
                    "",
                    "§dwww.plazmix.net"

            ).scoreboardUpdater(((baseScoreboard, player) -> {
                    Map<Integer, String> teamStatusMap = PlazmixBedWars.getInstance().getCache().get(GameConst.INGAME_TEAM_STATUSES);
                    ArrayList<GameTeam> loadedTeams = PlazmixBedWars.getInstance().getCache().get(GameConst.GAME_LOADED_TEAMS);
                    StringBuilder firstLine = new StringBuilder();
                    StringBuilder secondLine = new StringBuilder();

                    try {
                        for (int l1 = 0; 4 > l1; l1++)
                            firstLine.append(teamStatusMap.getOrDefault(loadedTeams.get(l1).getTeamIndex(), " ")).append(" ");
                        for (int l2 = 4; 8 > l2; l2++)
                            secondLine.append(teamStatusMap.getOrDefault(loadedTeams.get(l2).getTeamIndex(), " ")).append(" ");
                    } catch (IndexOutOfBoundsException ignored) {}

                    baseScoreboard.updateScoreboardLine(15, player, "§7" + DateUtil.formatPattern(DateUtil.DEFAULT_DATETIME_PATTERN));
                    baseScoreboard.updateScoreboardLine(14, player, PlazmixBedWars.getInstance().getCache().getString(GameConst.TIME));
                    baseScoreboard.updateScoreboardLine(11, player, firstLine.toString());
                    baseScoreboard.updateScoreboardLine(10, player, secondLine.toString());
                    baseScoreboard.updateScoreboardLine(9, player, "§fФинальные убийства: §c" + user.getCache().getInt(GameConst.PLAYER_FINAL_KILLS));
                    baseScoreboard.updateScoreboardLine(8, player, "§fКровати: §c" + user.getCache().getInt(GameConst.PLAYER_BEDS_BROKEN));
                    baseScoreboard.updateScoreboardLine(6, player, "§fПоинты: §b" + user.getCache().getInt(GameConst.PLAYER_EARNED_POINTS));
    }), 20L));

    private static final Supplier<BaseScoreboardBuilder> SCOREBOARD_CREATOR = () -> PlazmixApi.newScoreboardBuilder().scoreboardScope(BaseScoreboardScope.PROTOTYPE).scoreboardDisplay(GameConst.TITLE);

    @NonNull BiConsumer<BaseScoreboardBuilder, GameUser> scoreboardInitializer;

    public void createAndChange(@NonNull GameUser user) {
        Functions.accept(SCOREBOARD_CREATOR.get(), builder -> scoreboardInitializer.accept(builder, user)).build().setScoreboardToPlayer(user.getBukkitHandle());
    }
}