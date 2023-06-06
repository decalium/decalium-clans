package org.gepron1x.clans.api.chat;

import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.title.Title;
import org.jetbrains.annotations.NotNull;

public interface GroupAudience extends ForwardingAudience {

	@Override
	default void showTitle(@NotNull Title title) {
		forEachAudience(audience -> audience.showTitle(title));
	}


	interface Single extends ForwardingAudience.Single {

		@Override
		default void showTitle(@NotNull Title title) {
			audience().showTitle(title);
		}
	}
}
