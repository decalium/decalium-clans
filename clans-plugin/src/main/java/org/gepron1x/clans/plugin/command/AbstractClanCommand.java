package org.gepron1x.clans.plugin.command;

import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionHandler;
import org.bukkit.command.CommandSender;
import org.gepron1x.clans.api.repository.CachingClanRepository;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import org.slf4j.Logger;
import space.arim.omnibus.util.concurrent.FactoryOfTheFuture;

public abstract class AbstractClanCommand {


    private final Logger logger;
    protected final CachingClanRepository clanRepository;
    protected final ClansConfig clansConfig;
    protected final MessagesConfig messages;
    protected final FactoryOfTheFuture futuresFactory;

    public AbstractClanCommand(Logger logger, CachingClanRepository clanRepository,
                               ClansConfig clansConfig,
                               MessagesConfig messages,
                               FactoryOfTheFuture futuresFactory) {
        this.logger = logger;
        this.clanRepository = clanRepository;
        this.clansConfig = clansConfig;
        this.messages = messages;
        this.futuresFactory = futuresFactory;
    }



    public abstract void register(CommandManager<CommandSender> manager);


    
    protected <T> T exceptionHandler(Throwable throwable) {
        logger.error("A future completed exceptionally: ", throwable);
        return null;
    }

    protected ClanExecutionHandler clanExecutionHandler(CommandExecutionHandler<CommandSender> delegate) {
        return new ClanExecutionHandler(delegate, this.clanRepository, this.messages, this.logger);
    }








}
