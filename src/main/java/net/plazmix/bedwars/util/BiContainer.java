package net.plazmix.bedwars.util;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@EqualsAndHashCode
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class BiContainer<F, S> {

    @NonNull F firstValue;
    @NonNull S secondValue;

    public F firstValue() {
        return firstValue;
    }

    public S secondValue() {
        return secondValue;
    }
}