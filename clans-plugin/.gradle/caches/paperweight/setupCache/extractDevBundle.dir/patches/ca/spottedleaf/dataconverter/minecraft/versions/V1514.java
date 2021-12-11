package ca.spottedleaf.dataconverter.minecraft.versions;

import ca.spottedleaf.dataconverter.converters.DataConverter;
import ca.spottedleaf.dataconverter.minecraft.MCVersions;
import ca.spottedleaf.dataconverter.minecraft.datatypes.MCTypeRegistry;
import ca.spottedleaf.dataconverter.types.MapType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public final class V1514 {

    protected static final int VERSION = MCVersions.V1_13_PRE7 + 1;

    private V1514() {}

    public static void register() {
        MCTypeRegistry.OBJECTIVE.addStructureConverter(new DataConverter<>(VERSION) {
            @Override
            public MapType<String> convert(final MapType<String> data, final long sourceVersion, final long toVersion) {
                final String displayName = data.getString("DisplayName");
                if (displayName == null) {
                    return null;
                }

                final String update = Component.Serializer.toJson(new TextComponent(displayName));

                data.setString("DisplayName", update);

                return null;
            }
        });

        MCTypeRegistry.TEAM.addStructureConverter(new DataConverter<>(VERSION) {
            @Override
            public MapType<String> convert(final MapType<String> data, final long sourceVersion, final long toVersion) {
                final String displayName = data.getString("DisplayName");
                if (displayName == null) {
                    return null;
                }

                final String update = Component.Serializer.toJson(new TextComponent(displayName));

                data.setString("DisplayName", update);

                return null;
            }
        });

        MCTypeRegistry.OBJECTIVE.addStructureConverter(new DataConverter<>(VERSION) {
            private static ObjectiveCriteria.RenderType getRenderType(String string) {
                return string.equals("health") ? ObjectiveCriteria.RenderType.HEARTS : ObjectiveCriteria.RenderType.INTEGER;
            }

            @Override
            public MapType<String> convert(final MapType<String> data, final long sourceVersion, final long toVersion) {
                final String renderType = data.getString("RenderType");
                if (renderType != null) {
                    return null;
                }

                final String criteriaName = data.getString("CriteriaName", "");

                data.setString("RenderType", getRenderType(criteriaName).getId());

                return null;
            }
        });
    }
}
