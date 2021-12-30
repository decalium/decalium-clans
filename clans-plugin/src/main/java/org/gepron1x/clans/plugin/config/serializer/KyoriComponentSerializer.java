package org.gepron1x.clans.plugin.config.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public final class KyoriComponentSerializer implements ValueSerialiser<Component> {

    private final ComponentSerializer<Component, ? extends Component, String> componentSerializer;

    public KyoriComponentSerializer(ComponentSerializer<Component, ? extends Component, String> componentSerializer) {

        this.componentSerializer = componentSerializer;
    }
    @Override
    public Class<Component> getTargetClass() {
        return Component.class;
    }

    @Override
    public Component deserialise(FlexibleType flexibleType) throws BadValueException {
        return componentSerializer.deserialize(flexibleType.getString());
    }

    @Override
    public String serialise(Component value, Decomposer decomposer) {
        return componentSerializer.serialize(value);
    }
}
