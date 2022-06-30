package org.gepron1x.clans.plugin.config;

import com.destroystokyo.paper.util.SneakyThrow;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;
import space.arim.dazzleconf.ConfigurationFactory;
import space.arim.dazzleconf.ConfigurationOptions;
import space.arim.dazzleconf.error.ConfigFormatSyntaxException;
import space.arim.dazzleconf.error.InvalidConfigException;
import space.arim.dazzleconf.ext.snakeyaml.CommentMode;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlConfigurationFactory;
import space.arim.dazzleconf.ext.snakeyaml.SnakeYamlOptions;
import space.arim.dazzleconf.helper.ConfigurationHelper;

import java.io.IOException;
import java.nio.file.Path;

public final class Configuration<C> {

    private final Logger logger;
    private final ConfigurationHelper<C> configHelper;
    private volatile C configData;

    private Configuration(Logger logger, ConfigurationHelper<C> configHelper) {
        this.logger = logger;
        this.configHelper = configHelper;
    }

    public static <C> Configuration<C> create(Plugin plugin,
                                              String fileName,
                                              Class<C> configClass,
                                              ConfigurationOptions options) {
        return create(plugin.getSLF4JLogger(), plugin.getDataFolder().toPath(), fileName, configClass, options);
    }

    public static <C> Configuration<C> create(Logger logger,
                                              Path configFolder,
                                              String fileName,
                                              Class<C> configClass,
                                              ConfigurationOptions options) {
        SnakeYamlOptions yamlOptions = new SnakeYamlOptions.Builder()
                .commentMode(CommentMode.alternativeWriter())
                .build();
        ConfigurationFactory<C> configFactory = SnakeYamlConfigurationFactory.create(
                configClass,
                options, // change this if desired
                yamlOptions);
        return new Configuration<>(logger, new ConfigurationHelper<>(configFolder, fileName, configFactory));
    }

    public void reloadConfig() {
        try {
            configData = configHelper.reloadConfigData();
        } catch (IOException ex) {
            SneakyThrow.sneaky(ex);

        } catch (ConfigFormatSyntaxException ex) {
            loadDefault();
            logger.error("The yaml syntax in your configuration is invalid. "
                    + "Check your YAML syntax with a tool such as https://yaml-online-parser.appspot.com/", ex);

        } catch (InvalidConfigException ex) {
            loadDefault();
            logger.error("One of the values in your configuration is not valid. "
                    + "Check to make sure you have specified the right data types.", ex);
        }
    }

    private void loadDefault() {
        logger.error("Failed to load configuration! Loading defaults!");
        this.configData = configHelper.getFactory().loadDefaults();
    }

    public C data() {
        if(this.configData == null) {
            this.reloadConfig();
        }
        return this.configData;
    }

}


