package org.bukkit.craftbukkit.v1_18_R1.block.data.type;

import org.bukkit.block.data.type.Bell;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;

public abstract class CraftBell extends CraftBlockData implements Bell {

    private static final net.minecraft.world.level.block.state.properties.EnumProperty<?> ATTACHMENT = getEnum("attachment");

    @Override
    public org.bukkit.block.data.type.Bell.Attachment getAttachment() {
        return get(CraftBell.ATTACHMENT, org.bukkit.block.data.type.Bell.Attachment.class);
    }

    @Override
    public void setAttachment(org.bukkit.block.data.type.Bell.Attachment leaves) {
        set(CraftBell.ATTACHMENT, leaves);
    }
}
