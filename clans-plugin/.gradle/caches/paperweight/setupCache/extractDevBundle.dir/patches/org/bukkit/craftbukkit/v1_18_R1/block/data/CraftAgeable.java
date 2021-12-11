package org.bukkit.craftbukkit.v1_18_R1.block.data;

import org.bukkit.block.data.Ageable;

public abstract class CraftAgeable extends CraftBlockData implements Ageable {

    private static final net.minecraft.world.level.block.state.properties.IntegerProperty AGE = getInteger("age");

    @Override
    public int getAge() {
        return get(CraftAgeable.AGE);
    }

    @Override
    public void setAge(int age) {
        set(CraftAgeable.AGE, age);
    }

    @Override
    public int getMaximumAge() {
        return getMax(CraftAgeable.AGE);
    }
}
