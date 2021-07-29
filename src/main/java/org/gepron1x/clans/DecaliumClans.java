package org.gepron1x.clans;

import co.aikar.commands.PaperCommandManager;
import org.gepron1x.clans.config.serializer.ClanRoleSerializer;
import org.gepron1x.clans.helper.ClanHelper;
import org.gepron1x.clans.hook.ClanPlaceholderExpansion;
import org.gepron1x.clans.command.InviteCommand;
import org.gepron1x.clans.statistic.StatisticType;
import org.gepron1x.clans.storage.ClanLoader;
import org.gepron1x.clans.storage.converters.StatisticRowMapper;
import org.gepron1x.clans.storage.converters.component.ComponentArgumentFactory;
import org.gepron1x.clans.storage.converters.component.ComponentMapper;
import org.gepron1x.clans.storage.converters.uuid.UuidMapper;
import org.gepron1x.clans.util.TaskScheduler;
import org.gepron1x.clans.util.Tasks;
import org.gepron1x.clans.clan.Clan;
import org.gepron1x.clans.clan.role.ClanRole;
import org.gepron1x.clans.command.ClanCommand;
import org.gepron1x.clans.config.ClansConfig;
import org.gepron1x.clans.config.ConfigManager;
import org.gepron1x.clans.config.MessagesConfig;
import org.gepron1x.clans.config.serializer.ClanPermissionSerializer;
import org.gepron1x.clans.config.serializer.ComponentSerializer;
import org.gepron1x.clans.storage.converters.ClanMemberMapper;
import org.gepron1x.clans.storage.converters.uuid.UuidArgumentFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.util.Index;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.storage.ClanMapper;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import space.arim.dazzleconf.ConfigurationOptions;

import java.nio.file.Path;
import java.util.List;

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
    private ClanHelper clanHelper;
    private final Path dataFolder = getDataFolder().toPath();
    private ConfigManager<ClansConfig> configManager;
    private final ClanLoader loader = new ClanLoader();
    private MiniMessage miniMessage;
    private PaperCommandManager commandManager;
    private ClanCommand command;
    private InviteCommand inviteCommand;

    @Override
    public void onEnable() {
        instance = this;
        this.commandManager = new PaperCommandManager(this);
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
        {
            if(s.equals("prefix")) return prefix;
            else return null;
        }).build();
        roleRegistry = Index.create(ClanRole::getName, cfg.roles());
        command = new ClanCommand(this, messagesConfigManager.getConfigData());
        inviteCommand = new InviteCommand(this, messagesConfigManager.getConfigData());
        this.commandManager.registerCommand(command);
        commandManager.registerCommand(inviteCommand);
        defaultRole = roleRegistry.value(cfg.defaultRole());
        ownerRole = roleRegistry.value(cfg.ownerRole());

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
            List<Clan> clans = loader.load(jdbi);
            Tasks.sync(this, t -> {
                this.jdbi = jdbi;
                this.clanHelper = new ClanHelper(scheduler, jdbi);
                clans.forEach(clan -> clanHelper.addClan(clan));
                new ClanPlaceholderExpansion(clanHelper).register();
            }
            );
        });
    }


    @Override
    public void onDisable() {

    }
    public Index<String, ClanRole> getRoleRegistry() {
        return roleRegistry;
    }
    public Jdbi getJdbi() {
        return jdbi;
    }

    public TaskScheduler getScheduler() {
        return scheduler;
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

    public ClanHelper getClanHelper() {
        return clanHelper;
    }
}
