package org.bukkit.craftbukkit.v1_17_R1.inventory;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.apache.commons.lang.Validate;

public class CraftMerchantCustom extends CraftMerchant {

    @Deprecated // Paper - Adventure
    public CraftMerchantCustom(String title) {
        super(new MinecraftMerchant(title));
        this.getMerchant().craftMerchant = this;
    }
    // Paper start
    public CraftMerchantCustom(net.kyori.adventure.text.Component title) {
        super(new MinecraftMerchant(title));
        getMerchant().craftMerchant = this;
    }
    // Paper end

    @Override
    public String toString() {
        return "CraftMerchantCustom";
    }

    @Override
    public MinecraftMerchant getMerchant() {
        return (MinecraftMerchant) super.getMerchant();
    }

    public static class MinecraftMerchant implements Merchant {

        private final Component title;
        private final MerchantOffers trades = new MerchantOffers();
        private Player tradingPlayer;
        private Level tradingWorld;
        protected CraftMerchant craftMerchant;

        @Deprecated // Paper - Adventure
        public MinecraftMerchant(String title) {
            Validate.notNull(title, "Title cannot be null");
            this.title = new TextComponent(title);
        }
        // Paper start
        public MinecraftMerchant(net.kyori.adventure.text.Component title) {
            Validate.notNull(title, "Title cannot be null");
            this.title = io.papermc.paper.adventure.PaperAdventure.asVanilla(title);
        }
        // Paper end

        @Override
        public CraftMerchant getCraftMerchant() {
            return this.craftMerchant;
        }

        @Override
        public void setTradingPlayer(Player customer) {
            this.tradingPlayer = customer;
            if (customer != null) {
                this.tradingWorld = customer.level;
            }
        }

        @Override
        public Player getTradingPlayer() {
            return this.tradingPlayer;
        }

        @Override
        public MerchantOffers getOffers() {
            return this.trades;
        }

        @Override
        public void notifyTrade(MerchantOffer offer) {
            // Paper start
            /** Based on {@link net.minecraft.world.entity.npc.EntityVillagerAbstract#b(MerchantRecipe)} */
            if (getTradingPlayer() instanceof net.minecraft.server.level.ServerPlayer) {
                final net.minecraft.server.level.ServerPlayer trader = (net.minecraft.server.level.ServerPlayer) getTradingPlayer();
                final io.papermc.paper.event.player.PlayerPurchaseEvent event = new io.papermc.paper.event.player.PlayerPurchaseEvent(
                    trader.getBukkitEntity(),
                    offer.asBukkit(),
                    false, // reward xp?
                    true); // should increase uses?
                event.callEvent();
                if (event.isCancelled()) {
                    return;
                }
                final org.bukkit.inventory.MerchantRecipe eventTrade = event.getTrade();
                if (event.willIncreaseTradeUses()) {
                    eventTrade.setUses(eventTrade.getUses() + 1);
                }
                if (event.isRewardingExp() && eventTrade.hasExperienceReward()) {
                    /** Based on {@link net.minecraft.world.entity.npc.EntityVillagerTrader#b(MerchantRecipe)} */
                    final int xp = 3 + net.minecraft.world.entity.Entity.SHARED_RANDOM.nextInt(4);
                    final Level world = trader.getCommandSenderWorld();
                    world.addFreshEntity(new net.minecraft.world.entity.ExperienceOrb(
                        world, trader.getX(), trader.getY() + 0.5d, trader.getZ(), xp,
                        org.bukkit.entity.ExperienceOrb.SpawnReason.VILLAGER_TRADE, trader, null));
                }
                return;
            }
            // Paper end

            // increase recipe's uses
            offer.increaseUses();
        }

        @Override
        public void notifyTradeUpdated(ItemStack stack) {
        }

        public Component getScoreboardDisplayName() {
            return this.title;
        }

        @Override
        public Level getLevel() {
            return this.tradingWorld;
        }

        @Override
        public int getVillagerXp() {
            return 0; // xp
        }

        @Override
        public void overrideXp(int experience) {
        }

        @Override
        public boolean showProgressBar() {
            return false; // is-regular-villager flag (hides some gui elements: xp bar, name suffix)
        }

        @Override
        public SoundEvent getNotifyTradeSound() {
            return SoundEvents.VILLAGER_YES;
        }

        @Override
        public void overrideOffers(MerchantOffers offers) {
        }
    }
}
