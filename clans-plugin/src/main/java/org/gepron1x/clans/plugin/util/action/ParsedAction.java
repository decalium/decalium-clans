package org.gepron1x.clans.plugin.util.action;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.gepron1x.clans.api.chat.action.Action;

import java.util.Optional;

public record ParsedAction(Action action, String value) implements Action {
	@Override
	public void send(Audience audience, TagResolver resolver) {
		action.send(audience, resolver);
	}

	@Override
	public Optional<Component> text(TagResolver resolver) {
		return action.text(resolver);
	}
}
