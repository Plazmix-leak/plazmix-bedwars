package net.plazmix.bedwars.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.function.Consumer;

@UtilityClass
public class Functions {

    public <T> T accept(@NonNull T t, @NonNull Consumer<T> action) {
        action.accept(t);

        return t;
    }
}