package org.gepron1x.test;

import net.kyori.adventure.text.Component;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.plugin.ClanBuilderFactoryImpl;
import org.gepron1x.clans.plugin.ClanCacheImpl;
import org.gepron1x.clans.plugin.clan.ClanBuilder;
import org.gepron1x.clans.plugin.clan.member.ClanMemberImpl;

import java.util.UUID;

public class ClanCacheTest {

    public static void main(String[] args) {
        ClanCacheImpl cache = new ClanCacheImpl();
        ClanBuilderFactory builderFactory = new ClanBuilderFactoryImpl();
        ClanRole testRole = builderFactory.roleBuilder().name("owner")
                .displayName(Component.text("Owner"))
                .emptyPermissions()
                .weight(1).build();
        Clan clan = new ClanBuilder(1)
                .tag("tag1")
                .displayName(Component.text("tag1"))
                .owner(new ClanMemberImpl(UUID.randomUUID(), testRole))
                .build();

        Clan editedClan = ClanBuilder.asBuilder(clan).addMember(new ClanMemberImpl(UUID.randomUUID(), testRole)).build();

        cache.cacheClan(clan);

        System.out.println(cache);

        cache.removeClan(clan);
        cache.cacheClan(editedClan);

        System.out.println(cache);



    }
}
