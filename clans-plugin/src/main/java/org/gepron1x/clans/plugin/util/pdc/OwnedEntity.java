package org.gepron1x.clans.plugin.util.pdc;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;

import java.util.Optional;
import java.util.UUID;

public final class OwnedEntity {

    private static final NamespacedKey OWNER = new NamespacedKey(JavaPlugin.getPlugin(DecaliumClansPlugin.class), "owner");

    private final Entity entity;

    public OwnedEntity(Entity entity) {
        this.entity = entity;
    }

    public Optional<UUID> owner() {
        return Optional.ofNullable(this.entity.getPersistentDataContainer().get(OWNER, UuidDataType.UUID));
    }

    public void owner(UUID owner) {
        this.entity.getPersistentDataContainer().set(OWNER, UuidDataType.UUID, owner);
    }
}
