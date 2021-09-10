package org.gepron1x.clans;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import com.sk89q.worldguard.WorldGuard;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.Index;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.clan.member.role.ClanRole;
import org.gepron1x.clans.command.ClanCommand;
import org.gepron1x.clans.command.InviteCommand;
import org.gepron1x.clans.config.ClansConfig;
import org.gepron1x.clans.config.ConfigManager;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.config.serializer.*;
import org.gepron1x.clans.statistic.StatisticType;
import org.gepron1x.clans.storage.*;
import org.gepron1x.clans.util.pdc.DataTypes;
import org.gepron1x.clans.util.pdc.collection.CollectionDataType;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;
import space.arim.dazzleconf.ConfigurationOptions;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public final class DecaliumClans extends JavaPlugin {
    public static final String AUTHOR = "gepron1x";
    public static final String NAME = "decaliumclans";
    public static final String VERSION = "1.0.0";


    private Index<String, ClanRole> roleRegistry;
    private final Index<String, StatisticType> statisticTypeRegistry = Index.create(StatisticType::getName,
            StatisticType.KILLS,
            StatisticType.DEATHS,
            StatisticType.CLAN_WAR_LOSES,
            StatisticType.CLAN_WAR_WINS);

    private ClanRole defaultRole, ownerRole;
    private ConfigManager<MessagesConfig> messagesConfigManager;
    private ClanManager clanManager;
    private WorldGuard worldGuard;
    private final Path dataFolder = getDataFolder().toPath();
    private ConfigManager<ClansConfig> configManager;
    private Storage storage;
    private MiniMessage miniMessage;
    private PaperCommandManager commandManager;
    private ClanCommand command;
    private InviteCommand inviteCommand;


    @Override
    public void onEnable() {


        enable();

        getLogger().info("Plugin enabled.");
    }

    private void enable() {
        setupConfigurations();
        this.miniMessage = MiniMessage.builder().placeholderResolver(s ->
        {
            if(s.equals("prefix")) return getPrefix();
            else return null;
        }).build();
        getLogger().info("Initializing storage...");
        this.storage = new StorageCreator(this).create();
        getLogger().info("Storage initialized. Loading clans...");
        this.clanManager = new ClanManager(storage.loadClans());
        getLogger().info("Clans loaded.");
        storage.start();
        getLogger().info("Scheduled save task.");

        ClansConfig cfg = getClansConfig();
        roleRegistry = Index.create(ClanRole::getName, cfg.roles());

        commandManager = new PaperCommandManager(this);
        registerCompletions();
        registerContexts();

        command = new ClanCommand(this, clanManager, getMessages());
        inviteCommand = new InviteCommand(this, clanManager, getMessages());

        Arrays.asList(command, inviteCommand).forEach(commandManager::registerCommand);

        worldGuard = WorldGuard.getInstance();

        getClansConfig().statisticTypes()
                .forEach((key, value) -> {
                    StatisticType type = statisticTypeRegistry.value(key);
                    if(type != null) type.setDisplayName(value.asComponent());
                });
        defaultRole = roleRegistry.value(cfg.defaultRole());
        ownerRole = roleRegistry.value(cfg.ownerRole());
    }
    private void disable() {
        HandlerList.unregisterAll(this);
        commandManager.unregisterCommands();
        getLogger().info("Saving clans");
        storage.shutdown();
        getLogger().info("Saved successfully.");
    }


    public ComponentLike getPrefix() {
        return getMessages().prefix();
    }



    public void reload() {
        getLogger().info("reloading plugin...");
        disable();
        enable();
        getLogger().info("reloaded plugin successfully.");

    }

    private void registerContexts() {
        var commandContexts = commandManager.getCommandContexts();

        commandContexts.registerContext(ClanRole.class, ctx -> {
            String name = ctx.popFirstArg();
            return getRoleRegistry().value(name);
        });
        commandContexts.registerContext(Clan.class, ctx -> clanManager.getClan(ctx.popFirstArg()));


    }
    private void registerCompletions() {
        var commandCompletions = commandManager.getCommandCompletions();
        commandCompletions.registerCompletion("roles", ctx -> roleRegistry.keys());

        commandCompletions.registerCompletion("clans",
                ctx -> clanManager.getClans().stream().map(Clan::getTag).collect(Collectors.toList()));
        commandCompletions.registerCompletion("members", ctx -> {
            Clan clan = clanManager.getUserClan(ctx.getPlayer());
            if(clan == null) return Collections.emptyList();
            return clan.getMembers().stream().map(ClanMember::asOffline)
                    .filter(Objects::nonNull).map(OfflinePlayer::getName)
                    .collect(Collectors.toList());
        });
    }

    private void setupConfigurations() {
        ConfigurationOptions defaultOptions = new ConfigurationOptions.Builder()
                .addSerialisers(new ComponentSerializer(),
                        new MiniComponentSerializer(miniMessage),
                        new ClanRoleSerializer(),
                        new ClanPermissionSerializer(), new DurationSerializer()).build();

        configManager = ConfigManager.create(dataFolder,
                "config.yml",
                ClansConfig.class,
                defaultOptions);
        configManager.reloadConfig();

        messagesConfigManager = ConfigManager.create(dataFolder,
                "messages.yml",
                MessagesConfig.class,
                defaultOptions);
        messagesConfigManager.reloadConfig();
    }



    @Override
    public void onDisable() {
        disable();
        getLogger().info("Plugin disabled. Goodbye and cya <3");
    }
    @NotNull
    public Index<String, ClanRole> getRoleRegistry() {
        return roleRegistry;
    }
    @NotNull
    public Jdbi getJdbi() {
        return storage.getJdbi();
    }

    @NotNull
    public ClansConfig getClansConfig() {
        return configManager.getConfigData();
    }
    @NotNull
    public MessagesConfig getMessages() {
        return messagesConfigManager.getConfigData();
    }
    @NotNull
    public ClanManager getClanManager() {
        return clanManager;
    }
    @NotNull
    public ClanRole getOwnerRole() {
        return ownerRole;
    }
    @NotNull
    public ClanRole getDefaultRole() {
        return defaultRole;
    }
    @NotNull
    public MiniMessage getMiniMessage() {
        return miniMessage;
    }
    @NotNull
    public Index<String, StatisticType> getStatisticTypeRegistry() {
        return statisticTypeRegistry;
    }
    @NotNull
    public Storage getStorage() {
        return storage;
    }
}
