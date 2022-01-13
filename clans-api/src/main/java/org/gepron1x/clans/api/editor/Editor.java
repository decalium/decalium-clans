package org.gepron1x.clans.api.editor;

import org.jetbrains.annotations.NotNull;

public interface Editor<T> {
    @NotNull Class<T> getTarget();
}
