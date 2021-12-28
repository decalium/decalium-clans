package org.gepron1x.clans;

import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.ClanManager;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.member.ClanPermission;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.async.BukkitFuturesFactory;
import org.gepron1x.clans.async.FuturesFactory;
import org.gepron1x.clans.command.ClanCommand;
import org.gepron1x.clans.listener.CacheListener;
import org.gepron1x.clans.storage.ClanStorage;
import org.gepron1x.clans.storage.implementation.sql.SqlClanStorage;
import org.gepron1x.clans.storage.implementation.sql.argument.Arguments;
import org.gepron1x.clans.storage.implementation.sql.mappers.column.ClanRoleMapper;
import org.gepron1x.clans.storage.implementation.sql.mappers.column.ColumnMappers;
import org.gepron1x.clans.util.Message;
import org.jdbi.v3.core.Jdbi;

import java.text.MessageFormat;
import java.util.Set;
import java.util.function.UnaryOperator;

public final class DecaliumClansPlugin extends JavaPlugin {

    private FuturesFactory futuresFactory;
    private RoleRegistry roleRegistry;
    private ClanBuilderFactory builderFactory;
    private ClanCacheImpl clanCache;
    private ClanManager clanManager;
    private DecaliumClansApi clansApi;


    @Override
    public void onEnable() {

        this.futuresFactory = new BukkitFuturesFactory(this);
        this.builderFactory = new ClanBuilderFactoryImpl();
        ClanRole defaultRole = builderFactory.roleBuilder().name("default").displayName(Component.text("Обычная роль")).weight(1).build();
        ClanRole ownerRole = builderFactory.roleBuilder().name("owner").displayName(Component.text("Владелец")).weight(10).permissions(ClanPermission.all()).build();
        this.roleRegistry = new RoleRegistryImpl(defaultRole, ownerRole,
                Set.of(defaultRole, ownerRole));
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

        ClanCommand command = new ClanCommand(builderFactory, roleRegistry, clanManager, null);

        getServer().getPluginManager().registerEvents(new CacheListener(clanCache, getServer(), storage), this);





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
        commandManager.registerBrigadier();

        command.register(commandManager);
    }

    public static void test(DecaliumClansApi api) {
        Message message = Message.message("value");




    }



    @Override
    public void onDisable() {

    }
}
