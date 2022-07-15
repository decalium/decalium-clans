package org.gepron1x.clans.api;

import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.war.Wars;
import org.jetbrains.annotations.NotNull;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public interface DecaliumClansApi extends ClanBuilderFactory {

    @NotNull CachingClanRepository repository();

    @NotNull FactoryOfTheFuture futuresFactory();


    @NotNull ClanBuilderFactory builderFactory();

    @NotNull RoleRegistry roleRegistry();

    @NotNull Wars wars();



   // im lazy
    @Override
    @NotNull
    default DraftClan.Builder draftClanBuilder() {
        return builderFactory().draftClanBuilder();
    }

    @Override
    @NotNull
    default ClanMember.Builder memberBuilder() {
        return builderFactory().memberBuilder();
    }

    @Override
    @NotNull
    default ClanHome.Builder homeBuilder() {
        return builderFactory().homeBuilder();
    }

    @Override
    @NotNull
    default ClanRole.Builder roleBuilder() {
        return builderFactory().roleBuilder();
    }
}
