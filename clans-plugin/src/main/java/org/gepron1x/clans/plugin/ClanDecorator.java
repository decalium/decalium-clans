package org.gepron1x.clans.plugin;

import org.gepron1x.clans.api.clan.Clan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface ClanDecorator extends UnaryOperator<Clan> {

	Clan decorate(Clan clan);

	@Override
	default Clan apply(Clan clan) {
		return decorate(clan);
	}

	static ClanDecorator combined(List<ClanDecorator> decorators) {
		return clan -> {
			for(ClanDecorator decorator : decorators) clan = decorator.decorate(clan);
			return clan;
		};
	}

	static Builder builder() {
		return new Builder();
	}


	final class Builder {


		private final List<ClanDecorator> decorators = new ArrayList<>();
		private Builder() {}


		public Builder add(ClanDecorator decorator) {
			this.decorators.add(decorator);
			return this;
		}

		public Builder add(Collection<? extends ClanDecorator> decorators) {
			this.decorators.addAll(decorators);
			return this;
		}

		public Builder add(ClanDecorator... decorators) {
			return add(List.of(decorators));
		}

		public ClanDecorator build() {
			return ClanDecorator.combined(List.copyOf(decorators));
		}
	}

}
