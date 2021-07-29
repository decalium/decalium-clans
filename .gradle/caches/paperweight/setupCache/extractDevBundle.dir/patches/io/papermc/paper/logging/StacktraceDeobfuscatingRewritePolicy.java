package io.papermc.paper.logging;

import io.papermc.paper.util.StacktraceDeobfuscator;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.jetbrains.annotations.NotNull;

@Plugin(
    name = "StacktraceDeobfuscatingRewritePolicy",
    category = Core.CATEGORY_NAME,
    elementType = "rewritePolicy",
    printObject = true
)
public final class StacktraceDeobfuscatingRewritePolicy implements RewritePolicy {
    @Override
    public @NotNull LogEvent rewrite(final @NotNull LogEvent rewrite) {
        final Throwable thrown = rewrite.getThrown();
        if (thrown != null) {
            StacktraceDeobfuscator.INSTANCE.deobfuscateThrowable(thrown);
        }
        return rewrite;
    }

    @PluginFactory
    public static @NotNull StacktraceDeobfuscatingRewritePolicy createPolicy() {
        return new StacktraceDeobfuscatingRewritePolicy();
    }
}
