package org.gepron1x.clans.api.economy;

import org.gepron1x.clans.api.clan.DraftClan;

import java.time.Duration;

public interface LevelsMeta {

	
	
	int maxLevel();

	AllowAt allowAt();

	PerLevel forLevel(int level);


	default PerLevel forLevel(DraftClan clan) {
		return forLevel(clan.level());
	}

	interface AllowAt {

		
		int wars();

		
		int regions();

		
		int homes();

		
		
		int regionEffects();

		int shields();

	}

	
	

	interface PerLevel {

		
		int slots();

		
		int homes();

		
		int regions();
		Duration shieldDuration();
	}
}
