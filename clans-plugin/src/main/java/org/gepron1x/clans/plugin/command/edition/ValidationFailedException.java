package org.gepron1x.clans.plugin.command.edition;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;

public final class ValidationFailedException extends RuntimeException {
    private final ComponentLike message;

    public ValidationFailedException(ComponentLike message) {
        this.message = message;
    }

    public void describe(Audience audience) {
        audience.sendMessage(message);
    }
}
