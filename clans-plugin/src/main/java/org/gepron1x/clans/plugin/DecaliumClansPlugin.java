package org.gepron1x.clans.plugin;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import io.leangen.geantyref.TypeToken;
import io.papermc.paper.text.PaperComponents;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.template.TemplateResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.ClanManager;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.member.ClanPermission;
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
import org.gepron1x.clans.plugin.storage.implementation.sql.SqlClanStorage;
import org.gepron1x.clans.plugin.storage.implementation.sql.argument.Arguments;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.column.ClanRoleMapper;
import org.gepron1x.clans.plugin.storage.implementation.sql.mappers.column.ColumnMappers;
import org.gepron1x.clans.plugin.util.Message;
import org.jdbi.v3.core.Jdbi;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.text.MessageFormat;
import java.util.Set;
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
        ClanRole defaultRole = builderFactory.roleBuilder().name("default").displayName(Component.text("Участник", NamedTextColor.GRAY)).weight(1).build();
        ClanRole ownerRole = builderFactory.roleBuilder().name("owner").displayName(Component.text("Владелец", NamedTextColor.RED)).weight(10).permissions(ClanPermission.all()).build();
        ClanRole moderatorRole = builderFactory.roleBuilder().name("moderator").displayName(Component.text("Модератор", NamedTextColor.AQUA)).weight(5).permissions(ClanPermission.ADD_HOME, ClanPermission.KICK, ClanPermission.INVITE).build();
        this.roleRegistry = new RoleRegistryImpl(defaultRole, ownerRole,
                Set.of(defaultRole, ownerRole, moderatorRole));
        String url = MessageFormat.format("jdbc:mysql://{0}/{1}?useSSL={2}", "143.47.226.239:3306", "s2_clans", false);
        Jdbi jdbi = Jdbi.create(url, "u2_AlPEbu2j58", "I+!jIfqfkM.K@0FYBY4WsEWI")
                .registerArgument(Arguments.COMPONENT).registerArgument(Arguments.UUID).registerArgument(Arguments.ITEM_STACK)
                .registerArgument(Arguments.CLAN_ROLE).registerArgument(Arguments.STATISTIC_TYPE)
                .registerColumnMapper(ColumnMappers.COMPONENT).registerColumnMapper(ColumnMappers.ITEM_STACK).registerColumnMapper(ColumnMappers.UUID)
                .registerColumnMapper(ColumnMappers.STATISTIC_TYPE).registerColumnMapper(new ClanRoleMapper(this.roleRegistry));
        ClanStorage storage = new SqlClanStorage(this, jdbi, builderFactory, roleRegistry);
        storage.initialize();
        this.clanManager = new ClanManagerImpl(storage, futuresFactory, getServer().getPluginManager());
        this.clanCache = new ClanCacheImpl();

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

        this.configManager = ConfigManager.create(this, "config.yml", ClansConfig.class, options);
        this.messagesConfigManager = ConfigManager.create(this, "messages.yml", MessagesConfig.class, options);





        ClanCommand command = new ClanCommand(builderFactory, roleRegistry, clanManager, getClansConfig(), getMessages());
        InviteCommand inviteCommand = new InviteCommand(futuresFactory, clanManager, builderFactory, roleRegistry, getClansConfig(), getMessages());
        MemberCommand memberCommand = new MemberCommand(clanManager, roleRegistry, futuresFactory);

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
