/*
 * decalium-clans
 * Copyright © 2023 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.plugin;

import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import cloud.commandframework.paper.PaperCommandManager;
import com.sk89q.worldguard.WorldGuard;
import com.zaxxer.hikari.HikariDataSource;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.region.effect.RegionEffect;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.api.statistic.StatisticType;
import org.gepron1x.clans.api.user.Users;
import org.gepron1x.clans.api.war.Wars;
import org.gepron1x.clans.plugin.announce.AnnouncingClanRepository;
import org.gepron1x.clans.plugin.async.BukkitFactoryOfTheFuture;
import org.gepron1x.clans.plugin.bootstrap.WarsCreation;
import org.gepron1x.clans.plugin.cache.CachingClanRepositoryImpl;
import org.gepron1x.clans.plugin.cache.ClanCache;
import org.gepron1x.clans.plugin.cache.UserCaching;
import org.gepron1x.clans.plugin.chat.carbon.CarbonChatHook;
import org.gepron1x.clans.plugin.command.*;
import org.gepron1x.clans.plugin.command.war.ClanWarCommand;
import org.gepron1x.clans.plugin.config.Configs;
import org.gepron1x.clans.plugin.config.Configuration;
import org.gepron1x.clans.plugin.config.messages.MessagesConfig;
import org.gepron1x.clans.plugin.config.serializer.*;
import org.gepron1x.clans.plugin.config.settings.ClansConfig;
import org.gepron1x.clans.plugin.config.settings.PricesConfig;
import org.gepron1x.clans.plugin.economy.VaultHook;
import org.gepron1x.clans.plugin.level.LeveledClanRepository;
import org.gepron1x.clans.plugin.listener.CacheListener;
import org.gepron1x.clans.plugin.listener.HologramProtection;
import org.gepron1x.clans.plugin.listener.StatisticListener;
import org.gepron1x.clans.plugin.papi.ClanTopCache;
import org.gepron1x.clans.plugin.papi.PlaceholderAPIHook;
import org.gepron1x.clans.plugin.shield.region.wg.WgGlobalRegions;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.gepron1x.clans.plugin.storage.HikariDataSourceCreation;
import org.gepron1x.clans.plugin.storage.RegionStorage;
import org.gepron1x.clans.plugin.storage.implementation.sql.*;
import org.gepron1x.clans.plugin.users.DefaultUsers;
import org.gepron1x.clans.plugin.util.AsciiArt;
import org.gepron1x.clans.plugin.util.MapOf;
import org.gepron1x.clans.plugin.util.MapRegistry;
import org.gepron1x.clans.plugin.util.action.ActionParser;
import org.gepron1x.clans.plugin.util.hologram.Line;
import org.gepron1x.clans.plugin.util.services.PluginServices;
import org.gepron1x.clans.plugin.util.services.Services;
import org.gepron1x.clans.plugin.wg.RegionEffectSessionHandler;
import org.gepron1x.clans.plugin.wg.ShieldRefreshTask;
import org.gepron1x.clans.plugin.wg.WgExtension;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import space.arim.dazzleconf.ConfigurationOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.gepron1x.clans.plugin.wg.WgRepositoryImpl.AsyncRegionStorage;


public final class DecaliumClansPlugin extends JavaPlugin {

	public static final String VERSION = "0.1";
	public static final int BSTATS_ID = 17010;

	private RoleRegistry roleRegistry;

	private BukkitFactoryOfTheFuture futuresFactory;

	private ClanStorage storage;
	private RegionStorage regionStorage;

	private PaperCommandManager<CommandSender> commandManager;

	private Configuration<ClansConfig> configuration;
	private Configuration<MessagesConfig> messagesConfiguration;

	private Configuration<PricesConfig> prices;

	private MutableClansApi api;

	private UserCaching userCaching;


	@Override
	public void onEnable() {
		enable();
		new AsciiArt(getSLF4JLogger()).print();
		WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(
				new RegionEffectSessionHandler.Factory(this.api), null);
		getSLF4JLogger().info("Plugin successfully loaded!");
	}


	private void buildRoleRegistry() {
		ClanRole ownerRole = config().roles().ownerRole();
		ClanRole defaultRole = config().roles().defaultRole();
		List<ClanRole> otherRoles = config().roles().otherRoles();

		ArrayList<ClanRole> roles = new ArrayList<>(otherRoles.size() + 2);
		roles.add(ownerRole);
		roles.add(defaultRole);
		roles.addAll(otherRoles);
		this.roleRegistry = new RoleRegistryImpl(defaultRole, ownerRole, roles);

	}

	private void enable() {
		ClanBuilderFactory builderFactory = new ClanBuilderFactoryImpl();

		TagResolver resolver = TagResolver.resolver(
				TagResolver.standard(),
				TagResolver.resolver(
						"prefix", (queue, ctx) -> Tag.selfClosingInserting(messages().prefix())
				)
		);
		MiniMessage miniMessage = MiniMessage.builder().tags(resolver).build();

		ConfigurationOptions options = new ConfigurationOptions.Builder()
				.addSerialiser(new DurationSerializer())
				.addSerialiser(new TextMessageSerializer(miniMessage))
				.addSerialiser(new AdventureComponentSerializer(miniMessage))
				.addSerialiser(new ClanRoleSerializer(builderFactory))
				.addSerialiser(new ClanPermissionSerializer())
				.addSerialiser(new PatternSerializer())
				.addSerialiser(new TextColorSerializer())
				.addSerialiser(new HelpColorsSerializer())
				.addSerialiser(new FlagSetSerializer())
				.addSerialiser(new TimeFormatSerializer())
				.addSerialiser(new Line.Serializer())
				.addSerialiser(new MessageSerializer(new ActionParser(miniMessage)))
				.addSerialiser(new PotionEffectTypeSerializer())
				.addSerialiser(new RegionEffectSerializer())
				.setCreateSingleElementCollections(true)
				.build();
		this.messagesConfiguration = Configuration.create(this, "messages.yml", MessagesConfig.class, options);
		this.configuration = Configuration.create(this, "config.yml", ClansConfig.class, options);
		this.prices = Configuration.create(this, "prices.yml", PricesConfig.class, options);

		this.messagesConfiguration.reloadConfig();
		this.configuration.reloadConfig();
		this.prices.reloadConfig();

		futuresFactory = BukkitFactoryOfTheFuture.fixedThreadPool(this, config().storage().hikariPool().maxPoolSize());

		buildRoleRegistry();
		ClansConfig config = config();
		MessagesConfig messages = messages();

		Configs configs = new Configs(this.configuration, this.messagesConfiguration);


		HikariDataSource ds = new HikariDataSourceCreation(this, config).create();
		Jdbi jdbi = new JdbiCreation(ds).create();

		this.storage = new SqlClanStorage(this, jdbi, ds, config.storage().type(), new ClanCollector(builderFactory, roleRegistry));
		this.storage.initialize();


		ClanCache clanCache = new ClanCache();

		ClanRepository base = new ClanRepositoryImpl(this.storage, futuresFactory);

		ClanRepository repository = new AnnouncingClanRepository(base,
				getServer(),
				messages);


		if (config.levels().enabled()) {
			repository = new LeveledClanRepository(repository, futuresFactory, configs);
		}

		userCaching = new UserCaching(repository, clanCache, getServer(), futuresFactory);

		CachingClanRepository cachingClanRepository = new CachingClanRepositoryImpl(
				repository,
				futuresFactory,
				clanCache
		);

		regionStorage = new SqlRegionStorage(jdbi, cachingClanRepository, new SqlQueue(), new MapRegistry<>(new MapOf<>(RegionEffect::name, config.regionEffects()).create()));
		WgGlobalRegions regions = new WgGlobalRegions(regionStorage.loadRegions(), WorldGuard.getInstance().getPlatform().getRegionContainer(), configs);
		regions.update();
		CachingClanRepository clanRepository = new WgExtension(cachingClanRepository, configs, regions, new AsyncRegionStorage(futuresFactory, regionStorage)).make();

		getServer().getScheduler().runTaskTimerAsynchronously(this, regionStorage::save, 20 * 60, 20 * 60);


		Services services = new PluginServices(this);
		Users users = new DefaultUsers(clanRepository, regions);
		if (isEnabled("Vault")) {
			users = new VaultHook(users, services, prices.data(), futuresFactory).hook();
		}

		getServer().getPluginManager().registerEvents(new CacheListener(userCaching), this);
		ClanTopCache topCache = new ClanTopCache(cachingClanRepository.top(), 10);
		Set.of(StatisticType.KILLS, StatisticType.CLAN_WAR_WINS).forEach(topCache::cache);
		getServer().getScheduler().runTaskTimer(this, topCache, 20 * 60 * 5, 20 * 60 * 5);

		LegacyComponentSerializer legacy = LegacyComponentSerializer.builder().character(LegacyComponentSerializer.SECTION_CHAR).hexColors().useUnusualXRepeatedCharacterHexFormat().build();

		if (isEnabled("PlaceholderAPI")) {
			new PlaceholderAPIHook(getServer(), config, clanCache, topCache, legacy).register();
		}

		if (isEnabled("CarbonChat")) {
			new CarbonChatHook(getServer(), clanCache, configs).register();
		}

		Logger logger = getSLF4JLogger();

		ClanCommand command = new ClanCommand(logger, clanRepository, users, configs, futuresFactory, builderFactory, roleRegistry);
		InviteCommand inviteCommand = new InviteCommand(logger, clanRepository, users, configs, futuresFactory, builderFactory, roleRegistry);
		MemberCommand memberCommand = new MemberCommand(logger, clanRepository, users, configs, futuresFactory);
		HomeCommand homeCommand = new HomeCommand(logger, clanRepository, users, configs, futuresFactory, builderFactory);
		Wars wars = new WarsCreation(this, configs).create();
		ClanWarCommand clanWarCommand = new ClanWarCommand(logger, clanRepository, users, configs, futuresFactory, wars);
		// ShieldCommand shieldCommand = new ShieldCommand(logger, clanRepository, users, configs, futuresFactory, shields);

		try {
			commandManager = new CommandManagerCreation(this, clanRepository, roleRegistry, messages).create();
		} catch (Exception e) {
			getSLF4JLogger().error("Error initializing the command manager! Please contact the developer.", e);
		}


		command.register(commandManager);
		inviteCommand.register(commandManager);
		memberCommand.register(commandManager);
		homeCommand.register(commandManager);
		clanWarCommand.register(commandManager);
		// shieldCommand.register(commandManager);

		commandManager.command(
				commandManager.commandBuilder("clan").literal("reload").permission("clans.admin.reload")
						.meta(CommandMeta.DESCRIPTION, messages.help().messages().descriptions().reload())
						.handler(ctx -> {
							disable();
							enable();
							List<UUID> uuids = getServer().getOnlinePlayers().stream().map(Player::getUniqueId).toList();
							futuresFactory.runAsync(() -> uuids.forEach(this.userCaching::cacheUser)).thenAccept(ignored -> {
								ctx.getSender().sendPlainMessage("[DecaliumClans] Successfully reloaded.");
							});
						})
		);
		var help = new MinecraftHelp<>("/clan help", AudienceProvider.nativeAudience(), this.commandManager);
		help.setHelpColors(messages.help().colors());
		help.messageProvider(messages.help().messages().messageProvider());
		help.descriptionDecorator(s -> miniMessage.deserialize(s));
		commandManager.command(commandManager.commandBuilder("clan").literal("help", "usage")
				.meta(CommandMeta.DESCRIPTION, messages.help().messages().descriptions().help())
				.permission("clans.help")
				.argument(StringArgument.<CommandSender>builder("query").greedy().asOptionalWithDefault(""))
				.handler(ctx -> help.queryCommands(ctx.getOrDefault("query", ""), ctx.getSender())));
		StatisticListener statisticListener = new StatisticListener(clanRepository, this, futuresFactory, config);
		getServer().getPluginManager().registerEvents(statisticListener, this);
		statisticListener.start();
		new ShieldRefreshTask(regions, WorldGuard.getInstance().getPlatform().getRegionContainer(), configs)
				.runTaskTimerAsynchronously(this, 20, 5 * 20);

		DecaliumClansApi clansApi = new DecaliumClansApiImpl(clanRepository, users, this.roleRegistry, builderFactory, futuresFactory, wars, regions, prices.data(), config.levels());
		this.api = this.api == null ? new MutableClansApi(clansApi) : this.api;
		this.api.setApi(clansApi);
		services.register(DecaliumClansApi.class, this.api);
		new Metrics(this, BSTATS_ID);

		getServer().getPluginManager().registerEvents(new CombatLogListener(users), this);

	}

	private void disable() {
		regionStorage.save();
		this.getServer().getScheduler().cancelTasks(this);
		if (this.storage != null) this.storage.shutdown();
		HandlerList.unregisterAll(this);
		try {
			futuresFactory.shutdownAndTerminate();
		} catch (InterruptedException e) {
			getSLF4JLogger().error("Failed to shutdown with following error:", e);
		}
		PlaceholderAPIHook.unregister();
		getServer().getServicesManager().unregisterAll(this);
	}

	@Override
	public void onLoad() {
		/*if(isEnabled("WorldGuard"))*/
		WgExtension.registerFlags();
	}

	private boolean isEnabled(String pluginName) {
		return getServer().getPluginManager().isPluginEnabled(pluginName);
	}

	@Override
	public void onDisable() {
		disable();
		getSLF4JLogger().info("Goodbye!");
	}

	public ClansConfig config() {
		return configuration.data();
	}

	public MessagesConfig messages() {
		return messagesConfiguration.data();
	}

	public PaperCommandManager<CommandSender> commandManager() {
		return this.commandManager;
	}

}
