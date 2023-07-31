package org.gepron1x.clans.plugin.storage.implementation.sql;

import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.intellij.lang.annotations.Language;
import org.jdbi.v3.core.Jdbi;

import java.util.List;

public final class SqlClanTop implements ClanStorage.Top {
	@Language("SQL")
	private static final String SELECT_ORDER_BY_STATISTIC_TYPE = """
						SELECT clans_simple.* FROM
						(SELECT * FROM statistics WHERE statistics.`type`=?) AS statistics
						LEFT JOIN clans_simple ON statistics.clan_id = clans_simple.clan_id ORDER BY statistics.`value` DESC
						""";


	@Language("SQL")
	private static final String SELECT_ORDER_BY_STATISTIC_TYPE_LIMIT = """
						SELECT clans_simple.* FROM
						(SELECT * FROM statistics WHERE statistics.`type`=? LIMIT ?) AS statistics
						LEFT JOIN clans_simple ON statistics.clan_id = clans_simple.clan_id ORDER BY statistics.`value` DESC
						""";

	private final Jdbi jdbi;
	private final ClanCollector collector;
	public SqlClanTop(Jdbi jdbi, ClanCollector collector) {

		this.jdbi = jdbi;
		this.collector = collector;
	}

	@Override
	public List<IdentifiedDraftClan> top(StatisticType type) {

		return jdbi.withHandle(handle ->
				collector.collectClans(handle.createQuery(SELECT_ORDER_BY_STATISTIC_TYPE)
						.bind(0, type)
				).toList()
		);
	}

	@Override
	public List<IdentifiedDraftClan> top(StatisticType type, int limit) {
		return jdbi.withHandle(handle ->
				collector.collectClans(handle.createQuery(SELECT_ORDER_BY_STATISTIC_TYPE_LIMIT)
						.bind(0, type)
						.bind(1, limit)
				).toList()
		);
	}
}
