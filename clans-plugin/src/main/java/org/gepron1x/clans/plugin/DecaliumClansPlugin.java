package org.gepron1x.clans.plugin;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import com.sk89q.worldguard.WorldGuard;
import io.leangen.geantyref.TypeToken;
import io.papermc.paper.text.PaperComponents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.plugin.announce.AnnouncingClanRepository;
import org.gepron1x.clans.plugin.async.BukkitFactoryOfTheFuture;
import org.gepron1x.clans.plugin.cache.CachingClanRepositoryImpl;
import org.gepron1x.clans.plugin.cache.ClanCacheImpl;
import org.gepron1x.clans.plugin.chat.CarbonChatHook;
import org.gepron1x.clans.plugin.command.ClanCommand;
import org.gepron1x.clans.plugin.command.HomeCommand;
import org.gepron1x.clans.plugin.command.InviteCommand;
import org.gepron1x.clans.plugin.command.MemberCommand;
import org.gepron1x.clans.plugin.command.parser.ClanRoleParser;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.ConfigManager;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.config.serializer.AdventureComponentSerializer;
import org.gepron1x.clans.plugin.config.serializer.ClanPermissionSerializer;
import org.gepron1x.clans.plugin.config.serializer.ClanRoleSerializer;
import org.gepron1x.clans.plugin.config.serializer.MessageSerializer;
import org.gepron1x.clans.plugin.listener.CacheListener;
import org.gepron1x.clans.plugin.papi.PlaceholderAPIHook;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.gepron1x.clans.plugin.storage.StorageCreation;
import org.gepron1x.clans.plugin.util.AsciiArt;
import org.gepron1x.clans.plugin.wg.WgRepositoryImpl;
import org.slf4j.Logger;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;


public final class DecaliumClansPlugin extends JavaPlugin {

    private FactoryOfTheFuture futuresFactory;
    private RoleRegistry roleRegistry;
    private ClanBuilderFactory builderFactory;
    private ClanCacheImpl clanCache;
    private CachingClanRepository clanRepository;
    private DecaliumClansApi clansApi;

    private ConfigManager<ClansConfig> configManager;
    private ConfigManager<MessagesConfig> messagesConfigManager;


    @Override
    public void onEnable() {

        this.futuresFactory = new BukkitFactoryOfTheFuture(this);
        this.builderFactory = new ClanBuilderFactoryImpl();

        MiniMessage miniMessage = MiniMessage.builder().tags(TagResolver.resolver((TagResolver.WithoutArguments) (text) -> switch(text) {
            case "prefix" -> Tag.selfClosingInserting(getMessages().prefix());
            default -> null;
        }, TagResolver.standard())).build();

        ConfigurationOptions options = new ConfigurationOptions.Builder()
                .addSerialiser(new MessageSerializer(miniMessage))
                .addSerialiser(new AdventureComponentSerializer(miniMessage))
                .addSerialiser(new ClanRoleSerializer(builderFactory))
                .addSerialiser(new ClanPermissionSerializer())
                .build();
        this.messagesConfigManager = ConfigManager.create(this, "messages.yml", MessagesConfig.class, options);
        this.configManager = ConfigManager.create(this, "config.yml", ClansConfig.class, options);

        this.messagesConfigManager.reloadConfig();
        this.configManager.reloadConfig();

        buildRoleRegistry();
        ClansConfig config = getClansConfig();
        MessagesConfig messages = getMessages();



        ClanStorage storage = new StorageCreation(this, getClansConfig(), builderFactory, roleRegistry).create();
        storage.initialize();
        this.clanCache = new ClanCacheImpl();


        ClanRepository repository = new AnnouncingClanRepository(
                new WgRepositoryImpl(
                        new ClanRepositoryImpl(storage, futuresFactory),
                        WorldGuard.getInstance(),
                        getServer()
                ),
                getServer(),
                messages);

        this.clanRepository = new CachingClanRepositoryImpl(
                repository,
                futuresFactory,
                clanCache
        );

        getServer().getPluginManager().registerEvents(new CacheListener(clanCache, getServer(), repository), this);



        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderAPIHook(getServer(), config, clanCache, PaperComponents.legacySectionSerializer()).register();
        }

        if(getServer().getPluginManager().getPlugin("CarbonChat") != null) {
            new CarbonChatHook(getServer(), clanCache, messages, config).register();
        }


        Logger logger = getSLF4JLogger();

        ClanCommand command = new ClanCommand(logger, this.clanRepository, config, messages, futuresFactory, builderFactory, roleRegistry);
        InviteCommand inviteCommand = new InviteCommand(logger, this.clanRepository, config, messages, futuresFactory, builderFactory, roleRegistry);
        MemberCommand memberCommand = new MemberCommand(logger, this.clanRepository, config, messages, futuresFactory);
        HomeCommand homeCommand = new HomeCommand(logger, this.clanRepository, config, messages, futuresFactory, builderFactory);



        PaperCommandManager<CommandSender> commandManager;
        try {
             commandManager = new PaperCommandManager<>(
                    this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    UnaryOperator.identity(), UnaryOperator.identity());
        } catch (Exception e) {
            getLogger().severe("error caused initializing command manager!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        commandManager.getParserRegistry().registerParserSupplier(TypeToken.get(ClanRole.class), params -> new ClanRoleParser<>(roleRegistry));
        commandManager.registerBrigadier();


        command.register(commandManager);
        inviteCommand.register(commandManager);
        memberCommand.register(commandManager);
        homeCommand.register(commandManager);


        new AsciiArt(logger).print();
    }


    private void buildRoleRegistry() {
        ClanRole ownerRole = getClansConfig().roles().ownerRole();
        ClanRole defaultRole = getClansConfig().roles().defaultRole();
        List<ClanRole> otherRoles = getClansConfig().roles().otherRoles();

        ArrayList<ClanRole> roles = new ArrayList<>(otherRoles.size() + 2);
        roles.add(ownerRole);
        roles.add(defaultRole);
        roles.addAll(otherRoles);

        this.roleRegistry = new RoleRegistryImpl(defaultRole, ownerRole, roles);


    }

    private void registerCommands() {

    }





    @Override
    public void onDisable() {

    }

    public ClansConfig getClansConfig() {
        return configManager.getConfigData();
    }

    public MessagesConfig getMessages() {
        return messagesConfigManager.getConfigData();
    }

}
