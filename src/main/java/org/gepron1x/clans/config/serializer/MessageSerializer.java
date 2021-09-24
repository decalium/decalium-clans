package org.gepron1x.clans.config.serializer;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.gepron1x.clans.config.Message;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;


public class MessageSerializer implements ValueSerialiser<Message> {

    private final MiniMessage miniMessage;
    public MessageSerializer(MiniMessage miniMessage) {
        this.miniMessage = miniMessage;
    }
    @Override
    public Class<Message> getTargetClass() {
        return Message.class;
    }

    @Override
    public Message deserialise(FlexibleType flexibleType) throws BadValueException {
        return new Message(flexibleType.getString(), miniMessage);

    }

    @Override
    public String serialise(Message value, Decomposer decomposer) {
        return value.getValue();
    }


}
