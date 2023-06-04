package org.gepron1x.clans.plugin.config.serializer;

import org.gepron1x.clans.plugin.util.action.Action;
import org.gepron1x.clans.plugin.util.action.ActionParser;
import org.gepron1x.clans.plugin.util.action.CombinedAction;
import org.gepron1x.clans.plugin.util.action.ParsedAction;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.List;
import java.util.stream.StreamSupport;

public final class ActionSerializer implements ValueSerialiser<Action> {

	private final ActionParser parser;

	public ActionSerializer(ActionParser parser) {
		this.parser = parser;
	}
	@Override
	public Class<Action> getTargetClass() {
		return Action.class;
	}

	@Override
	public Action deserialise(FlexibleType flexibleType) throws BadValueException {
		return parser.parse(flexibleType.getList(FlexibleType::getString));
	}

	@Override
	public Object serialise(Action value, Decomposer decomposer) {
		if(value instanceof ParsedAction action) {
			return action.value();
		} else if(value instanceof CombinedAction action) {
			List<String> strings = StreamSupport.stream(action.actions().spliterator(), false).filter(ParsedAction.class::isInstance)
					.map(ParsedAction.class::cast).map(ParsedAction::value).toList();
			return decomposer.decomposeCollection(String.class, strings);
		} else if(value == Action.EMPTY) {
			return "[empty]";
		}
		throw new IllegalArgumentException("Dont know how to serialize " + value);
	}
}
