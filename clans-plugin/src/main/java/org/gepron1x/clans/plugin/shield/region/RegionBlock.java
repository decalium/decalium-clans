package org.gepron1x.clans.plugin.shield.region;

import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;

import java.util.Optional;

public final class RegionBlock {


	private static final DecaliumClansPlugin plugin = JavaPlugin.getPlugin(DecaliumClansPlugin.class);

	private static final String REGION_ID = "decaliumclans_region_id";


	public static void set(Block block, int id) {
		block.setMetadata(REGION_ID, new FixedMetadataValue(plugin, id));
	}

	public static Optional<Integer> get(Block block) {
		return block.getMetadata(REGION_ID).stream().map(MetadataValue::asInt).findAny();
	}

	public static void remove(Block block) {
		block.removeMetadata(REGION_ID, plugin);
	}


}
