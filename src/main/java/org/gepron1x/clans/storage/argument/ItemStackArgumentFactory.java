package org.gepron1x.clans.storage.argument;

import org.bukkit.inventory.ItemStack;
import org.jdbi.v3.core.argument.AbstractArgumentFactory;
import org.jdbi.v3.core.argument.Argument;
import org.jdbi.v3.core.config.ConfigRegistry;

import java.sql.Types;

public class ItemStackArgumentFactory extends AbstractArgumentFactory<ItemStack> {
    public ItemStackArgumentFactory() {
        super(Types.BLOB);
    }

    @Override
    protected Argument build(ItemStack value, ConfigRegistry config) {
        return ((position, statement, ctx) -> statement.setBytes(position, value.serializeAsBytes()));
    }
}
