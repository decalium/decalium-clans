package org.gepron1x.clans.plugin.wg;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.HashMap;
import java.util.Map;

public final class FlagSetImpl implements FlagSet {

	private final Map<Flag<?>, Object> map;

	public FlagSetImpl(Map<Flag<?>, Object> map) {
		this.map = map;
	}


	@Override
	public void apply(ProtectedRegion region) {
		region.setFlags(map);
	}

	@Override
	public void clear(ProtectedRegion region) {
		map.keySet().forEach(flag -> region.setFlag(flag, null));
	}

	@Override
	public Map<String, ?> serialize() {
		Map<String, Object> serialized = new HashMap<>(map.size());
		map.forEach((key, value) -> {
			serialized.put(key.getName(), marshal(key, value));
		});
		return serialized;
	}

	private static <T> Object marshal(Flag<T> flag, Object object) {
		return flag.marshal((T) object);
	}
}
