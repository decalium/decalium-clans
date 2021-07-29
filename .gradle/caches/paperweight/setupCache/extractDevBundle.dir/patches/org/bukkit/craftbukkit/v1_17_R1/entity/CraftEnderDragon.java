package org.bukkit.craftbukkit.v1_17_R1.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.util.Set;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.boss.CraftDragonBattle;
import org.bukkit.entity.ComplexEntityPart;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragon.Phase;
import org.bukkit.entity.EntityType;

public class CraftEnderDragon extends CraftComplexLivingEntity implements EnderDragon {

    public CraftEnderDragon(CraftServer server, net.minecraft.world.entity.boss.enderdragon.EnderDragon entity) {
        super(server, entity);
    }

    @Override
    public Set<ComplexEntityPart> getParts() {
        Builder<ComplexEntityPart> builder = ImmutableSet.builder();

        for (EnderDragonPart part : this.getHandle().subEntities) {
            builder.add((ComplexEntityPart) part.getBukkitEntity());
        }

        return builder.build();
    }

    @Override
    public net.minecraft.world.entity.boss.enderdragon.EnderDragon getHandle() {
        return (net.minecraft.world.entity.boss.enderdragon.EnderDragon) entity;
    }

    @Override
    public String toString() {
        return "CraftEnderDragon";
    }

    @Override
    public EntityType getType() {
        return EntityType.ENDER_DRAGON;
    }

    @Override
    public Phase getPhase() {
        return Phase.values()[this.getHandle().getEntityData().get(net.minecraft.world.entity.boss.enderdragon.EnderDragon.DATA_PHASE)];
    }

    @Override
    public void setPhase(Phase phase) {
        this.getHandle().getPhaseManager().setPhase(CraftEnderDragon.getMinecraftPhase(phase));
    }

    public static Phase getBukkitPhase(EnderDragonPhase phase) {
        return Phase.values()[phase.getId()];
    }

    public static EnderDragonPhase getMinecraftPhase(Phase phase) {
        return EnderDragonPhase.getById(phase.ordinal());
    }

    @Override
    public BossBar getBossBar() {
        return this.getDragonBattle().getBossBar();
    }

    @Override
    public DragonBattle getDragonBattle() {
        return new CraftDragonBattle(this.getHandle().getDragonFight());
    }

    @Override
    public int getDeathAnimationTicks() {
        return this.getHandle().dragonDeathTime;
    }
}
