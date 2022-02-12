package org.gepron1x.clans.plugin.storage.implementation.sql.mappers.column;

import org.bukkit.inventory.ItemStack;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ItemStackMapper implements ColumnMapper<ItemStack> {
    @Override
    public ItemStack map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        return ItemStack.deserializeBytes(r.getBytes(columnNumber));
    }
}
