package org.gepron1x.clans.api.clan;

import java.util.Optional;
import java.util.Set;

public interface Homes extends Iterable<ClanHome> {

    Set<ClanHome> homes();

    Optional<ClanHome> home(String name);



}
