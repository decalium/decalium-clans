package org.gepron1x.clans.plugin.util.hologram;

import org.gepron1x.clans.plugin.util.message.Message;
import space.arim.dazzleconf.error.BadValueException;
import space.arim.dazzleconf.serialiser.Decomposer;
import space.arim.dazzleconf.serialiser.FlexibleType;
import space.arim.dazzleconf.serialiser.ValueSerialiser;

import java.util.LinkedHashMap;
import java.util.Map;

public record Line(double height, Message content) {

	public Line(Message content) {
		this(Double.NaN, content);
	}

	public boolean hasHeight() {
		return !Double.isNaN(height);
	}


	public static final class Serializer implements ValueSerialiser<Line> {

		@Override
		public Class<Line> getTargetClass() {
			return Line.class;
		}

		@Override
		public Line deserialise(FlexibleType flexibleType) throws BadValueException {
			Map<String, FlexibleType> map = flexibleType.getMap((key, value) -> Map.entry(key.getString(), value));
			FlexibleType type = map.get("height");
			return new Line(type == null ? Double.NaN : type.getDouble(), map.get("content").getObject(Message.class));
		}

		@Override
		public Object serialise(Line value, Decomposer decomposer) {
			Map<String, Object> map = new LinkedHashMap<>();
			if(value.hasHeight()) map.put("height", value.height);
			map.put("content", decomposer.decompose(Message.class, value.content));
			return map;
		}
	}

}
