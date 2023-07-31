package org.gepron1x.clans.plugin.storage.implementation.sql;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.plugin.storage.IdentifiedDraftClanImpl;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.ClanBuilderMapper;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.ClanHomeBuilderMapper;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.LocationMapper;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.row.MemberMapper;
import org.jdbi.v3.core.statement.Query;

import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.stream.Stream;

public final class ClanCollector {
	private final ClanBuilderFactory builderFactory;
	private final RoleRegistry roleRegistry;

	public ClanCollector(ClanBuilderFactory builderFactory, RoleRegistry roleRegistry) {
		this.builderFactory = builderFactory;
		this.roleRegistry = roleRegistry;
	}


	public Stream<IdentifiedDraftClan> collectClans(Query query) {
		return query.registerRowMapper(DraftClan.Builder.class, new ClanBuilderMapper("clan", builderFactory))
				.registerRowMapper(ClanMember.class, new MemberMapper(builderFactory, roleRegistry, "member"))
				.registerRowMapper(ClanHome.Builder.class, new ClanHomeBuilderMapper(builderFactory, "home"))
				.registerRowMapper(Location.class, new LocationMapper(Bukkit.getServer(), "location"))
				.reduceRows(new LinkedHashMap<Integer, DraftClan.Builder>(), (map, rowView) -> {
					DraftClan.Builder builder = map.computeIfAbsent(
							rowView.getColumn("clan_id", Integer.class),
							clanTag -> rowView.getRow(DraftClan.Builder.class));
					UUID owner = rowView.getColumn("clan_owner", UUID.class);

					if (rowView.getColumn("member_uuid", byte[].class) != null) {
						ClanMember member = rowView.getRow(ClanMember.class);
						if (member.uniqueId().equals(owner)) {
							builder.owner(member);
						} else {
							builder.addMember(rowView.getRow(ClanMember.class));
						}
					}

					if (rowView.getColumn("home_name", String.class) != null) {
						builder.addHome(rowView.getRow(ClanHome.Builder.class).location(rowView.getRow(Location.class)).build());
					}
					String statType = rowView.getColumn("statistic_type", String.class);

					if (statType != null) {
						builder.statistic(StatisticType.type(statType), rowView.getColumn("statistic_value", Integer.class));
					}
					return map;
				}).entrySet().stream().map(entry -> new IdentifiedDraftClanImpl(entry.getKey(), entry.getValue().build()));
	}



}
