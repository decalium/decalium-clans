package org.gepron1x.clans.api.edition.home;

import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.home.Homes;
import org.gepron1x.clans.api.edition.RegistryEdition;

import java.util.Collection;
import java.util.function.Consumer;

public interface HomesEdition extends RegistryEdition<String, ClanHome, HomeEdition, Homes> {

    @Override
    HomesEdition add(ClanHome value);

    @Override
    HomesEdition add(Collection<ClanHome> values);

    @Override
    HomesEdition remove(String key);

    @Override
    HomesEdition edit(String key, Consumer<HomeEdition> consumer);
}
