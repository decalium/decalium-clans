package org.bukkit.craftbukkit.v1_18_R1.scheduler;

import java.util.function.Consumer;

import co.aikar.timings.NullTimingHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import org.spigotmc.CustomTimingsHandler; // Spigot
import co.aikar.timings.MinecraftTimings; // Paper
import co.aikar.timings.Timing; // Paper

public class CraftTask implements BukkitTask, Runnable { // Spigot

    private volatile CraftTask next = null;
    public static final int ERROR = 0;
    public static final int NO_REPEATING = -1;
    public static final int CANCEL = -2;
    public static final int PROCESS_FOR_FUTURE = -3;
    public static final int DONE_FOR_FUTURE = -4;
    /**
     * -1 means no repeating <br>
     * -2 means cancel <br>
     * -3 means processing for Future <br>
     * -4 means done for Future <br>
     * Never 0 <br>
     * >0 means number of ticks to wait between each execution
     */
    private volatile long period;
    private long nextRun;
    public final Runnable rTask; // Paper
    public final Consumer<BukkitTask> cTask; // Paper
    public Timing timings; // Paper
    private final Plugin plugin;
    private final int id;
    private final long createdAt = System.nanoTime();

    CraftTask() {
        this(null, null, CraftTask.NO_REPEATING, CraftTask.NO_REPEATING);
    }

    CraftTask(final Object task) {
        this(null, task, CraftTask.NO_REPEATING, CraftTask.NO_REPEATING);
    }
    // Paper start
    public String taskName = null;
    boolean internal = false;
    CraftTask(final Object task, int id, String taskName) {
        this.rTask = (Runnable) task;
        this.cTask = null;
        this.plugin = CraftScheduler.MINECRAFT;
        this.taskName = taskName;
        this.internal = true;
        this.id = id;
        this.period = CraftTask.NO_REPEATING;
        this.taskName = taskName;
        this.timings = MinecraftTimings.getInternalTaskName(taskName);
    }
    // Paper end

    CraftTask(final Plugin plugin, final Object task, final int id, final long period) {
        this.plugin = plugin;
        if (task instanceof Runnable) {
            this.rTask = (Runnable) task;
            this.cTask = null;
        } else if (task instanceof Consumer) {
            this.cTask = (Consumer<BukkitTask>) task;
            this.rTask = null;
        } else if (task == null) {
            // Head or Future task
            this.rTask = null;
            this.cTask = null;
        } else {
            throw new AssertionError("Illegal task class " + task);
        }
        this.id = id;
        this.period = period;
        timings = task != null ? MinecraftTimings.getPluginTaskTimings(this, period) : NullTimingHandler.NULL; // Paper
    }

    @Override
    public final int getTaskId() {
        return this.id;
    }

    @Override
    public final Plugin getOwner() {
        return this.plugin;
    }

    @Override
    public boolean isSync() {
        return true;
    }

    @Override
    public void run() {
        try (Timing ignored = timings.startTiming()) { // Paper
        if (this.rTask != null) {
            this.rTask.run();
        } else {
            this.cTask.accept(this);
        }
        } // Paper
    }

    long getCreatedAt() {
        return this.createdAt;
    }

    long getPeriod() {
        return this.period;
    }

    void setPeriod(long period) {
        this.period = period;
    }

    long getNextRun() {
        return this.nextRun;
    }

    void setNextRun(long nextRun) {
        this.nextRun = nextRun;
    }

    CraftTask getNext() {
        return this.next;
    }

    void setNext(CraftTask next) {
        this.next = next;
    }

    public Class<?> getTaskClass() { // Paper
        return (this.rTask != null) ? this.rTask.getClass() : ((this.cTask != null) ? this.cTask.getClass() : null);
    }

    @Override
    public boolean isCancelled() {
        return (this.period == CraftTask.CANCEL);
    }

    @Override
    public void cancel() {
        Bukkit.getScheduler().cancelTask(id);
    }

    /**
     * This method properly sets the status to cancelled, synchronizing when required.
     *
     * @return false if it is a craft future task that has already begun execution, true otherwise
     */
    boolean cancel0() {
        this.setPeriod(CraftTask.CANCEL);
        return true;
    }

}
