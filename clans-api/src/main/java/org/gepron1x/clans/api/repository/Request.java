package org.gepron1x.clans.api.repository;

import java.util.concurrent.CompletableFuture;

public interface Request<V> {

    CompletableFuture<V> request();
}
