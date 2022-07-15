package org.gepron1x.clans.plugin.wg;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.gepron1x.clans.api.clan.Clan;
import org.gepron1x.clans.api.clan.home.ClanHome;
import org.gepron1x.clans.plugin.DecaliumClansPlugin;
import org.gepron1x.clans.plugin.config.ClansConfig;
import org.gepron1x.clans.plugin.util.Optionals;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public final class HologramOfHome {

    private static final NamespacedKey CLAN_NAME = new NamespacedKey(JavaPlugin.getPlugin(DecaliumClansPlugin.class), "clan_name");
    private static final NamespacedKey CLAN_HOME_NAME = new NamespacedKey(JavaPlugin.getPlugin(DecaliumClansPlugin.class), "clan_home_name");

    private final ClansConfig config;
    private final Clan clan;
    private final ClanHome home;

    public HologramOfHome(ClansConfig config, Clan clan, ClanHome home) {
        this.config = config;
        this.clan = clan;
        this.home = home;
    }

    public Optional<ArmorStand> entity() {
        Collection<ArmorStand> armorStands = this.home.location().getNearbyEntitiesByType(ArmorStand.class, 2, as -> {
            PersistentDataContainer pdc = as.getPersistentDataContainer();
            return Objects.equals(pdc.get(CLAN_NAME, PersistentDataType.STRING), clan.tag()) &&
                    Objects.equals(pdc.get(CLAN_HOME_NAME, PersistentDataType.STRING), home.name());
        });
        Preconditions.checkState(armorStands.size() <= 1, "Too many holograms spawned");
        return Optionals.ofIterable(armorStands);

    }

    public ArmorStand spawnIfNotPresent() {
        return entity().orElseGet(this::spawn);
    }

    public void destroy() {
        entity().ifPresent(Entity::remove);
    }

    public ArmorStand spawn() {
        Preconditions.checkState(entity().isEmpty(), "Already spawned.");
        return this.home.location().getWorld().spawn(this.home.location(), ArmorStand.class, stand -> {
            stand.setPersistent(true);
            stand.setCanMove(false);
            stand.setCanTick(false);
            stand.setCustomNameVisible(true);
            stand.setMarker(true);
            stand.setInvisible(true);
            PersistentDataContainer pdc = stand.getPersistentDataContainer();
            pdc.set(CLAN_NAME, PersistentDataType.STRING, clan.tag());
            pdc.set(CLAN_HOME_NAME, PersistentDataType.STRING, home.name());
            stand.customName(this.config.homes().hologramFormat().with("home_name", home.displayName()).asComponent());
            stand.setDisabledSlots(EquipmentSlot.values());
            stand.getEquipment().setHelmet(home.icon());
        });
    }

    public void rename(Component displayName) {
        entity().ifPresent(as -> as.customName(this.config.homes().hologramFormat().with("home_name", displayName).asComponent()));
    }

    public void icon(ItemStack icon) {
        entity().ifPresent(as -> as.getEquipment().setHelmet(icon));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HologramOfHome that = (HologramOfHome) o;
        return config.equals(that.config) && clan.equals(that.clan) && home.equals(that.home);
    }

    @Override
    public int hashCode() {
        return Objects.hash(config, clan, home);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("clan", clan)
                .add("home", home)
                .toString();
    }
}
