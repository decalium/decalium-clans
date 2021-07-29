package org.bukkit.craftbukkit.v1_17_R1.entity;

import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderDragonPart;
import org.bukkit.entity.Entity;

public class CraftEnderDragonPart extends CraftComplexPart implements EnderDragonPart {
    public CraftEnderDragonPart(CraftServer server, net.minecraft.world.entity.boss.EnderDragonPart entity) {
        super(server, entity);
    }

    @Override
    public EnderDragon getParent() {
        return (EnderDragon) super.getParent();
    }

    @Override
    public net.minecraft.world.entity.boss.EnderDragonPart getHandle() {
        return (net.minecraft.world.entity.boss.EnderDragonPart) entity;
    }

    @Override
    public String toString() {
        return "CraftEnderDragonPart";
    }

    @Override
    public void damage(double amount) {
        this.getParent().damage(amount);
    }

    @Override
    public void damage(double amount, Entity source) {
        this.getParent().damage(amount, source);
    }

    @Override
    public double getHealth() {
        return this.getParent().getHealth();
    }

    @Override
    public void setHealth(double health) {
        this.getParent().setHealth(health);
    }

    @Override
    public double getAbsorptionAmount() {
        return this.getParent().getAbsorptionAmount();
    }

    @Override
    public void setAbsorptionAmount(double amount) {
        this.getParent().setAbsorptionAmount(amount);
    }

    @Override
    public double getMaxHealth() {
        return this.getParent().getMaxHealth();
    }

    @Override
    public void setMaxHealth(double health) {
        this.getParent().setMaxHealth(health);
    }

    @Override
    public void resetMaxHealth() {
        this.getParent().resetMaxHealth();
    }
}
