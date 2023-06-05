package org.gepron1x.clans.plugin.util.action;

import com.google.common.base.MoreObjects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.gepron1x.clans.plugin.util.message.Formatted;

import java.util.Collection;
import java.util.Objects;

public interface FormattedAction extends Formatted<FormattedAction>, Action {

	Action action();

	default void send(Audience audience) {
		send(audience, TagResolver.empty());
	}

	static FormattedAction create(Action action) {
		return new Impl(action);
	}

	record Impl(Action action) implements FormattedAction {

		@Override
		public void send(Audience audience, TagResolver resolver) {
			action.send(audience, resolver);
		}

		@Override
		public FormattedAction with(TagResolver tagResolver) {
			return new Mutable(action, TagResolver.builder().resolver(tagResolver));
		}

		@Override
		public FormattedAction with(String key, Tag tag) {
			return new Mutable(action, TagResolver.builder().tag(key, tag));
		}

		@Override
		public FormattedAction with(Collection<? extends TagResolver> resolvers) {
			return new Mutable(action, TagResolver.builder().resolvers(resolvers));
		}
	}

	final class Mutable implements FormattedAction {

		private final Action action;
		private final TagResolver.Builder builder;

		public Mutable(Action action, TagResolver.Builder builder) {

			this.action = action;
			this.builder = builder;
		}

		@Override
		public void send(Audience audience, TagResolver resolver) {
			action.send(audience, TagResolver.resolver(resolver, builder.build()));
		}

		@Override
		public void send(Audience audience) {
			action.send(audience, builder.build());
		}

		@Override
		public FormattedAction with(TagResolver tagResolver) {
			builder.resolver(tagResolver);
			return this;
		}

		@Override
		public FormattedAction with(String key, Tag tag) {
			builder.tag(key, tag);
			return this;
		}

		@Override
		public FormattedAction with(Collection<? extends TagResolver> resolvers) {
			builder.resolvers(resolvers);
			return this;
		}

		@Override
		public Action action() {
			return action;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Mutable mutable = (Mutable) o;
			return Objects.equals(action, mutable.action) && Objects.equals(builder, mutable.builder);
		}

		@Override
		public int hashCode() {
			return Objects.hash(action, builder);
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("action", action)
					.add("builder", builder)
					.toString();
		}
	}


}
