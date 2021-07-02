package com.manya.clans;

import co.aikar.commands.PaperCommandManager;
import com.manya.clans.config.serializer.ClanRoleSerializer;
import com.manya.clans.hook.ClanPlaceholderExpansion;
import com.manya.clans.statistic.StatisticType;
import com.manya.clans.storage.ClanMapper;
import com.manya.clans.storage.StatisticDao;
import com.manya.clans.storage.converters.StatisticRowMapper;
import com.manya.clans.storage.converters.component.ComponentArgumentFactory;
import com.manya.clans.storage.converters.component.ComponentMapper;
import com.manya.clans.storage.converters.uuid.UuidMapper;
import com.manya.clans.util.TaskScheduler;
import com.manya.clans.util.Tasks;
import com.manya.clans.clan.Clan;
import com.manya.clans.clan.member.ClanMember;
import com.manya.clans.clan.role.ClanRole;
import com.manya.clans.command.ClanCommand;
import com.manya.clans.config.ClansConfig;
import com.manya.clans.config.ConfigManager;
import com.manya.clans.config.MessagesConfig;
import com.manya.clans.config.serializer.ClanPermissionSerializer;
import com.manya.clans.config.serializer.ComponentSerializer;
import com.manya.clans.manager.ClanManager;
import com.manya.clans.storage.ClanDao;
import com.manya.clans.storage.ClanMemberDao;
import com.manya.clans.storage.converters.ClanMemberMapper;
import com.manya.clans.storage.converters.uuid.UuidArgumentFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.Index;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import space.arim.dazzleconf.ConfigurationOptions;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

public final class DecaliumClans extends JavaPlugin {
    public static final String AUTHOR = "gepron1x";
    public static final String VERSION = "1.0.0";


    private static DecaliumClans instance;
    private TaskScheduler scheduler;
    private Index<String, ClanRole> roleRegistry;
    private Index<String, StatisticType> statisticTypeRegistry = Index.create(StatisticType::getName,
            StatisticType.KILLS,
            StatisticType.DEATHS,
            StatisticType.CLAN_WAR_LOSES,
            StatisticType.CLAN_WAR_WINS);

    private ClanRole defaultRole, ownerRole;
    private ConfigManager<MessagesConfig> messagesConfigManager;
    private Jdbi jdbi;
    private final Path dataFolder = getDataFolder().toPath();
    private ConfigManager<ClansConfig> configManager;
    private ClanManager clanManager;
    private MiniMessage miniMessage;
    private PaperCommandManager commandManager;
    private ClanCommand command;

    @Override
    public void onEnable() {
        instance = this;
        scheduler = new TaskScheduler(this);
        ConfigurationOptions defaultOptions = new ConfigurationOptions.Builder()
                .addSerialisers(ComponentSerializer.INSTANCE,
                ClanRoleSerializer.INSTANCE,
                ClanPermissionSerializer.INSTANCE).build();



        saveResource("messages.yml", false);
        saveDefaultConfig();

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
        Component prefix = MiniMessage.get().parse(messagesConfigManager.getConfigData().prefix());
        this.miniMessage = MiniMessage.builder().placeholderResolver(s ->
                switch(s) {
            case "prefix" -> prefix;
            default -> null;
        }).build();
        roleRegistry = Index.create(ClanRole::getName, cfg.roles());

        defaultRole = roleRegistry.value(cfg.defaultRole());
        ownerRole = roleRegistry.value(cfg.ownerRole());
        this.clanManager = new ClanManager(this);
        scheduler.async(task -> {
            ClansConfig.SqlConfig sqlCfg = cfg.mysql();
            Jdbi jdbi = Jdbi.create("jdbc:mysql://" + sqlCfg.host() + "/" + sqlCfg.database(),
                    sqlCfg.user(),
                    sqlCfg.password());
            jdbi.installPlugin(new SqlObjectPlugin());
            jdbi.registerRowMapper(new ClanMapper())
                    .registerRowMapper(new ClanMemberMapper(getRoleRegistry()))
                    .registerRowMapper(new StatisticRowMapper(getStatisticTypeRegistry()));

            jdbi.registerColumnMapper(new UuidMapper()).registerColumnMapper(new ComponentMapper());
            jdbi.registerArgument(new UuidArgumentFactory()).registerArgument(new ComponentArgumentFactory());
            Map<String, Clan> clans = jdbi.withExtension(ClanDao.class, dao -> {
                dao.createTable();
                return dao.getClans().stream()
                        .peek(clan ->
                                clan.getMemberList().addMember(
                                        new ClanMember(Bukkit.getOfflinePlayer(clan.getCreator()), getOwnerRole())
                                )
                        )
                        .collect(Collectors.toMap(Clan::getTag, c -> c));
            });
            // loading members
            jdbi.withExtension(ClanMemberDao.class, dao -> {
                dao.createTable();
                return dao.loadMembers();
            }).forEach((key, value) -> {
                Clan clan = clans.get(key);
                if(clan == null) return;
                clan.getMemberList().addMember(value);
            });
            // loading statistics
            jdbi.withExtension(StatisticDao.class, dao -> {
                dao.createTable();
                return dao.getStats();
            }).forEach(result -> {
                Clan clan = clans.get(result.clanTag());
                if(clan != null) clan.getStatistics().setValue(result.statType(), result.value());
            });
            Tasks.sync(this, t -> {
                this.jdbi = jdbi;
                clans.forEach((tag, clan) -> clanManager.addClan(clan, true));
            }
            );
        });


        this.commandManager = new PaperCommandManager(this);
        command = new ClanCommand(this, clanManager, messagesConfigManager.getConfigData());
        commandManager.registerCommand(command);
        new ClanPlaceholderExpansion(clanManager).register();
    }


    @Override
    public void onDisable() {

    }
    public Index<String, ClanRole> getRoleRegistry() {
        return Index.create(ClanRole::getName, ClanRole.USER, ClanRole.OWNER);
    }
    public Jdbi getJdbi() {
        return jdbi;
    }

    public TaskScheduler getScheduler() {
        return scheduler;
    }

    // this is bad, right?
    public static DecaliumClans instance() {
        return instance;
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
