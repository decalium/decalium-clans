package org.gepron1x.clans;

import cloud.commandframework.CommandManager;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import com.sk89q.worldguard.WorldGuard;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.Index;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.clan.member.role.ClanRole;
import org.gepron1x.clans.command.ClanCommand;
import org.gepron1x.clans.command.InviteCommand;
import org.gepron1x.clans.command.MemberCommand;
import org.gepron1x.clans.command.postprocessors.ClanMemberCommandPostprocessor;
import org.gepron1x.clans.command.postprocessors.ClanPermissionPostprocessor;
import org.gepron1x.clans.config.ClansConfig;
import org.gepron1x.clans.config.ConfigManager;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.config.serializer.*;
import org.gepron1x.clans.statistic.StatisticRegistry;
import org.gepron1x.clans.statistic.StatisticType;
import org.gepron1x.clans.storage.*;

import org.gepron1x.clans.util.registry.ClanRoleRegistry;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;
import space.arim.dazzleconf.ConfigurationOptions;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public final class DecaliumClans extends JavaPlugin {


    public static final String AUTHOR = "gepron1x";
    public static final String NAME = "decaliumclans";
    public static final String VERSION = "1.0.0";


    private ClanRoleRegistry roleRegistry;
    private StatisticRegistry statisticRegistry;

    private ConfigManager<MessagesConfig> messagesConfigManager;
    private ClanManager clanManager;
    private WorldGuard worldGuard;
    private final Path dataFolder = getDataFolder().toPath();
    private ConfigManager<ClansConfig> configManager;
    private StorageService storageService;
    private MiniMessage miniMessage;
    private CommandManager<CommandSender> commandManager;

    private ClanCommand command;
    private InviteCommand inviteCommand;
    private MemberCommand memberCommand;


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
        this.statisticRegistry = StatisticRegistry.create(StatisticType.KILLS,
                StatisticType.DEATHS,
                StatisticType.CLAN_WAR_LOSES,
                StatisticType.CLAN_WAR_WINS);

        this.storageService = new StorageCreator(this, getClansConfig().storage(), roleRegistry, statisticRegistry).create();
        getLogger().info("Storage initialized. Loading clans...");
        this.clanManager = new ClanManager(storageService.loadClans());
        getLogger().info("Clans loaded.");
        storageService.start();
        getLogger().info("Scheduled save task.");
        getClansConfig().statisticTypes().forEach(
                (key, value) ->
                Objects.requireNonNull(statisticRegistry.get(key), "no statistic type with " + key + "name!")
                .setDisplayName(value.asComponent())
        );

        setupCommands();
        worldGuard = WorldGuard.getInstance();


    }
    private void disable() {
        HandlerList.unregisterAll(this);
        getLogger().info("Saving clans");
        storageService.shutdown();
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

    private void registerPreprocessors() {
        commandManager.registerCommandPostProcessor(new ClanMemberCommandPostprocessor(clanManager, getMessages()));
        commandManager.registerCommandPostProcessor(new ClanPermissionPostprocessor(clanManager, getMessages()));
    }

    private void setupCommands() {
        try {
            commandManager = new BukkitCommandManager<>(
                    this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity()
            );
        } catch (Exception e) {
            getLogger().severe("Some error happened while creating commandManager! Please contact developers");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        registerPreprocessors();
        command = new ClanCommand(this, clanManager, getMessages(), roleRegistry);
        inviteCommand = new InviteCommand(this, clanManager, getMessages(), roleRegistry);
        memberCommand = new MemberCommand(clanManager, getMessages());
        Arrays.asList(command, inviteCommand, memberCommand).forEach(cmd -> cmd.register(commandManager));

    }

    private void setupConfigurations() {
        ConfigurationOptions defaultOptions = new ConfigurationOptions.Builder()
                .addSerialisers(
                        new ComponentSerializer(),
                        new MessageSerializer(miniMessage),
                        new ClanRoleSerializer(),
                        new ClanPermissionSerializer(),
                        new DurationSerializer(),
                        new ClanRolesSerializer()
                ).build();

        configManager = ConfigManager.create(
                dataFolder,
                "config.yml",
                ClansConfig.class,
                defaultOptions);
        configManager.reloadConfig();

        messagesConfigManager = ConfigManager.create(
                dataFolder,
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
    public MiniMessage getMiniMessage() {
        return miniMessage;
    }

    @NotNull
    public StorageService getStorage() {
        return storageService;
    }
}
