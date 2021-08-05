package org.gepron1x.clans.storage.mappers.column;

import org.bukkit.inventory.ItemStack;
import org.jdbi.v3.core.mapper.ColumnMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemStackMapper implements ColumnMapper<ItemStack> {
    @Override
    public ItemStack map(ResultSet r, int columnNumber, StatementContext ctx) throws SQLException {
        return ItemStack.deserializeBytes(r.getBytes(columnNumber));
    }
}
