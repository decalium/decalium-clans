package org.gepron1x.clans.plugin.config.serializer;

import org.gepron1x.clans.api.chat.action.Action;
import org.gepron1x.clans.api.chat.action.Message;
import org.gepron1x.clans.plugin.util.action.ActionParser;
import org.gepron1x.clans.plugin.util.action.CombinedAction;
import org.gepron1x.clans.plugin.util.action.ParsedAction;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.List;
import java.util.stream.StreamSupport;

public final class MessageSerializer implements ValueSerialiser<Message> {

	private final ActionParser parser;

	public MessageSerializer(ActionParser parser) {
		this.parser = parser;
	}
	@Override
	public Class<Message> getTargetClass() {
		return Message.class;
	}

	@Override
	public Message deserialise(FlexibleType flexibleType) throws BadValueException {
		return Message.create(parser.parse(flexibleType.getList(FlexibleType::getString)));
	}

	@Override
	public Object serialise(Message value, Decomposer decomposer) {
		if(value.action() instanceof ParsedAction action) {
			return action.value();
		} else if(value.action() instanceof CombinedAction action) {
			List<String> strings = StreamSupport.stream(action.actions().spliterator(), false).filter(ParsedAction.class::isInstance)
					.map(ParsedAction.class::cast).map(ParsedAction::value).toList();
			return decomposer.decomposeCollection(String.class, strings);
		} else if(value.action() == Action.EMPTY) {
			return "[empty]";
		}
		throw new IllegalArgumentException("Dont know how to serialize " + value.action());
	}
}
