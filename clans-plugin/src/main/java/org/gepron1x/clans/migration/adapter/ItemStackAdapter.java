package org.gepron1x.clans.migration.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Base64;
// im sorry
public final class ItemStackAdapter extends TypeAdapter<ItemStack> {


    @Override
    public void write(JsonWriter out, ItemStack value) throws IOException {
        out.value(Base64.getEncoder().encodeToString(value.serializeAsBytes()));
    }

    @Override
    public ItemStack read(JsonReader in) throws IOException {
        return ItemStack.deserializeBytes(Base64.getDecoder().decode(in.nextString()));
    }
}
