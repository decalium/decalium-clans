package org.gepron1x.clans.config.serializer;

import org.bukkit.inventory.ItemStack;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.Map;

public class ItemStackSerializer implements ValueSerialiser<ItemStack> {
    @Override
    public Class<ItemStack> getTargetClass() {
        return ItemStack.class;
    }

    @Override
    public ItemStack deserialise(FlexibleType flexibleType) throws BadValueException {
        Map<String, Object> map = flexibleType.getMap((key, value) -> Map.entry(key.getString(), key.getObject(Object.class)));
        return ItemStack.deserialize(map);
    }

    @Override
    public Map<String, Object> serialise(ItemStack value, Decomposer decomposer) {
        return value.serialize();
    }
}
