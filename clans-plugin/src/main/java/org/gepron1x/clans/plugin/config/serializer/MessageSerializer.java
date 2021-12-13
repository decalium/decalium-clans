package org.gepron1x.clans.plugin.config.serializer;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.gepron1x.clans.plugin.util.Message;
import org.jetbrains.annotations.NotNull;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

public final class MessageSerializer implements ValueSerialiser<Message> {

    private final MiniMessage miniMessage;

    public MessageSerializer(@NotNull MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }

    @Override
    public Class<Message> getTargetClass() {
        return Message.class;
    }

    @Override
    public Message deserialise(FlexibleType flexibleType) throws BadValueException {
        return Message.message(flexibleType.getString(), miniMessage);
    }

    @Override
    public String serialise(Message value, Decomposer decomposer) {
        return value.value();
    }
}
