package org.gepron1x.clans.plugin.storage.implementation.sql.mappers.column;

import org.gepron1x.clans.api.decoration.CombinedDecoration;
import org.gepron1x.clans.plugin.util.DecorationAdapter;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DecorationMapper implements ColumnMapper<CombinedDecoration> {

	@Override
	public CombinedDecoration map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
		return DecorationAdapter.fromString(r.getString(columnNumber));
	}
}
