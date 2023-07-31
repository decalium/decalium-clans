/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin.storage.implementation.sql;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;
import org.flywaydb.core.Flyway;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.IdentifiedDraftClan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.gepron1x.clans.plugin.storage.StorageType;
import org.gepron1x.clans.plugin.storage.implementation.sql.common.SavableHomes;
import org.gepron1x.clans.plugin.storage.implementation.sql.common.SavableMembers;
import org.gepron1x.clans.plugin.storage.implementation.sql.common.SavableStatistics;
import org.gepron1x.clans.plugin.storage.implementation.sql.edition.SqlClanEdition;
import org.intellij.lang.annotations.Language;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.Query;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SqlClanStorage implements ClanStorage {

	@Language("SQL")
	private static final String SELECT_CLANS = "SELECT * FROM clans_simple";

	@Language("SQL")
	private static final String SELECT_CLAN_WITH_ID = "SELECT * FROM clans_simple WHERE`clan_id`=?";

	@Language("SQL")
	private static final String SELECT_CLAN_WITH_TAG = "SELECT * FROM clans_simple WHERE `clan_tag`=?";
	@Language("SQL")
	private static final String SELECT_USER_CLAN = "SELECT * FROM clans_simple WHERE `clan_id`=(SELECT `clan_id` FROM `members` WHERE `uuid`=?)";
	@Language("SQL")
	private static final String INSERT_CLAN = "INSERT IGNORE INTO clans(`tag`, `owner`, `display_name`, `decoration`) VALUES (?, ?, ?, ?)";

	@Language("SQL")
	private static final String DELETE_CLAN = "DELETE FROM clans WHERE id=?";


	private final Jdbi jdbi;
	private final HikariDataSource dataSource;
	private final StorageType type;
	private final ClanCollector collector;
	private final Plugin plugin;


	public SqlClanStorage(@NotNull Plugin plugin,
						  @NotNull Jdbi jdbi,
						  @NotNull HikariDataSource dataSource,
						  @NotNull StorageType type,
						  @NotNull ClanCollector collector) {
		this.plugin = plugin;
		this.jdbi = jdbi;
		this.dataSource = dataSource;
		this.type = type;
		this.collector = collector;
	}

	@Override
	public void initialize() {
		Flyway flyway = Flyway.configure(plugin.getClass().getClassLoader())
				.dataSource(dataSource).baselineVersion("0")
				.locations("classpath:db/migration")
				.baselineOnMigrate(true)
				.validateOnMigrate(true).load();
		flyway.repair();
		flyway.migrate();
	}

	@Override
	public void shutdown() {
		jdbi.useHandle(type::disable);
		dataSource.close();

	}

	@Override
	public @Nullable IdentifiedDraftClan loadClan(@NotNull String tag) {
		return jdbi.withHandle(handle -> collectClans(handle.createQuery(SELECT_CLAN_WITH_TAG).bind(0, tag))
				.findFirst().orElse(null));
	}

	@Override
	public @Nullable IdentifiedDraftClan loadClan(int id) {
		return jdbi.withHandle(handle -> collectClans(handle.createQuery(SELECT_CLAN_WITH_ID).bind(0, id)))
				.findFirst().orElse(null);
	}

	@Override
	public @Nullable IdentifiedDraftClan loadUserClan(@NotNull UUID uuid) {
		return jdbi.withHandle(handle -> collectClans(handle.createQuery(SELECT_USER_CLAN).bind(0, uuid)))
				.findFirst().orElse(null);
	}

	@Override
	public @NotNull Set<IdentifiedDraftClan> loadClans() {
		return jdbi.withHandle(handle -> collectClans(handle.createQuery(SELECT_CLANS))).collect(Collectors.toSet());
	}

	@Override
	public @NotNull Top top() {
		return new SqlClanTop(jdbi, collector);
	}

	@Override
	public SaveResult saveClan(@NotNull DraftClan draftClan) {
		return jdbi.inTransaction(handle -> {
			Optional<Integer> optionalId = handle.createUpdate(INSERT_CLAN)
					.bind(0, draftClan.tag())
					.bind(1, draftClan.owner().uniqueId())
					.bind(2, draftClan.displayName())
					.bind(3, draftClan.tagDecoration())
					.executeAndReturnGeneratedKeys("id").mapTo(Integer.class).findFirst();
			if (optionalId.isEmpty()) {
				handle.rollback();
				return SaveResult.ALREADY_EXISTS;
			}
			int id = optionalId.get();

			int updates = new SavableMembers(id, draftClan.members()).execute(handle); // some members weren't added; those are already in other clans;

			if (updates != draftClan.members().size()) {
				handle.rollback();
				return SaveResult.MEMBERS_IN_OTHER_CLANS;
			}

			new SavableHomes(id, draftClan.homes()).execute(handle);
			new SavableStatistics(id, draftClan.statistics()).execute(handle);
			return SaveResult.success(id);
		});
	}

	@Override
	public void applyEdition(int id, @NotNull Consumer<ClanEdition> consumer) {
		jdbi.useHandle(handle -> consumer.accept(new SqlClanEdition(handle, id)));
	}


	@Override
	public boolean removeClan(int id) {
		return jdbi.inTransaction(handle -> handle.createUpdate(DELETE_CLAN).bind(0, id).execute() != 0);
	}


	private Stream<IdentifiedDraftClan> collectClans(Query query) {
		return collector.collectClans(query);
	}

}
