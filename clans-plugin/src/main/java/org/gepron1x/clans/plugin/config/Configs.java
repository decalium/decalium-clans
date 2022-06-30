package org.gepron1x.clans.plugin.config;


public final class Configs {


    private final Configuration<ClansConfig> clansConfig;
    private final Configuration<MessagesConfig> messagesConfig;

    public Configs(Configuration<ClansConfig> clansConfig, Configuration<MessagesConfig> messagesConfig) {

        this.clansConfig = clansConfig;
        this.messagesConfig = messagesConfig;
    }

    public void reload() {
        this.clansConfig.reloadConfig();
        this.messagesConfig.reloadConfig();
    }

    public ClansConfig config() {
        return this.clansConfig.data();
    }

    public MessagesConfig messages() {
        return this.messagesConfig.data();
    }



}
