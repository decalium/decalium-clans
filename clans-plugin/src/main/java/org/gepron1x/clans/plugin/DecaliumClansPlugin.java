/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
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

import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.parser.ParserRegistry;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import io.leangen.geantyref.TypeToken;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.ClanBuilderFactory;
import org.gepron1x.clans.api.DecaliumClansApi;
import org.gepron1x.clans.api.RoleRegistry;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.api.clan.member.ClanMember;
import org.gepron1x.clans.api.clan.member.ClanRole;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.api.repository.ClanRepository;
import org.gepron1x.clans.api.user.Users;
import org.gepron1x.clans.api.war.Wars;
import org.gepron1x.clans.plugin.announce.AnnouncingClanRepository;
import org.gepron1x.clans.plugin.async.BukkitFactoryOfTheFuture;
import org.gepron1x.clans.plugin.bootstrap.WarsCreation;
import org.gepron1x.clans.plugin.cache.CachingClanRepositoryImpl;
import org.gepron1x.clans.plugin.cache.ClanCache;
import org.gepron1x.clans.plugin.cache.UserCaching;
import org.gepron1x.clans.plugin.chat.carbon.CarbonChatHook;
import org.gepron1x.clans.plugin.command.ClanCommand;
import org.gepron1x.clans.plugin.command.HomeCommand;
import org.gepron1x.clans.plugin.command.InviteCommand;
import org.gepron1x.clans.plugin.command.MemberCommand;
import org.gepron1x.clans.plugin.command.parser.ClanRoleParser;
import org.gepron1x.clans.plugin.command.parser.HomeParser;
import org.gepron1x.clans.plugin.command.parser.MemberParser;
import org.gepron1x.clans.plugin.command.war.ClanWarCommand;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.Configuration;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.gepron1x.clans.plugin.config.PricesConfig;
import org.gepron1x.clans.plugin.config.serializer.*;
import org.gepron1x.clans.plugin.economy.VaultHook;
import org.gepron1x.clans.plugin.listener.CacheListener;
import org.gepron1x.clans.plugin.listener.StatisticListener;
import org.gepron1x.clans.plugin.papi.PlaceholderAPIHook;
import org.gepron1x.clans.plugin.storage.ClanStorage;
import org.gepron1x.clans.plugin.storage.StorageCreation;
import org.gepron1x.clans.plugin.users.DefaultUsers;
import org.gepron1x.clans.plugin.util.AsciiArt;
import org.gepron1x.clans.plugin.wg.WgExtension;
import org.slf4j.Logger;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;


public final class DecaliumClansPlugin extends JavaPlugin {

    public static final String VERSION = "0.1";

    private RoleRegistry roleRegistry;

    private ClanStorage storage;

    private PaperCommandManager<CommandSender> commandManager;

    private Configuration<ClansConfig> configuration;
    private Configuration<MessagesConfig> messagesConfiguration;

    private Configuration<PricesConfig> prices;
    private UserCaching userCaching;


    @Override
    public void onEnable() {
        enable();
        new AsciiArt(getSLF4JLogger()).print();
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
        FactoryOfTheFuture futuresFactory = new BukkitFactoryOfTheFuture(this);
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
                .addSerialiser(new MessageSerializer(miniMessage))
                .addSerialiser(new AdventureComponentSerializer(miniMessage))
                .addSerialiser(new ClanRoleSerializer(builderFactory))
                .addSerialiser(new ClanPermissionSerializer())
                .build();
        this.messagesConfiguration = Configuration.create(this, "messages.yml", MessagesConfig.class, options);
        this.configuration = Configuration.create(this, "config.yml", ClansConfig.class, options);
        this.prices = Configuration.create(this, "prices.yml", PricesConfig.class, options);

        this.messagesConfiguration.reloadConfig();
        this.configuration.reloadConfig();
        this.prices.reloadConfig();

        buildRoleRegistry();
        ClansConfig config = config();
        MessagesConfig messages = messages();



        this.storage = new StorageCreation(this, config(), builderFactory, roleRegistry).create();
        this.storage.initialize();
        ClanCache clanCache = new ClanCache();

        ClanRepository base = new ClanRepositoryImpl(this.storage, futuresFactory);

        ClanRepository repository = new AnnouncingClanRepository(
                isEnabled("WorldGuard") ? new WgExtension(
                        getServer(),
                        config(),
                        base).make() : base,
                getServer(),
                messages);

        userCaching = new UserCaching(repository, clanCache, getServer());

        CachingClanRepository clanRepository = new CachingClanRepositoryImpl(
                repository,
                futuresFactory,
                clanCache
        );

        Users users = new DefaultUsers(clanRepository);
        if(isEnabled("Vault")) {
            users = new VaultHook(users, getServer().getServicesManager(), prices.data(), futuresFactory).hook();
        }

        getServer().getPluginManager().registerEvents(new CacheListener(userCaching), this);



        if(isEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook(getServer(), config, clanCache, LegacyComponentSerializer.legacySection()).register();
        }

        if(isEnabled("CarbonChat")) {
            new CarbonChatHook(getServer(), clanCache, messages, config).register();
        }

        Logger logger = getSLF4JLogger();

        ClanCommand command = new ClanCommand(logger, clanRepository, users, config, messages, futuresFactory, builderFactory, roleRegistry);
        InviteCommand inviteCommand = new InviteCommand(logger, clanRepository, users, config, messages, futuresFactory, builderFactory, roleRegistry);
        MemberCommand memberCommand = new MemberCommand(logger, clanRepository, users, config, messages, futuresFactory);
        HomeCommand homeCommand = new HomeCommand(logger, clanRepository, users, config, messages, futuresFactory, builderFactory);
        Wars wars = new WarsCreation(this, config, messages).create();
        ClanWarCommand clanWarCommand = new ClanWarCommand(logger, clanRepository, users, config, messages, futuresFactory, wars);

        try {
            this.commandManager = new PaperCommandManager<>(
                    this,
                    CommandExecutionCoordinator.simpleCoordinator(),
                    UnaryOperator.identity(), UnaryOperator.identity());
        } catch (Exception e) {
            getLogger().severe("error caused initializing command manager!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        ParserRegistry<CommandSender> parserRegistry = commandManager.parserRegistry();
        parserRegistry.registerParserSupplier(TypeToken.get(ClanRole.class), params -> new ClanRoleParser<>(roleRegistry));
        parserRegistry.registerParserSupplier(TypeToken.get(ClanMember.class), params -> {
            return new MemberParser(clanRepository, getServer());
        });
        parserRegistry.registerParserSupplier(TypeToken.get(ClanHome.class), params ->
                new HomeParser(clanRepository)
        );
        commandManager.registerBrigadier();
        commandManager.setSetting(CommandManager.ManagerSettings.OVERRIDE_EXISTING_COMMANDS, true);

        commandManager.registerExceptionHandler(NoPermissionException.class, (sender, ex) -> {
            sender.sendMessage(this.messages().noPermission());
        });

        command.register(commandManager);
        inviteCommand.register(commandManager);
        memberCommand.register(commandManager);
        homeCommand.register(commandManager);
        clanWarCommand.register(commandManager);

        commandManager.command(
                commandManager.commandBuilder("clan").literal("reload").permission("clans.admin.reload").handler(ctx -> {
                    disable();
                    enable();
                    List<UUID> uuids = getServer().getOnlinePlayers().stream().map(Player::getUniqueId).toList();
                    futuresFactory.runAsync(() -> uuids.forEach(this.userCaching::cacheUser)).thenAccept(ignored -> {
                        ctx.getSender().sendMessage("[DecaliumClans] Successfully reloaded.");
                    });
                })
        );

        StatisticListener statisticListener = new StatisticListener(clanRepository, this, futuresFactory, config);
        getServer().getPluginManager().registerEvents(statisticListener, this);
        statisticListener.start();

        DecaliumClansApi clansApi = new DecaliumClansApiImpl(clanRepository, users, this.roleRegistry, builderFactory, futuresFactory, wars);
        getServer().getServicesManager().register(DecaliumClansApi.class, clansApi, this, ServicePriority.Normal);
    }

    private void disable() {
        if(this.storage != null) this.storage.shutdown();
        HandlerList.unregisterAll(this);
        this.getServer().getScheduler().cancelTasks(this);
        getServer().getServicesManager().unregisterAll(this);
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

}
