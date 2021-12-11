package org.gepron1x.clans.plugin.storage;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;

public interface StorageService {
    <T> T withRepository(@NotNull Function<ClanStorage, T> function);


    default void useRepository(@NotNull Consumer<ClanStorage> consumer) {
        withRepository(repo -> {
            consumer.accept(repo);
            return null;
        });
    }
}
