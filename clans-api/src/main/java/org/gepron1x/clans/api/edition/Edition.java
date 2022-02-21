package org.gepron1x.clans.api.edition;

import org.jetbrains.annotations.NotNull;

public interface Edition<T> {
    @NotNull Class<T> getTarget();
}
