package org.bukkit.craftbukkit.v1_17_R1.util;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Dynamic;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import org.bukkit.Bukkit;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.UnsafeValues;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_17_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.legacy.CraftLegacy;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginDescriptionFile;

@SuppressWarnings("deprecation")
public final class CraftMagicNumbers implements UnsafeValues {
    public static final UnsafeValues INSTANCE = new CraftMagicNumbers();

    private CraftMagicNumbers() {}

    // Paper start
    @Override
    public net.kyori.adventure.text.flattener.ComponentFlattener componentFlattener() {
        return io.papermc.paper.adventure.PaperAdventure.FLATTENER;
    }

    @Override
    public net.kyori.adventure.text.serializer.gson.GsonComponentSerializer colorDownsamplingGsonComponentSerializer() {
        return io.papermc.paper.adventure.PaperAdventure.COLOR_DOWNSAMPLING_GSON;
    }

    @Override
    public net.kyori.adventure.text.serializer.gson.GsonComponentSerializer gsonComponentSerializer() {
        return io.papermc.paper.adventure.PaperAdventure.GSON;
    }

    @Override
    public net.kyori.adventure.text.serializer.plain.PlainComponentSerializer plainComponentSerializer() {
        return io.papermc.paper.adventure.PaperAdventure.PLAIN;
    }

    @Override
    public net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer legacyComponentSerializer() {
        return io.papermc.paper.adventure.PaperAdventure.LEGACY_SECTION_UXRC;
    }
    // Paper end

    public static BlockState getBlock(MaterialData material) {
        return CraftMagicNumbers.getBlock(material.getItemType(), material.getData());
    }

    public static BlockState getBlock(Material material, byte data) {
        return CraftLegacy.fromLegacyData(CraftLegacy.toLegacy(material), data);
    }

    public static MaterialData getMaterial(BlockState data) {
        return CraftLegacy.toLegacy(CraftMagicNumbers.getMaterial(data.getBlock())).getNewData(CraftMagicNumbers.toLegacyData(data));
    }

    public static Item getItem(Material material, short data) {
        if (material.isLegacy()) {
            return CraftLegacy.fromLegacyData(CraftLegacy.toLegacy(material), data);
        }

        return CraftMagicNumbers.getItem(material);
    }

    public static MaterialData getMaterialData(Item item) {
        return CraftLegacy.toLegacyData(CraftMagicNumbers.getMaterial(item));
    }

    // ========================================================================
    private static final Map<Block, Material> BLOCK_MATERIAL = new HashMap<>();
    private static final Map<Item, Material> ITEM_MATERIAL = new HashMap<>();
    private static final Map<net.minecraft.world.level.material.Fluid, Fluid> FLUID_MATERIAL = new HashMap<>();
    private static final Map<Material, Item> MATERIAL_ITEM = new HashMap<>();
    private static final Map<Material, Block> MATERIAL_BLOCK = new HashMap<>();
    private static final Map<Material, net.minecraft.world.level.material.Fluid> MATERIAL_FLUID = new HashMap<>();
    // Paper start
    private static final Map<org.bukkit.entity.EntityType, net.minecraft.world.entity.EntityType<?>> ENTITY_TYPE_ENTITY_TYPES = new HashMap<>();
    private static final Map<net.minecraft.world.entity.EntityType<?>, org.bukkit.entity.EntityType> ENTITY_TYPES_ENTITY_TYPE = new HashMap<>();

    static {
        for (org.bukkit.entity.EntityType type : org.bukkit.entity.EntityType.values()) {
            if (type == org.bukkit.entity.EntityType.UNKNOWN) continue;
            ENTITY_TYPE_ENTITY_TYPES.put(type, net.minecraft.core.Registry.ENTITY_TYPE.get(CraftNamespacedKey.toMinecraft(type.getKey())));
            ENTITY_TYPES_ENTITY_TYPE.put(net.minecraft.core.Registry.ENTITY_TYPE.get(CraftNamespacedKey.toMinecraft(type.getKey())), type);
        }
        // Paper end
        for (Block block : net.minecraft.core.Registry.BLOCK) {
            BLOCK_MATERIAL.put(block, Material.getMaterial(net.minecraft.core.Registry.BLOCK.getKey(block).getPath().toUpperCase(Locale.ROOT)));
        }

        for (Item item : net.minecraft.core.Registry.ITEM) {
            ITEM_MATERIAL.put(item, Material.getMaterial(net.minecraft.core.Registry.ITEM.getKey(item).getPath().toUpperCase(Locale.ROOT)));
        }

        for (net.minecraft.world.level.material.Fluid fluid : net.minecraft.core.Registry.FLUID) {
            FLUID_MATERIAL.put(fluid, Registry.FLUID.get(CraftNamespacedKey.fromMinecraft(net.minecraft.core.Registry.FLUID.getKey(fluid))));
        }

        for (Material material : Material.values()) {
            if (material.isLegacy()) {
                continue;
            }

            ResourceLocation key = key(material);
            net.minecraft.core.Registry.ITEM.getOptional(key).ifPresent((item) -> {
                CraftMagicNumbers.MATERIAL_ITEM.put(material, item);
            });
            net.minecraft.core.Registry.BLOCK.getOptional(key).ifPresent((block) -> {
                CraftMagicNumbers.MATERIAL_BLOCK.put(material, block);
            });
            net.minecraft.core.Registry.FLUID.getOptional(key).ifPresent((fluid) -> {
                CraftMagicNumbers.MATERIAL_FLUID.put(material, fluid);
            });
        }
    }

    public static Material getMaterial(Block block) {
        return CraftMagicNumbers.BLOCK_MATERIAL.get(block);
    }

    public static Material getMaterial(Item item) {
        return CraftMagicNumbers.ITEM_MATERIAL.getOrDefault(item, Material.AIR);
    }

    public static Fluid getFluid(net.minecraft.world.level.material.Fluid fluid) {
        return CraftMagicNumbers.FLUID_MATERIAL.get(fluid);
    }

    public static Item getItem(Material material) {
        if (material != null && material.isLegacy()) {
            material = CraftLegacy.fromLegacy(material);
        }

        return CraftMagicNumbers.MATERIAL_ITEM.get(material);
    }

    public static Block getBlock(Material material) {
        if (material != null && material.isLegacy()) {
            material = CraftLegacy.fromLegacy(material);
        }

        return CraftMagicNumbers.MATERIAL_BLOCK.get(material);
    }

    public static net.minecraft.world.level.material.Fluid getFluid(Fluid fluid) {
        return CraftMagicNumbers.MATERIAL_FLUID.get(fluid);
    }

    public static ResourceLocation key(Material mat) {
        return CraftNamespacedKey.toMinecraft(mat.getKey());
    }
    // Paper start
    public static net.minecraft.world.entity.EntityType<?> getEntityTypes(org.bukkit.entity.EntityType type) {
        return ENTITY_TYPE_ENTITY_TYPES.get(type);
    }
    public static org.bukkit.entity.EntityType getEntityType(net.minecraft.world.entity.EntityType<?> entityTypes) {
        return ENTITY_TYPES_ENTITY_TYPE.get(entityTypes);
    }
    // Paper end
    // ========================================================================
    // Paper start
    @Override
    public void reportTimings() {
        co.aikar.timings.TimingsExport.reportTimings();
    }
    // Paper end

    public static byte toLegacyData(BlockState data) {
        return CraftLegacy.toLegacyData(data);
    }

    @Override
    public Material toLegacy(Material material) {
        return CraftLegacy.toLegacy(material);
    }

    @Override
    public Material fromLegacy(Material material) {
        return CraftLegacy.fromLegacy(material);
    }

    @Override
    public Material fromLegacy(MaterialData material) {
        return CraftLegacy.fromLegacy(material);
    }

    @Override
    public Material fromLegacy(MaterialData material, boolean itemPriority) {
        return CraftLegacy.fromLegacy(material, itemPriority);
    }

    @Override
    public BlockData fromLegacy(Material material, byte data) {
        return CraftBlockData.fromData(CraftMagicNumbers.getBlock(material, data));
    }

    @Override
    public Material getMaterial(String material, int version) {
        Preconditions.checkArgument(material != null, "material == null");
        Preconditions.checkArgument(version <= this.getDataVersion(), "Newer version! Server downgrades are not supported!");

        // Fastpath up to date materials
        if (version == this.getDataVersion()) {
            return Material.getMaterial(material);
        }

        Dynamic<Tag> name = new Dynamic<>(NbtOps.INSTANCE, StringTag.valueOf("minecraft:" + material.toLowerCase(Locale.ROOT)));
        Dynamic<Tag> converted = DataFixers.getDataFixer().update(References.ITEM_NAME, name, version, this.getDataVersion());

        if (name.equals(converted)) {
            converted = DataFixers.getDataFixer().update(References.BLOCK_NAME, name, version, this.getDataVersion());
        }

        return Material.matchMaterial(converted.asString(""));
    }

    /**
     * This string should be changed if the NMS mappings do.
     *
     * It has no meaning and should only be used as an equality check. Plugins
     * which are sensitive to the NMS mappings may read it and refuse to load if
     * it cannot be found or is different to the expected value.
     *
     * Remember: NMS is not supported API and may break at any time for any
     * reason irrespective of this. There is often supported API to do the same
     * thing as many common NMS usages. If not, you are encouraged to open a
     * feature and/or pull request for consideration, or use a well abstracted
     * third-party API such as ProtocolLib.
     *
     * @return string
     */
    public String getMappingsVersion() {
        return "f0e3dfc7390de285a4693518dd5bd126";
    }

    @Override
    public int getDataVersion() {
        return SharedConstants.getCurrentVersion().getWorldVersion();
    }

    @Override
    public ItemStack modifyItemStack(ItemStack stack, String arguments) {
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

        try {
            nmsStack.setTag((CompoundTag) TagParser.parseTag(arguments));
        } catch (CommandSyntaxException ex) {
            Logger.getLogger(CraftMagicNumbers.class.getName()).log(Level.SEVERE, null, ex);
        }

        stack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));

        return stack;
    }

    private static File getBukkitDataPackFolder() {
        return new File(MinecraftServer.getServer().getWorldPath(LevelResource.DATAPACK_DIR).toFile(), "bukkit");
    }

    @Override
    public Advancement loadAdvancement(NamespacedKey key, String advancement) {
        if (Bukkit.getAdvancement(key) != null) {
            throw new IllegalArgumentException("Advancement " + key + " already exists.");
        }
        ResourceLocation minecraftkey = CraftNamespacedKey.toMinecraft(key);

        JsonElement jsonelement = ServerAdvancementManager.GSON.fromJson(advancement, JsonElement.class);
        JsonObject jsonobject = GsonHelper.convertToJsonObject(jsonelement, "advancement");
        net.minecraft.advancements.Advancement.Builder nms = net.minecraft.advancements.Advancement.Builder.fromJson(jsonobject, new DeserializationContext(minecraftkey, MinecraftServer.getServer().getPredicateManager()));
        if (nms != null) {
            MinecraftServer.getServer().getAdvancements().advancements.add(Maps.newHashMap(Collections.singletonMap(minecraftkey, nms)));
            Advancement bukkit = Bukkit.getAdvancement(key);

            if (bukkit != null) {
                File file = new File(CraftMagicNumbers.getBukkitDataPackFolder(), "data" + File.separator + key.getNamespace() + File.separator + "advancements" + File.separator + key.getKey() + ".json");
                file.getParentFile().mkdirs();

                try {
                    Files.write(advancement, file, Charsets.UTF_8);
                } catch (IOException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "Error saving advancement " + key, ex);
                }

                // Paper start
                //MinecraftServer.getServer().getPlayerList().reload();
                MinecraftServer.getServer().getPlayerList().getPlayers().forEach(player -> {
                    player.getAdvancements().reload(MinecraftServer.getServer().getAdvancements());
                    player.getAdvancements().flushDirty(player);
                });
                // Paper end

                return bukkit;
            }
        }

        return null;
    }

    @Override
    public boolean removeAdvancement(NamespacedKey key) {
        File file = new File(CraftMagicNumbers.getBukkitDataPackFolder(), "data" + File.separator + key.getNamespace() + File.separator + "advancements" + File.separator + key.getKey() + ".json");
        return file.delete();
    }

    private static final List<String> SUPPORTED_API = Arrays.asList("1.13", "1.14", "1.15", "1.16", "1.17");

    @Override
    public void checkSupported(PluginDescriptionFile pdf) throws InvalidPluginException {
        String minimumVersion = MinecraftServer.getServer().server.minimumAPI;
        int minimumIndex = CraftMagicNumbers.SUPPORTED_API.indexOf(minimumVersion);

        if (pdf.getAPIVersion() != null) {
            int pluginIndex = CraftMagicNumbers.SUPPORTED_API.indexOf(pdf.getAPIVersion());

            if (pluginIndex == -1) {
                throw new InvalidPluginException("Unsupported API version " + pdf.getAPIVersion());
            }

            if (pluginIndex < minimumIndex) {
                throw new InvalidPluginException("Plugin API version " + pdf.getAPIVersion() + " is lower than the minimum allowed version. Please update or replace it.");
            }
        } else {
            if (minimumIndex == -1) {
                CraftLegacy.init();
                Bukkit.getLogger().log(Level.WARNING, "Legacy plugin " + pdf.getFullName() + " does not specify an api-version.");
            } else {
                throw new InvalidPluginException("Plugin API version " + pdf.getAPIVersion() + " is lower than the minimum allowed version. Please update or replace it.");
            }
        }
    }

    public static boolean isLegacy(PluginDescriptionFile pdf) {
        return pdf.getAPIVersion() == null;
    }

    @Override
    public byte[] processClass(PluginDescriptionFile pdf, String path, byte[] clazz) {
        try {
            clazz = Commodore.convert(clazz, !CraftMagicNumbers.isLegacy(pdf));
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Fatal error trying to convert " + pdf.getFullName() + ":" + path, ex);
        }

        return clazz;
    }

    // Paper start
    @Override
    public String getTimingsServerName() {
        return com.destroystokyo.paper.PaperConfig.timingsServerName;
    }

    @Override
    public com.destroystokyo.paper.util.VersionFetcher getVersionFetcher() {
        return new com.destroystokyo.paper.PaperVersionFetcher();
    }

    @Override
    public boolean isSupportedApiVersion(String apiVersion) {
        return apiVersion != null && SUPPORTED_API.contains(apiVersion);
    }

    @Override
    public byte[] serializeItem(ItemStack item) {
        Preconditions.checkNotNull(item, "null cannot be serialized");
        Preconditions.checkArgument(item.getType() != Material.AIR, "air cannot be serialized");

        java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream();
        CompoundTag compound = (item instanceof CraftItemStack ? ((CraftItemStack) item).handle : CraftItemStack.asNMSCopy(item)).save(new CompoundTag());
        compound.putInt("DataVersion", getDataVersion());
        try {
            net.minecraft.nbt.NbtIo.writeCompressed(
                compound,
                outputStream
            );
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        return outputStream.toByteArray();
    }

    @Override
    public ItemStack deserializeItem(byte[] data) {
        Preconditions.checkNotNull(data, "null cannot be deserialized");
        Preconditions.checkArgument(data.length > 0, "cannot deserialize nothing");

        try {
            CompoundTag compound = net.minecraft.nbt.NbtIo.readCompressed(
                new java.io.ByteArrayInputStream(data)
            );
            int dataVersion = compound.getInt("DataVersion");

            Preconditions.checkArgument(dataVersion <= getDataVersion(), "Newer version! Server downgrades are not supported!");
            Dynamic<Tag> converted = DataFixers.getDataFixer().update(References.ITEM_STACK, new Dynamic<Tag>(NbtOps.INSTANCE, compound), dataVersion, getDataVersion());
            return CraftItemStack.asCraftMirror(net.minecraft.world.item.ItemStack.of((CompoundTag) converted.getValue()));
        } catch (IOException ex) {
            com.destroystokyo.paper.util.SneakyThrow.sneaky(ex);
            throw new RuntimeException();
        }
    }

    @Override
    public String getTranslationKey(Material mat) {
        if (mat.isBlock()) {
            return getBlock(mat).getDescriptionId();
        }
        return getItem(mat).getDescriptionId();
    }

    @Override
    public String getTranslationKey(org.bukkit.block.Block block) {
        return ((org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock)block).getNMS().getBlock().getDescriptionId();
    }

    @Override
    public String getTranslationKey(org.bukkit.entity.EntityType type) {
        return net.minecraft.world.entity.EntityType.byString(type.getName()).map(net.minecraft.world.entity.EntityType::getDescriptionId).orElse(null);
    }

    @Override
    public String getTranslationKey(org.bukkit.inventory.ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsItemStack = CraftItemStack.asNMSCopy(itemStack);
        return nmsItemStack.getItem().getDescriptionId(nmsItemStack);
    }

    public int nextEntityId() {
        return net.minecraft.world.entity.Entity.nextEntityId();
    }

    @Override
    public io.papermc.paper.inventory.ItemRarity getItemRarity(org.bukkit.Material material) {
        Item item = getItem(material);
        if (item == null) {
            throw new IllegalArgumentException(material + " is not an item, and rarity does not apply to blocks");
        }
        return io.papermc.paper.inventory.ItemRarity.values()[item.rarity.ordinal()];
    }

    @Override
    public io.papermc.paper.inventory.ItemRarity getItemStackRarity(org.bukkit.inventory.ItemStack itemStack) {
        return io.papermc.paper.inventory.ItemRarity.values()[getItem(itemStack.getType()).getRarity(CraftItemStack.asNMSCopy(itemStack)).ordinal()];
    }

    @Override
    public boolean isValidRepairItemStack(org.bukkit.inventory.ItemStack itemToBeRepaired, org.bukkit.inventory.ItemStack repairMaterial) {
        if (!itemToBeRepaired.getType().isItem() || !repairMaterial.getType().isItem()) {
            return false;
        }
        return CraftMagicNumbers.getItem(itemToBeRepaired.getType()).isValidRepairItem(CraftItemStack.asNMSCopy(itemToBeRepaired), CraftItemStack.asNMSCopy(repairMaterial));
    }

    @Override
    public com.google.common.collect.Multimap<org.bukkit.attribute.Attribute, org.bukkit.attribute.AttributeModifier> getItemAttributes(org.bukkit.Material material, org.bukkit.inventory.EquipmentSlot equipmentSlot) {
        Item item = CraftMagicNumbers.getItem(material);
        if (item == null) {
            throw new IllegalArgumentException(material + " is not an item and therefore does not have attributes");
        }
        com.google.common.collect.ImmutableMultimap.Builder<org.bukkit.attribute.Attribute, org.bukkit.attribute.AttributeModifier> attributeMapBuilder = com.google.common.collect.ImmutableMultimap.builder();
        item.getDefaultAttributeModifiers(org.bukkit.craftbukkit.v1_17_R1.CraftEquipmentSlot.getNMS(equipmentSlot)).forEach((attributeBase, attributeModifier) -> {
            attributeMapBuilder.put(org.bukkit.Registry.ATTRIBUTE.get(CraftNamespacedKey.fromMinecraft(net.minecraft.core.Registry.ATTRIBUTE.getKey(attributeBase))), org.bukkit.craftbukkit.v1_17_R1.attribute.CraftAttributeInstance.convert(attributeModifier));
        });
        return attributeMapBuilder.build();
    }

    @Override
    public int getProtocolVersion() {
        return net.minecraft.SharedConstants.getCurrentVersion().getProtocolVersion();
    }
    // Paper end

    /**
     * This helper class represents the different NBT Tags.
     * <p>
     * These should match NBTBase#getTypeId
     */
    public static class NBT {

        public static final int TAG_END = 0;
        public static final int TAG_BYTE = 1;
        public static final int TAG_SHORT = 2;
        public static final int TAG_INT = 3;
        public static final int TAG_LONG = 4;
        public static final int TAG_FLOAT = 5;
        public static final int TAG_DOUBLE = 6;
        public static final int TAG_BYTE_ARRAY = 7;
        public static final int TAG_STRING = 8;
        public static final int TAG_LIST = 9;
        public static final int TAG_COMPOUND = 10;
        public static final int TAG_INT_ARRAY = 11;
        public static final int TAG_ANY_NUMBER = 99;
    }
}
