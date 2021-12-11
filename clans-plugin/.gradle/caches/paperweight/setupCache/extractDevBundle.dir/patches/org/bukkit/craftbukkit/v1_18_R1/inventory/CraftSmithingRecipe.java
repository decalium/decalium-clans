package org.bukkit.craftbukkit.v1_18_R1.inventory;

import net.minecraft.server.MinecraftServer;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;

public class CraftSmithingRecipe extends SmithingRecipe implements CraftRecipe {
    @Deprecated // Paper
    public CraftSmithingRecipe(NamespacedKey key, ItemStack result, RecipeChoice base, RecipeChoice addition) {
        super(key, result, base, addition);
    }
    // Paper start
    public CraftSmithingRecipe(NamespacedKey key, ItemStack result, RecipeChoice base, RecipeChoice addition, boolean copyNbt) {
        super(key, result, base, addition, copyNbt);
    }
    // Paper end

    public static CraftSmithingRecipe fromBukkitRecipe(SmithingRecipe recipe) {
        if (recipe instanceof CraftSmithingRecipe) {
            return (CraftSmithingRecipe) recipe;
        }
        CraftSmithingRecipe ret = new CraftSmithingRecipe(recipe.getKey(), recipe.getResult(), recipe.getBase(), recipe.getAddition(), recipe.willCopyNbt()); // Paper
        return ret;
    }

    @Override
    public void addToCraftingManager() {
        ItemStack result = this.getResult();

        MinecraftServer.getServer().getRecipeManager().addRecipe(new net.minecraft.world.item.crafting.UpgradeRecipe(CraftNamespacedKey.toMinecraft(this.getKey()), toNMS(this.getBase(), true), toNMS(this.getAddition(), true), CraftItemStack.asNMSCopy(result), this.willCopyNbt())); // Paper
    }
}
