package org.gepron1x.clans.plugin.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.clan.DraftClan;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

public class ClanCommand extends AbstractCommand {

    private static final String TAG = "tag";

    private final DecaliumClansApi clansApi;
    private final ClansConfig config;

    public ClanCommand(@NotNull DecaliumClansApi clansApi, @NotNull ClansConfig config) {

        this.clansApi = clansApi;
        this.config = config;
    }
    @Override
    public void register(CommandManager<CommandSender> manager) {

    }


    private void createClan(CommandContext<CommandSender> context) {
        Player player = (Player) context.getSender();
        String tag = context.get("tag");
        Component displayName = Objects.requireNonNull(context.getOrSupplyDefault("display_name", () -> Component.text(tag)));
        UUID uuid = player.getUniqueId();
        DraftClan clan = clansApi.clanBuilder().tag(tag)
                .displayName(displayName)
                .owner(uuid)
                .addMember(clansApi.memberBuilder()
                        .uuid(uuid)
                        .role(clansApi.getRoleRegistry().getOwnerRole())
                        .build()
                ).build();
        clansApi.getClanManager().createClan(clan).thenAccept(result -> {

        });

    }
}
