package org.gepron1x.clans.gui.item;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import java.util.List;
import java.util.function.Predicate;

public final class BlockProtection implements Listener {

	private final Predicate<Block> predicate;

	public BlockProtection(Predicate<Block> predicate) {

		this.predicate = predicate;
	}

	private void checkList(List<Block> blocks) {
		blocks.removeIf(predicate);
	}

	public void checkBlock(Cancellable event, Block block) {
		if (predicate.test(block)) event.setCancelled(true);
	}

	public <E extends BlockEvent & Cancellable> void checkBlock(E event) {
		checkBlock(event, event.getBlock());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void on(BlockBreakEvent event) {
		checkBlock(event);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void on(EntityChangeBlockEvent event) {
		checkBlock(event, event.getBlock());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void on(EntityExplodeEvent event) {
		checkList(event.blockList());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void on(BlockExplodeEvent event) {
		checkList(event.blockList());
	}


}
