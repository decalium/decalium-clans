package org.bukkit.craftbukkit.v1_18_R1.inventory;

import com.google.common.collect.ImmutableMap; // Paper
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.DelegateDeserialization;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftMetaItem.SerializableMeta;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftChatMessage;
import org.bukkit.inventory.meta.BookMeta;

@DelegateDeserialization(SerializableMeta.class)
class CraftMetaBookSigned extends CraftMetaBook implements BookMeta {

    CraftMetaBookSigned(CraftMetaItem meta) {
        super(meta);
    }

    CraftMetaBookSigned(CompoundTag tag) {
        super(tag);
    }

    CraftMetaBookSigned(Map<String, Object> map) {
        super(map);
    }

    @Override
    protected String deserializePage(String pageData) {
        return CraftChatMessage.fromJSONOrStringToJSON(pageData, false, true, MAX_PAGE_LENGTH, false);
    }

    @Override
    protected String convertPlainPageToData(String page) {
        return CraftChatMessage.fromStringToJSON(page, true);
    }

    @Override
    protected String convertDataToPlainPage(String pageData) {
        return CraftChatMessage.fromJSONComponent(pageData);
    }

    @Override
    void applyToItem(CompoundTag itemData) {
        super.applyToItem(itemData);
    }

    @Override
    boolean isEmpty() {
        return super.isEmpty();
    }

    @Override
    boolean applicableTo(Material type) {
        switch (type) {
        case WRITTEN_BOOK:
        case WRITABLE_BOOK:
            return true;
        default:
            return false;
        }
    }

    @Override
    public CraftMetaBookSigned clone() {
        CraftMetaBookSigned meta = (CraftMetaBookSigned) super.clone();
        return meta;
    }

    @Override
    int applyHash() {
        final int original;
        int hash = original = super.applyHash();
        return original != hash ? CraftMetaBookSigned.class.hashCode() ^ hash : hash;
    }

    @Override
    boolean equalsCommon(CraftMetaItem meta) {
        return super.equalsCommon(meta);
    }

    @Override
    boolean notUncommon(CraftMetaItem meta) {
        return super.notUncommon(meta) && (meta instanceof CraftMetaBookSigned || isBookEmpty());
    }

    @Override
    ImmutableMap.Builder<String, Object> serialize(ImmutableMap.Builder<String, Object> builder) {
        super.serialize(builder);
        return builder;
    }

    // Paper start - adventure
    private CraftMetaBookSigned(net.kyori.adventure.text.Component title, net.kyori.adventure.text.Component author, java.util.List<net.kyori.adventure.text.Component> pages) {
        super((org.bukkit.craftbukkit.v1_18_R1.inventory.CraftMetaItem) org.bukkit.Bukkit.getItemFactory().getItemMeta(Material.WRITABLE_BOOK));
        this.title = title == null ? null : io.papermc.paper.adventure.PaperAdventure.LEGACY_SECTION_UXRC.serialize(title);
        this.author = author == null ? null : io.papermc.paper.adventure.PaperAdventure.LEGACY_SECTION_UXRC.serialize(author);
        this.pages = io.papermc.paper.adventure.PaperAdventure.asJson(pages.subList(0, Math.min(MAX_PAGES, pages.size())));
    }

    static final class CraftMetaBookSignedBuilder extends CraftMetaBookBuilder {
        @Override
        protected BookMeta build(net.kyori.adventure.text.Component title, net.kyori.adventure.text.Component author, java.util.List<net.kyori.adventure.text.Component> pages) {
            return new CraftMetaBookSigned(title, author, pages);
        }
    }

    @Override
    public BookMetaBuilder toBuilder() {
        return new CraftMetaBookSignedBuilder();
    }
    // Paper end
}
