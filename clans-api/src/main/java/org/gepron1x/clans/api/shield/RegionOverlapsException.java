package org.gepron1x.clans.api.shield;

public class RegionOverlapsException extends RuntimeException {

	private final ClanRegion region;

	public RegionOverlapsException(ClanRegion region) {

		this.region = region;
	}

	public ClanRegion region() {
		return region;
	}

	@Override
	public String getMessage() {
		return "Region Overlaps with different region.";
	}
}
