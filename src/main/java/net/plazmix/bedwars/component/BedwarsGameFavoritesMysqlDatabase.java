package net.plazmix.bedwars.component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.NonNull;
import net.plazmix.bedwars.shop.favorites.PlayerFavorites;
import net.plazmix.game.GamePlugin;
import net.plazmix.game.mysql.GameMysqlDatabase;
import net.plazmix.game.mysql.RemoteDatabaseRowType;
import net.plazmix.game.user.GameUser;

public final class BedwarsGameFavoritesMysqlDatabase extends GameMysqlDatabase {

    private final Gson gson = new GsonBuilder()
            .create();

    public BedwarsGameFavoritesMysqlDatabase() {
        super("bedwars_favorites", true);
    }

    @Override
    public void initialize() {
        addColumn(PlayerFavorites.KEY, RemoteDatabaseRowType.LONG_TEXT, gameUser -> {
            PlayerFavorites playerFavorites = gameUser.getCache().get(PlayerFavorites.KEY, PlayerFavorites.class);
            if (playerFavorites == null) {
                playerFavorites = PlayerFavorites.DEFAULT;
            }
            return playerFavorites;
        });
    }

    @Override
    public void onJoinLoad(@NonNull GamePlugin gamePlugin, @NonNull GameUser gameUser) {
        loadPrimary(false, gameUser, (str, obj) -> gameUser.getCache().set(str, gson.fromJson((String) obj, PlayerFavorites.class)));
    }

}
