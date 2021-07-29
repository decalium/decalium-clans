package io.papermc.paper.util;

import com.destroystokyo.paper.PaperConfig;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.framework.qual.DefaultQualifier;
import org.bukkit.craftbukkit.libs.org.objectweb.asm.ClassReader;
import org.bukkit.craftbukkit.libs.org.objectweb.asm.ClassVisitor;
import org.bukkit.craftbukkit.libs.org.objectweb.asm.Label;
import org.bukkit.craftbukkit.libs.org.objectweb.asm.MethodVisitor;
import org.bukkit.craftbukkit.libs.org.objectweb.asm.Opcodes;

@DefaultQualifier(NonNull.class)
public enum StacktraceDeobfuscator {
    INSTANCE;

    private final Map<Class<?>, Map<Pair<String, String>, IntList>> lineMapCache = Collections.synchronizedMap(new LinkedHashMap<>(128, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(final Map.Entry<Class<?>, Map<Pair<String, String>, IntList>> eldest) {
            return this.size() > 127;
        }
    });

    public void deobfuscateThrowable(final Throwable throwable) {
        if (!PaperConfig.deobfuscateStacktraces) {
            return;
        }

        throwable.setStackTrace(this.deobfuscateStacktrace(throwable.getStackTrace()));
        final Throwable cause = throwable.getCause();
        if (cause != null) {
            this.deobfuscateThrowable(cause);
        }
        for (final Throwable suppressed : throwable.getSuppressed()) {
            this.deobfuscateThrowable(suppressed);
        }
    }

    public StackTraceElement[] deobfuscateStacktrace(final StackTraceElement[] traceElements) {
        if (!PaperConfig.deobfuscateStacktraces) {
            return traceElements;
        }

        final @Nullable Map<String, ObfHelper.ClassMapping> mappings = ObfHelper.INSTANCE.mappings();
        if (mappings == null || traceElements.length == 0) {
            return traceElements;
        }
        final StackTraceElement[] result = new StackTraceElement[traceElements.length];
        for (int i = 0; i < traceElements.length; i++) {
            final StackTraceElement element = traceElements[i];

            final String className = element.getClassName();
            final String methodName = element.getMethodName();

            final ObfHelper.ClassMapping classMapping = mappings.get(className);
            if (classMapping == null) {
                result[i] = element;
                continue;
            }

            final Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (final ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            final @Nullable Pair<String, String> nameDescriptorPair = this.determineMethodForLine(clazz, element.getLineNumber());
            final ObfHelper.@Nullable MethodMapping methodMapping = nameDescriptorPair == null
                ? null
                : classMapping.methodMappings().get(nameDescriptorPair);

            result[i] = new StackTraceElement(
                element.getClassLoaderName(),
                element.getModuleName(),
                element.getModuleVersion(),
                classMapping.mojangName(),
                methodMapping != null ? methodMapping.mojangName() : methodName,
                sourceFileName(classMapping.mojangName()),
                element.getLineNumber()
            );
        }
        return result;
    }

    private @Nullable Pair<String, String> determineMethodForLine(final Class<?> clazz, final int lineNumber) {
        final Map<Pair<String, String>, IntList> lineMap = this.lineMapCache.computeIfAbsent(clazz, StacktraceDeobfuscator::buildLineMap);
        for (final var entry : lineMap.entrySet()) {
            final Pair<String, String> pair = entry.getKey();
            final IntList lines = entry.getValue();
            for (int i = 0, linesSize = lines.size(); i < linesSize; i++) {
                final int num = lines.getInt(i);
                if (num == lineNumber) {
                    return pair;
                }
            }
        }
        return null;
    }

    private static String sourceFileName(final String fullClassName) {
        final int dot = fullClassName.lastIndexOf('.');
        final String className = dot == -1
            ? fullClassName
            : fullClassName.substring(dot + 1);
        final String rootClassName = className.split("\\$")[0];
        return rootClassName + ".java";
    }

    private static Map<Pair<String, String>, IntList> buildLineMap(final Class<?> key) {
        final Map<Pair<String, String>, IntList> lineMap = new HashMap<>();
        final class LineCollectingMethodVisitor extends MethodVisitor {
            private final IntList lines = new IntArrayList();
            private final String name;
            private final String descriptor;

            LineCollectingMethodVisitor(String name, String descriptor) {
                super(Opcodes.ASM9);
                this.name = name;
                this.descriptor = descriptor;
            }

            @Override
            public void visitLineNumber(int line, Label start) {
                super.visitLineNumber(line, start);
                this.lines.add(line);
            }

            @Override
            public void visitEnd() {
                super.visitEnd();
                lineMap.put(new Pair<>(this.name, this.descriptor), this.lines);
            }
        }
        final ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                return new LineCollectingMethodVisitor(name, descriptor);
            }
        };
        try {
            final ClassReader reader = new ClassReader(key.getName());
            reader.accept(classVisitor, 0);
        } catch (final IOException ex) {
            throw new RuntimeException(ex);
        }
        return lineMap;
    }
}
