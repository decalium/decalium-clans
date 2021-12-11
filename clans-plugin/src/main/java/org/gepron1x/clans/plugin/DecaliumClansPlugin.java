package org.gepron1x.clans.plugin;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.ClanManager;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.statistic.StatisticType;

import java.util.UUID;

public final class DecaliumClansPlugin extends JavaPlugin {

    @Override
    public void onEnable() {



    }

    public static void test(DecaliumClansApi api) {
        UUID uuid = UUID.randomUUID();
        ClanRole ownerRole = api.getRoles().value("owner");
        Clan clan = api.clanBuilder().tag("test_clan1").owner(uuid)
                .displayName(Component.text("Test clan 1"))
                .addMember(api.memberBuilder().uuid(uuid).role(ownerRole).build())
                .build();
        ClanManager manager = api.getClanManager();

        manager.addClan(clan).thenAccept(result -> {
            if(result == ClanManager.CreationResult.SUCCESS) {
                System.out.println("clan created successfully");
            } else if (result == ClanManager.CreationResult.ALREADY_EXISTS) {
                System.err.println("clan with given tag already exists!");
            }

        });


    }



    @Override
    public void onDisable() {

    }
}
