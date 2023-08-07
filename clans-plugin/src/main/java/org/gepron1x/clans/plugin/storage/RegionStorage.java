package org.gepron1x.clans.plugin.storage;

import org.gepron1x.clans.api.region.GlobalRegions;

public interface RegionStorage {
	GlobalRegions loadRegions();

	void save();
}
