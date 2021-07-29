package org.gepron1x.clans.storage.task;

import org.jdbi.v3.core.Jdbi;

import java.util.function.Consumer;
@FunctionalInterface
public interface DatabaseUpdate extends Consumer<Jdbi> {
}
