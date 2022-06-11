package org.gepron1x.clans.api.edition;

import org.gepron1x.clans.api.Registry;

import java.util.Collection;
import java.util.function.Consumer;

public interface RegistryEdition<K, V, E extends Edition<V>, T extends Registry<K, V>> extends Edition<T> {


    RegistryEdition<K, V, E, T> add(V value);
    RegistryEdition<K, V, E, T> add(Collection<V> values);

    RegistryEdition<K, V, E, T> remove(K key);

    RegistryEdition<K, V, E, T> edit(K key, Consumer<E> consumer);




}
