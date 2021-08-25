package org.gepron1x.clans;

import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandContexts;
import co.aikar.commands.PaperCommandManager;
import com.sk89q.worldguard.WorldGuard;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.Index;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.role.ClanRole;
import org.gepron1x.clans.command.ClanCommand;
import org.gepron1x.clans.command.InviteCommand;
import org.gepron1x.clans.config.ClansConfig;
import org.gepron1x.clans.config.ConfigManager;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.config.serializer.*;
import org.gepron1x.clans.statistic.StatisticType;
import org.gepron1x.clans.storage.*;
import org.jdbi.v3.core.Jdbi;
import space.arim.dazzleconf.ConfigurationOptions;

import java.nio.file.Path;
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
        setupConfigurations();
        this.miniMessage = MiniMessage.builder().placeholderResolver(s ->
        {
            if(s.equals("prefix")) return getPrefix();
            else return null;
        }).build();

        Storage storage = new StorageCreator(this).create();
        this.clanManager = new ClanManager(storage.loadClans());
        storage.start();

        ClansConfig cfg = getClansConfig();
        roleRegistry = Index.create(ClanRole::getName, cfg.roles());

        commandManager = new PaperCommandManager(this);
        command = new ClanCommand(this, clanManager, getMessages());
        inviteCommand = new InviteCommand(this, clanManager, getMessages());
        commandManager.registerCommand(command);
        commandManager.registerCommand(inviteCommand);
        worldGuard = WorldGuard.getInstance();

        defaultRole = roleRegistry.value(cfg.defaultRole());
        ownerRole = roleRegistry.value(cfg.ownerRole());

    }


    public ComponentLike getPrefix() {
        return messagesConfigManager.getConfigData().prefix();
    }



    public void reload() {
        configManager.reloadConfig();
        ClansConfig config = configManager.getConfigData();
        messagesConfigManager.reloadConfig();
        command.setMessages(messagesConfigManager.getConfigData());
        inviteCommand.setMessages(messagesConfigManager.getConfigData());

    }

    private void registerContexts() {
        CommandContexts<BukkitCommandExecutionContext> commandContexts = commandManager.getCommandContexts();

        commandContexts.registerContext(ClanRole.class, ctx -> {
            String name = ctx.popFirstArg();
            return getRoleRegistry().value(name);
        });

        commandContexts.registerContext(Clan.class, ctx -> clanManager.getClan(ctx.popFirstArg()));


    }
    private void registerCompletions() {
        commandManager.getCommandCompletions().registerCompletion("roles", ctx -> roleRegistry.keys());

        commandManager.getCommandCompletions().registerCompletion("clans",
                ctx -> clanManager.getClans().stream().map(Clan::getTag).collect(Collectors.toList()));

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
        commandManager.unregisterCommands();
        getLogger().info("Saving clans");
        storage.shutdown();

        getLogger().info("Saved successfully! Goodbye and cya! <3");
    }
    public Index<String, ClanRole> getRoleRegistry() {
        return roleRegistry;
    }
    public Jdbi getJdbi() {
        return storage.getJdbi();
    }


    public ClansConfig getClansConfig() {
        return configManager.getConfigData();
    }
    public MessagesConfig getMessages() {
        return messagesConfigManager.getConfigData();
    }

    public ClanManager getClanManager() {
        return clanManager;
    }

    public ClanRole getOwnerRole() {
        return ownerRole;
    }

    public ClanRole getDefaultRole() {
        return defaultRole;
    }

    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    public Index<String, StatisticType> getStatisticTypeRegistry() {
        return statisticTypeRegistry;
    }

    public Storage getStorage() {
        return storage;
    }
}
