package io.papermc.paper.console;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.dedicated.DedicatedServer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.org.bukkit.craftbukkit.libs.jline.reader.Highlighter;
import org.org.bukkit.craftbukkit.libs.jline.reader.LineReader;
import org.org.bukkit.craftbukkit.libs.jline.utils.AttributedString;
import org.org.bukkit.craftbukkit.libs.jline.utils.AttributedStringBuilder;
import org.org.bukkit.craftbukkit.libs.jline.utils.AttributedStyle;

public final class BrigadierCommandHighlighter implements Highlighter {
    private static final int[] COLORS = {AttributedStyle.CYAN, AttributedStyle.YELLOW, AttributedStyle.GREEN, AttributedStyle.MAGENTA, /* Client uses GOLD here, not BLUE, however there is no GOLD AttributedStyle. */ AttributedStyle.BLUE};
    private final CommandSourceStack commandSourceStack;
    private final DedicatedServer server;

    public BrigadierCommandHighlighter(final @NonNull DedicatedServer server, final @NonNull CommandSourceStack commandSourceStack) {
        this.server = server;
        this.commandSourceStack = commandSourceStack;
    }

    @Override
    public AttributedString highlight(final @NonNull LineReader reader, final @NonNull String buffer) {
        final AttributedStringBuilder builder = new AttributedStringBuilder();
        final ParseResults<CommandSourceStack> results = this.server.getCommands().getDispatcher().parse(BrigadierCommandCompleter.prepareStringReader(buffer), this.commandSourceStack);
        int pos = 0;
        if (buffer.startsWith("/")) {
            builder.append("/", AttributedStyle.DEFAULT);
            pos = 1;
        }
        int component = -1;
        for (final ParsedCommandNode<CommandSourceStack> node : results.getContext().getLastChild().getNodes()) {
            if (node.getRange().getStart() >= buffer.length()) {
                break;
            }
            final int start = node.getRange().getStart();
            final int end = Math.min(node.getRange().getEnd(), buffer.length());
            builder.append(buffer.substring(pos, start), AttributedStyle.DEFAULT);
            if (node.getNode() instanceof LiteralCommandNode) {
                builder.append(buffer.substring(start, end), AttributedStyle.DEFAULT);
            } else {
                if (++component >= COLORS.length) {
                    component = 0;
                }
                builder.append(buffer.substring(start, end), AttributedStyle.DEFAULT.foreground(COLORS[component]));
            }
            pos = end;
        }
        if (pos < buffer.length()) {
            builder.append((buffer.substring(pos)), AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        }
        return builder.toAttributedString();
    }
}
