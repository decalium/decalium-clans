package org.gepron1x.clans.plugin;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import io.leangen.geantyref.TypeToken;
import io.papermc.paper.text.PaperComponents;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.*;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.plugin.async.BukkitFactoryOfTheFuture;
import org.gepron1x.clans.plugin.command.ClanCommand;
import org.gepron1x.clans.plugin.command.InviteCommand;
import org.gepron1x.clans.plugin.command.MemberCommand;
import org.gepron1x.clans.plugin.command.parser.ClanRoleParser;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.ConfigManager;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.config.serializer.ClanPermissionSerializer;
import org.gepron1x.clans.plugin.config.serializer.ClanRoleSerializer;
import org.gepron1x.clans.plugin.config.serializer.KyoriComponentSerializer;
import org.gepron1x.clans.plugin.config.serializer.MessageSerializer;
import org.gepron1x.clans.plugin.listener.CacheListener;
import org.gepron1x.clans.plugin.papi.ClansExpansion;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.gepron1x.clans.plugin.storage.StorageCreation;
import org.gepron1x.clans.plugin.util.Message;
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
    private ClanManager clanManager;
    private DecaliumClansApi clansApi;

    private ConfigManager<ClansConfig> configManager;
    private ConfigManager<MessagesConfig> messagesConfigManager;


    @Override
    public void onEnable() {

        this.futuresFactory = new BukkitFactoryOfTheFuture(this);
        this.builderFactory = new ClanBuilderFactoryImpl();

        MiniMessage miniMessage = MiniMessage.builder().templateResolver(TemplateResolver.dynamic(s -> switch(s) {
            case "prefix" -> getMessages().prefix();
            default -> null;
        })).build();

        ConfigurationOptions options = new ConfigurationOptions.Builder()
                .addSerialiser(new MessageSerializer(miniMessage))
                .addSerialiser(new KyoriComponentSerializer(miniMessage))
                .addSerialiser(new ClanRoleSerializer(builderFactory))
                .addSerialiser(new ClanPermissionSerializer())
                .build();
        this.messagesConfigManager = ConfigManager.create(this, "messages.yml", MessagesConfig.class, options);
        this.configManager = ConfigManager.create(this, "config.yml", ClansConfig.class, options);

        this.messagesConfigManager.reloadConfig();
        this.configManager.reloadConfig();

        buildRoleRegistry();


        ClanStorage storage = new StorageCreation(this, getClansConfig(), builderFactory, roleRegistry).create();
        storage.initialize();

        this.clanManager = new ClanManagerImpl(storage, futuresFactory, getServer().getPluginManager());
        this.clanCache = new ClanCacheImpl();


        CachingClanManager cachingClanManager = new CachingClanManagerImpl(clanManager, futuresFactory, clanCache);






        ClanCommand command = new ClanCommand(builderFactory, roleRegistry, cachingClanManager, getClansConfig(), getMessages());
        InviteCommand inviteCommand = new InviteCommand(futuresFactory, cachingClanManager, builderFactory, roleRegistry, getClansConfig(), getMessages());
        MemberCommand memberCommand = new MemberCommand(cachingClanManager, roleRegistry, futuresFactory);

        getServer().getPluginManager().registerEvents(new CacheListener(clanCache, getServer(), storage), this);

        PlaceholderAPI.registerExpansion(new ClansExpansion(getServer(), clanCache, PaperComponents.legacySectionSerializer()));


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
    }

    public static void test(DecaliumClansApi api) {
        Message message = Message.message("value");




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
