package org.gepron1x.clans.storage;

import org.jdbi.v3.core.Jdbi;
@FunctionalInterface
public interface Loader<T> {
    T load(Jdbi jdbi);

}
