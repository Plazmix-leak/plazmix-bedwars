package net.plazmix.bedwars.component;

import lombok.NonNull;
import net.plazmix.bedwars.PlazmixBedWars;
import net.plazmix.bedwars.util.GameConst;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.mysql.RemoteDatabaseRowType;
import net.plazmix.game.user.GameUser;

public final class BedwarsGameStatsMysqlDatabase extends GameMysqlDatabase {

    public BedwarsGameStatsMysqlDatabase() {
        super(PlazmixBedWars.getInstance().getBedWarsMode().getSqlTable(), true);
    }

    @Override
    public void initialize() {
        addColumn(GameConst.WINS_PLAYER_DATA, RemoteDatabaseRowType.INT, gameUser -> gameUser.getCache().getInt(GameConst.WINS_PLAYER_DATA));
        addColumn(GameConst.BEDS_BROKEN_PLAYER_DATA, RemoteDatabaseRowType.INT, gameUser -> gameUser.getCache().getInt(GameConst.BEDS_BROKEN_PLAYER_DATA));
        addColumn(GameConst.KILLS_PLAYER_DATA, RemoteDatabaseRowType.INT, gameUser -> gameUser.getCache().getInt(GameConst.KILLS_PLAYER_DATA));
        addColumn(GameConst.BLOCK_PLACED_PLAYER_DATA, RemoteDatabaseRowType.INT, gameUser -> gameUser.getCache().getInt(GameConst.BLOCK_PLACED_PLAYER_DATA));
        addColumn(GameConst.GAMES_PLAYED_PLAYER_DATA, RemoteDatabaseRowType.INT, gameUser -> gameUser.getCache().getInt(GameConst.GAMES_PLAYED_PLAYER_DATA));
    }

    @Override
    public void onJoinLoad(@NonNull GamePlugin gamePlugin, @NonNull GameUser gameUser) {
        loadPrimary(false, gameUser, gameUser.getCache()::set);
    }

}
