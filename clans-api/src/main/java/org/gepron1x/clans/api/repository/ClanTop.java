package org.gepron1x.clans.api.repository;

import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.statistic.StatisticType;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.stream.Stream;

public interface ClanTop {


	CentralisedFuture<Stream<Clan>> sortBy(StatisticType type);

	CentralisedFuture<Stream<Clan>> sortBy(StatisticType type, int limit);
}
