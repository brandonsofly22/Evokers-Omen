package com.brandon.evokeronlyraid;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;

public final class ModItems {
    public static final Item EVOKERS_OMEN_I = registerOmen("evokers_omen_1", 0);
    public static final Item EVOKERS_OMEN_II = registerOmen("evokers_omen_2", 1);
    public static final Item EVOKERS_OMEN_III = registerOmen("evokers_omen_3", 2);
    public static final Item EVOKERS_OMEN_IV = registerOmen("evokers_omen_4", 3);
    public static final Item EVOKERS_OMEN_V = registerOmen("evokers_omen_5", 4);

    private static Item registerOmen(String name, int amplifier) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(EvokerOnlyRaid.MOD_ID, name);
        Item item = new EvokersOmenItem(new Item.Properties().stacksTo(64).component(DataComponents.ITEM_NAME, Component.translatable("item.evokers-omen." + name)), amplifier);
        return Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    private static final class EvokersOmenItem extends Item {
        private final int amplifier;
        private EvokersOmenItem(Properties properties, int amplifier) { super(properties); this.amplifier = amplifier; }

        @Override
        public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
            if (livingEntity instanceof ServerPlayer player) {
                CriteriaTriggers.CONSUME_ITEM.trigger(player, stack);
                player.awardStat(Stats.ITEM_USED.get(this));
            }
            if (!level.isClientSide) {
                level.playSound(null, livingEntity.blockPosition(), SoundEvents.OMINOUS_BOTTLE_DISPOSE, livingEntity.getSoundSource(), 1.0F, 1.0F);
                livingEntity.addEffect(new MobEffectInstance(ModEffects.EVOKERS_OMEN, 18000, amplifier));
            }
            if (livingEntity instanceof Player player) {
                stack.consume(1, player);
                if (!player.hasInfiniteMaterials()) {
                    if (stack.isEmpty()) return new ItemStack(Items.GLASS_BOTTLE);
                    player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
                }
            } else stack.shrink(1);
            return stack;
        }

        @Override public int getUseDuration(ItemStack stack, LivingEntity livingEntity) { return 32; }
        @Override public UseAnim getUseAnimation(ItemStack stack) { return UseAnim.DRINK; }
        @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) { return ItemUtils.startUsingInstantly(level, player, hand); }
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FOOD_AND_DRINKS).register(output -> {
            output.accept(EVOKERS_OMEN_I); output.accept(EVOKERS_OMEN_II); output.accept(EVOKERS_OMEN_III); output.accept(EVOKERS_OMEN_IV); output.accept(EVOKERS_OMEN_V);
            output.getDisplayStacks().removeIf(ModItems::isUnwantedEssencePotionVariant);
            output.getSearchTabStacks().removeIf(ModItems::isUnwantedEssencePotionVariant);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT).register(output -> {
            output.getDisplayStacks().removeIf(ModItems::isUnwantedEssenceArrow);
            output.getSearchTabStacks().removeIf(ModItems::isUnwantedEssenceArrow);
        });
        EvokerOnlyRaid.LOGGER.info("Registering Evoker's Omen items.");
    }

    private static boolean isUnwantedEssencePotionVariant(ItemStack stack) {
        if (!stack.is(Items.SPLASH_POTION) && !stack.is(Items.LINGERING_POTION)) return false;
        return hasEvokersOmenEssence(stack);
    }
    private static boolean isUnwantedEssenceArrow(ItemStack stack) {
        if (!stack.is(Items.TIPPED_ARROW)) return false;
        return hasEvokersOmenEssence(stack);
    }
    private static boolean hasEvokersOmenEssence(ItemStack stack) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null || contents.potion().isEmpty()) return false;
        Holder<Potion> potion = contents.potion().get();
        return potion.equals(ModPotions.EVOKERS_OMEN_I_BASE) || potion.equals(ModPotions.EVOKERS_OMEN_II_BASE) || potion.equals(ModPotions.EVOKERS_OMEN_III_BASE) || potion.equals(ModPotions.EVOKERS_OMEN_III_EMERALD) || potion.equals(ModPotions.EVOKERS_OMEN_III_BANNER) || potion.equals(ModPotions.EVOKERS_OMEN_IV_BASE) || potion.equals(ModPotions.EVOKERS_OMEN_V_BASE);
    }
    private ModItems() {}
}