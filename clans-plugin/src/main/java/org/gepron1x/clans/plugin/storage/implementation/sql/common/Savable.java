package org.gepron1x.clans.plugin.storage.implementation.sql.common;

import org.jdbi.v3.core.Handle;

public interface Savable {

    int execute(Handle handle);
}
