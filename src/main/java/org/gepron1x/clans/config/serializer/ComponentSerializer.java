package org.gepron1x.clans.config.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public class ComponentSerializer implements ValueSerialiser<Component> {
    private final MiniMessage miniMessage = MiniMessage.get();
    public static final ComponentSerializer INSTANCE = new ComponentSerializer();
    @Override
    public Class<Component> getTargetClass() {
        return Component.class;
    }

    @Override
    public Component deserialise(FlexibleType flexibleType) throws BadValueException {

        return miniMessage.parse(flexibleType.getString());
    }

    @Override
    public Object serialise(Component value, Decomposer decomposer) {
        return miniMessage.serialize(value);
    }
    public static ComponentSerializer getInstance() {
        return INSTANCE;
    }
}
