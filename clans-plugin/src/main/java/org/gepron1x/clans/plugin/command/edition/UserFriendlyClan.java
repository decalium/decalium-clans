package org.gepron1x.clans.plugin.command.edition;

import net.kyori.adventure.audience.Audience;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.edition.ClanEdition;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.config.MessagesConfig;
import space.arim.omnibus.util.concurrent.CentralisedFuture;

import java.util.function.Consumer;

public final class UserFriendlyClan {

    private final Clan clan;
    private final ClansConfig clansConfig;
    private final MessagesConfig messages;
    private final Audience audience;

    public UserFriendlyClan(Clan clan, ClansConfig clansConfig, MessagesConfig messages, Audience audience) {
        this.clan = clan;
        this.clansConfig = clansConfig;
        this.messages = messages;
        this.audience = audience;
    }

    public CentralisedFuture<Clan> edit(Consumer<ClanEdition> consumer) {
        try {
            consumer.accept(new UserFriendlyClanEdition(this.clan, this.clansConfig, this.messages));
        } catch (ValidationFailedException ex) {
            ex.describe(this.audience);
        }
        return this.clan.edit(consumer);
    }
}
