package net.plazmix.bedwars.shop.favorites;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class PlayerFavorites {

    public static final String KEY = "favorites";

    public static final PlayerFavorites DEFAULT = new PlayerFavorites(Lists.newArrayList(
            1, 7, 23, 22, 14, 24, 29,
            2, 8, 19, 21, 33, 25, 4
    ));

    private final List<Integer> entries;
}
