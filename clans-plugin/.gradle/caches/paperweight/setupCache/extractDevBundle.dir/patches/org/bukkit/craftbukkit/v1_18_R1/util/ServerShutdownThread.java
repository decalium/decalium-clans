package org.bukkit.craftbukkit.v1_18_R1.util;

import net.minecraft.server.MinecraftServer;

public class ServerShutdownThread extends Thread {
    private final MinecraftServer server;

    public ServerShutdownThread(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            // Paper start - try to shutdown on main
            server.safeShutdown(false, false);
            for (int i = 1000; i > 0 && !server.hasStopped(); i -= 100) {
                Thread.sleep(100);
            }
            if (server.hasStopped()) {
                while (!server.hasFullyShutdown) Thread.sleep(1000);
                return;
            }
            // Looks stalled, close async
            org.spigotmc.AsyncCatcher.enabled = false; // Spigot
            org.spigotmc.AsyncCatcher.shuttingDown = true; // Paper
            server.forceTicks = true;
            this.server.close();
            while (!server.hasFullyShutdown) Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            // Paper end
        } finally {
            try {
                //net.minecrell.terminalconsole.TerminalConsoleAppender.close(); // Paper - Move into stop
            } catch (Exception e) {
            }
        }
    }
}
