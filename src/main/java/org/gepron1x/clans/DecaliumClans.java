package org.gepron1x.clans;

import co.aikar.commands.PaperCommandManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.gepron1x.clans.clan.ClanBuilder;
import org.gepron1x.clans.clan.home.ClanHome;
import org.gepron1x.clans.clan.member.ClanMember;
import org.gepron1x.clans.config.serializer.*;
import org.gepron1x.clans.hook.ClanPlaceholderExpansion;
import org.gepron1x.clans.command.InviteCommand;
import org.gepron1x.clans.manager.ClanManager;
import org.gepron1x.clans.statistic.StatisticType;
import org.gepron1x.clans.storage.ClanLoader;
import org.gepron1x.clans.storage.Mappers;
import org.gepron1x.clans.storage.StorageType;
import org.gepron1x.clans.storage.UpdateListener;
import org.gepron1x.clans.storage.argument.ItemStackArgumentFactory;
import org.gepron1x.clans.storage.argument.LocationArgumentFactory;
import org.gepron1x.clans.storage.mappers.row.*;
import org.gepron1x.clans.storage.argument.ComponentArgumentFactory;
import org.gepron1x.clans.storage.task.DataSyncTask;
import org.gepron1x.clans.util.Functions;
import org.gepron1x.clans.util.Tasks;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.member.role.ClanRole;
import org.gepron1x.clans.command.ClanCommand;
import org.gepron1x.clans.config.ClansConfig;
import org.gepron1x.clans.config.ConfigManager;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.storage.argument.UuidArgumentFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.Index;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import space.arim.dazzleconf.ConfigurationOptions;

import java.io.File;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class DecaliumClans extends JavaPlugin {
    public static final String AUTHOR = "gepron1x";
    public static final String NAME = "decaliumclans";
    public static final String VERSION = "1.0.0";


    private Index<String, ClanRole> roleRegistry;
    private Index<String, StatisticType> statisticTypeRegistry = Index.create(StatisticType::getName,
            StatisticType.KILLS,
            StatisticType.DEATHS,
            StatisticType.CLAN_WAR_LOSES,
            StatisticType.CLAN_WAR_WINS);

    private ClanRole defaultRole, ownerRole;
    private ConfigManager<MessagesConfig> messagesConfigManager;
    private Jdbi jdbi;
    private ClanManager clanManager;
    private final Path dataFolder = getDataFolder().toPath();
    private ConfigManager<ClansConfig> configManager;
    private ClanLoader loader;
    private MiniMessage miniMessage;
    private PaperCommandManager commandManager;
    private ClanCommand command;
    private InviteCommand inviteCommand;
    private UpdateListener updateListener;
    private DataSyncTask dataSyncTask;

    @Override
    public void onEnable() {
        this.loader = new ClanLoader(this);
        this.clanManager = new ClanManager();
        this.updateListener = new UpdateListener();
        this.miniMessage = MiniMessage.builder().placeholderResolver(s ->
        {
            if(s.equals("prefix")) return getPrefix();
            else return null;
        }).build();

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

        ClansConfig cfg = configManager.getConfigData();


        roleRegistry = Index.create(ClanRole::getName, cfg.roles());


        commandManager = new PaperCommandManager(this);
        command = new ClanCommand(this, clanManager, messagesConfigManager.getConfigData());
        inviteCommand = new InviteCommand(this, clanManager, messagesConfigManager.getConfigData());
        commandManager.registerCommand(command);
        commandManager.registerCommand(inviteCommand);


        defaultRole = roleRegistry.value(cfg.defaultRole());
        ownerRole = roleRegistry.value(cfg.ownerRole());

        Executor mainThreadExecutor = getServer().getScheduler().getMainThreadExecutor(this);
        setupStorage(cfg)
                // set jdbi instance
                .thenAcceptAsync(jdbi -> this.jdbi = jdbi, mainThreadExecutor)
                // load clans lol
                .thenApplyAsync(Functions.supplier(() -> {

                    getLogger().info("Loading clans...");
                    List<Clan> clans = loader.load(jdbi);
                    getLogger().info("Successfully loaded clans!");
                    return clans;
                }))
                // apply loaded clans
                .thenAcceptAsync(clans -> {

            clans.forEach(clan -> clanManager.insertClan(clan));
            new ClanPlaceholderExpansion(clanManager).register();
            this.dataSyncTask = new DataSyncTask(this, updateListener);
            getServer().getPluginManager().registerEvents(updateListener, this);
            long syncPeriod = Tasks.asTicks(cfg.storage().saveTaskPeriod());
            this.dataSyncTask.runTaskTimerAsynchronously(this, syncPeriod, syncPeriod);
            getLogger().info("Yay! Plugin successfully enabled, clans are loaded.");

        }, mainThreadExecutor).exceptionally(Tasks.defaultExceptionally());

    }
    public ComponentLike getPrefix() {
        return messagesConfigManager.getConfigData().prefix();
    }

    private CompletableFuture<Jdbi> setupStorage(ClansConfig cfg) {
        return CompletableFuture.supplyAsync(() -> {
            getLogger().info("connecting to mysql");
            HikariConfig hikariConfig = new HikariConfig();

            ClansConfig.Storage sqlCfg = cfg.storage();
            String url, password, user;
            if(sqlCfg.storageType() == StorageType.MYSQL) {
                ClansConfig.Storage.AuthDetails details = sqlCfg.authDetails();
                url = MessageFormat.format("jdbc:mysql://{0}/{1}?useSSL=false", details.host(), details.database());
                password = details.password();
                user = details.user();
            } else {
                File file = new File(getDataFolder(), "clans");
                hikariConfig.setDriverClassName(org.h2.Driver.class.getName());
                url = "jdbc:h2:file:." + File.separator + file.toPath() + ";mode=MySQL";
                password = "";
                user = "sa";
            }
            hikariConfig.setJdbcUrl(url);
            hikariConfig.setPassword(password);
            hikariConfig.setUsername(user);
            hikariConfig.setPoolName("ClansPool");
            hikariConfig.setMaximumPoolSize(6);
            hikariConfig.setMinimumIdle(10);
            hikariConfig.setMaxLifetime(1800000);
            hikariConfig.setConnectionTimeout(5000);
            hikariConfig.setInitializationFailTimeout(-1);

            Jdbi jdbi = Jdbi.create(new HikariDataSource(hikariConfig));
            jdbi.installPlugin(new SqlObjectPlugin());
            jdbi.registerRowMapper(ClanBuilder.class, new ClanMapper(this))
                    .registerRowMapper(ClanMember.class, new ClanMemberMapper(getRoleRegistry()))
                    .registerRowMapper(StatisticRow.class, new StatisticRowMapper(getStatisticTypeRegistry()))
                    .registerRowMapper(ClanHome.class, new ClanHomeMapper());


            jdbi.registerColumnMapper(UUID.class, Mappers.UUID)
                    .registerColumnMapper(Component.class, Mappers.COMPONENT)
                    .registerColumnMapper(ItemStack.class, Mappers.ITEM_STACK)
                    .registerColumnMapper(Location.class, Mappers.LOCATION);

            jdbi.registerArgument(new UuidArgumentFactory())
                    .registerArgument(new ComponentArgumentFactory())
                    .registerArgument(new ItemStackArgumentFactory())
                    .registerArgument(new LocationArgumentFactory());

            return jdbi;

        });
    }
    public void reload() {
        configManager.reloadConfig();
        ClansConfig config = configManager.getConfigData();
        dataSyncTask.cancel();


        messagesConfigManager.reloadConfig();
        command.setMessages(messagesConfigManager.getConfigData());
        inviteCommand.setMessages(messagesConfigManager.getConfigData());

    }

    private void registerContexts() {
        commandManager.getCommandContexts().registerContext(ClanRole.class, ctx -> {
            String name = ctx.popFirstArg();
            return getRoleRegistry().value(name);
        });
    }


    @Override
    public void onDisable() {
        commandManager.unregisterCommands();
        HandlerList.unregisterAll(updateListener);
        getLogger().info("Cancelling update task..");
        dataSyncTask.cancel();
        getLogger().info("Success.");
        getLogger().info("Saving clans");
        dataSyncTask.run();
        getLogger().info("Saved successfully! Goodbye and cya! <3");
    }
    public Index<String, ClanRole> getRoleRegistry() {
        return roleRegistry;
    }
    public Jdbi getJdbi() {
        return jdbi;
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

}
