/*
 * decalium-clans
 * Copyright © 2022 George Pronyuk <https://vk.com/gpronyuk>
 *
 * decalium-clans is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * decalium-clans is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with decalium-clans. If not, see <https://www.gnu.org/licenses/>
 * and navigate to version 3 of the GNU Lesser General Public License.
 */
package org.gepron1x.clans.api.util;

import com.destroystokyo.paper.util.SneakyThrow;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

public final class InventorySerialization {

    private InventorySerialization() {}

    public static byte[] serializeInventory(@NotNull Inventory inventory) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos);) {
            writeInventory(inventory, oos);
            return bos.toByteArray();
        } catch (IOException e) {
            SneakyThrow.sneaky(e);
            return null;
        }
    }


    public static void writeInventory(@NotNull Inventory inventory, @NotNull ObjectOutputStream oos) throws IOException {
        ItemStack[] contents = inventory.getContents();
        int size = contents.length;
        int sized = (int) Arrays.stream(contents).filter(Objects::nonNull).count();
        oos.writeInt(sized);
        for (int slot = 0; slot < size; slot++) {
            ItemStack item = contents[slot];
            if (item == null) {
                continue;
            }
            oos.writeShort(slot);
            byte[] itemAsBytes = item.serializeAsBytes();
            oos.writeInt(itemAsBytes.length);
            oos.write(itemAsBytes);
        }
    }

    public static void readInventory(@NotNull Inventory inventory, @NotNull ObjectInputStream ois) throws IOException, ClassNotFoundException {
        int size = ois.readInt();
        for (int i = 0; i < size; i++) {
            int slot = ois.readShort();
            int rawSize = ois.readInt();
            byte[] rawItem = new byte[rawSize];
            if(ois.read(rawItem) == rawSize){
                ItemStack itemStack = ItemStack.deserializeBytes(rawItem);
                inventory.setItem(slot, itemStack);
            }
        }
    }

    public static void deserializeInventory(@NotNull Inventory inventory, byte[] bytes) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInputStream ois = new ObjectInputStream(bis);) {
            readInventory(inventory, ois);
        } catch (ClassNotFoundException | IOException e) {
            SneakyThrow.sneaky(e);
        }
    }


}
