package org.gepron1x.clans.plugin.storage.implementation.sql.common;

import org.gepron1x.clans.api.statistic.StatisticType;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.util.Arrays;
import java.util.Map;

public final class SavableStatistics implements Savable {
    private static final String INSERT_STATISTIC = "INSERT INTO statistics (clan_id, type, value) (?, ?, ?) ON DUPLICATE KEY UPDATE value=?";
    private final Handle handle;
    private final int clanId;
    private final Map<StatisticType, Integer> statistics;

    public SavableStatistics(Handle handle, int clanId, Map<StatisticType, Integer> statistics) {

        this.handle = handle;
        this.clanId = clanId;
        this.statistics = statistics;
    }

    public SavableStatistics(Handle handle, int clanId, StatisticType type, int value) {
        this(handle, clanId, Map.of(type, value));
    }
    @Override
    public int execute() {
        PreparedBatch batch = this.handle.prepareBatch(INSERT_STATISTIC);
        this.statistics.forEach((key, value) -> {
            batch.add(this.clanId, key, value);
        });

        return Arrays.stream(batch.execute()).sum();
    }
}
