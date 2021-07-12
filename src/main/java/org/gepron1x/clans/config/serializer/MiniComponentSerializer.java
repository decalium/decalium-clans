package org.gepron1x.clans.config.serializer;

import org.gepron1x.clans.config.MiniComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;


public class MiniComponentSerializer implements ValueSerialiser<MiniComponent> {

    private final MiniMessage miniMessage;
    public MiniComponentSerializer(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }
    @Override
    public Class<MiniComponent> getTargetClass() {
        return MiniComponent.class;
    }

    @Override
    public MiniComponent deserialise(FlexibleType flexibleType) throws BadValueException {
        return new MiniComponent(flexibleType.getString());

    }

    @Override
    public String serialise(MiniComponent value, Decomposer decomposer) {
        return value.getValue();
    }


}
