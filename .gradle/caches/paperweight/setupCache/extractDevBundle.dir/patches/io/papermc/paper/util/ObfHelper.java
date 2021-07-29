package io.papermc.paper.util;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import net.fabricmc.mapping.tree.ClassDef;
import net.fabricmc.mapping.tree.MethodDef;
import net.fabricmc.mapping.tree.TinyMappingFactory;
import net.fabricmc.mapping.tree.TinyTree;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;

@DefaultQualifier(NonNull.class)
public enum ObfHelper {
    INSTANCE;

    public static final String MOJANG_PLUS_YARN_NAMESPACE = "mojang+yarn";
    public static final String SPIGOT_NAMESPACE = "spigot";

    private final @Nullable Map<String, ClassMapping> mappings;

    ObfHelper() {
        this.mappings = loadMappingsIfPresent();
    }

    public @Nullable Map<String, ClassMapping> mappings() {
        return this.mappings;
    }

    private static @Nullable Map<String, ClassMapping> loadMappingsIfPresent() {
        try (final @Nullable InputStream mappingsInputStream = StacktraceDeobfuscator.class.getClassLoader().getResourceAsStream("META-INF/mappings/reobf.tiny")) {
            if (mappingsInputStream == null) {
                return null;
            }
            final TinyTree tree = TinyMappingFactory.loadWithDetection(new BufferedReader(new InputStreamReader(mappingsInputStream, Charsets.UTF_8)));
            final var builder = ImmutableMap.<String, ClassMapping>builder();

            for (final ClassDef classDef : tree.getClasses()) {
                final String obfClassName = classDef.getName(SPIGOT_NAMESPACE).replace('/', '.');
                final var methodMappings = ImmutableMap.<Pair<String, String>, MethodMapping>builder();

                for (final MethodDef methodDef : classDef.getMethods()) {
                    final MethodMapping method = new MethodMapping(
                        methodDef.getName(SPIGOT_NAMESPACE),
                        methodDef.getName(MOJANG_PLUS_YARN_NAMESPACE),
                        methodDef.getDescriptor(SPIGOT_NAMESPACE)
                    );
                    methodMappings.put(
                        new Pair<>(method.obfName(), method.descriptor()),
                        method
                    );
                }

                final ClassMapping map = new ClassMapping(
                    obfClassName,
                    classDef.getName(MOJANG_PLUS_YARN_NAMESPACE).replace('/', '.'),
                    methodMappings.build()
                );
                builder.put(map.obfName(), map);
            }

            return builder.build();
        } catch (final IOException ex) {
            System.err.println("Failed to load mappings for stacktrace deobfuscation.");
            ex.printStackTrace();
            return null;
        }
    }

    public record ClassMapping(
        String obfName,
        String mojangName,
        Map<Pair<String, String>, MethodMapping> methodMappings
    ) {}

    public record MethodMapping(
        String obfName,
        String mojangName,
        String descriptor
    ) {}
}
