package org.gepron1x.clans.api.shield;

public class RegionOverlapsException extends RuntimeException {


	public RegionOverlapsException() {

	}

	@Override
	public String getMessage() {
		return "Region Overlaps with different region.";
	}
}
