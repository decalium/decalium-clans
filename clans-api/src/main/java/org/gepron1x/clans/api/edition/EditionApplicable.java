package org.gepron1x.clans.api.edition;

import java.util.function.Consumer;

public interface EditionApplicable<T, E extends Edition<T>> {

    void applyEdition(Consumer<E> consumer);
}
