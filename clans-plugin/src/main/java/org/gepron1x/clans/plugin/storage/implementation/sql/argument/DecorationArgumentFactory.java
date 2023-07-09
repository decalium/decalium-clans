package org.gepron1x.clans.plugin.storage.implementation.sql.argument;

import org.gepron1x.clans.api.decoration.CombinedDecoration;
import org.gepron1x.clans.plugin.util.DecorationAdapter;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;

public class DecorationArgumentFactory extends AbstractArgumentFactory<CombinedDecoration> {
	protected DecorationArgumentFactory() {
		super(Types.VARCHAR);
	}

	@Override
	protected Argument build(CombinedDecoration value, ConfigRegistry config) {
		return (position, statement, ctx) -> statement.setString(position, DecorationAdapter.asString(value));
	}
}
