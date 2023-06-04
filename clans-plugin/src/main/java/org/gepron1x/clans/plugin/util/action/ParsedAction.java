package org.gepron1x.clans.plugin.util.action;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public record ParsedAction(Action action, String value) implements Action {
	@Override
	public void send(Audience audience, TagResolver resolver) {
		action.send(audience, resolver);
	}
}
