package org.bukkit.craftbukkit.v1_17_R1.inventory;

import net.minecraft.world.Container;
import org.bukkit.inventory.SaddledHorseInventory;

public class CraftSaddledInventory extends CraftInventoryAbstractHorse implements SaddledHorseInventory {

    public CraftSaddledInventory(Container inventory) {
        super(inventory);
    }

}
