package com.brandon.evokeronlyraid;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

public final class ModItems {

    public static final Item EVOKERS_OMEN_I =
            registerOmen("evokers_omen_1", 0);

    public static final Item EVOKERS_OMEN_II =
            registerOmen("evokers_omen_2", 1);

    public static final Item EVOKERS_OMEN_III =
            registerOmen("evokers_omen_3", 2);

    public static final Item EVOKERS_OMEN_IV =
            registerOmen("evokers_omen_4", 3);

    public static final Item EVOKERS_OMEN_V =
            registerOmen("evokers_omen_5", 4);

    private static Item registerOmen(
            String name,
            int amplifier
    ) {
        ResourceKey<Item> itemKey =
                ResourceKey.create(
                        Registries.ITEM,
                        ResourceLocation.fromNamespaceAndPath(
                                EvokerOnlyRaid.MOD_ID,
                                name
                        )
                );

        Consumable evokersOmenDrink =
                Consumables.defaultDrink()
                        .soundAfterConsume(
                                SoundEvents.OMINOUS_BOTTLE_DISPOSE
                        )
                        .onConsume(
                                new ApplyStatusEffectsConsumeEffect(
                                        new MobEffectInstance(
                                                ModEffects.EVOKERS_OMEN,
                                                18000,
                                                amplifier
                                        )
                                )
                        )
                        .build();

        Item item =
                new EvokersOmenItem(
                        new Item.Properties()
                                .setId(itemKey)
                                .stacksTo(64)
                                .usingConvertsTo(
                                        Items.GLASS_BOTTLE
                                )
                                .component(
                                        DataComponents.ITEM_NAME,
                                        Component.translatable(
                                                "item.evokers-omen."
                                                        + name
                                        )
                                )
                                .component(
                                        DataComponents.CONSUMABLE,
                                        evokersOmenDrink
                                )
                );

        return Registry.register(
                BuiltInRegistries.ITEM,
                itemKey,
                item
        );
    }

    public static void register() {

        // Put the actual Evoker's Omen items in Food & Drinks.
        ItemGroupEvents.modifyEntriesEvent(
                CreativeModeTabs.FOOD_AND_DRINKS
        ).register(output -> {

            output.accept(EVOKERS_OMEN_I);
            output.accept(EVOKERS_OMEN_II);
            output.accept(EVOKERS_OMEN_III);
            output.accept(EVOKERS_OMEN_IV);
            output.accept(EVOKERS_OMEN_V);

            // Hide automatic Splash and Lingering Essence variants.
            output.getDisplayStacks().removeIf(
                    ModItems::isUnwantedEssencePotionVariant
            );

            output.getSearchTabStacks().removeIf(
                    ModItems::isUnwantedEssencePotionVariant
            );
        });

        // Hide automatic Essence Tipped Arrows from Combat.
        ItemGroupEvents.modifyEntriesEvent(
                CreativeModeTabs.COMBAT
        ).register(output -> {

            output.getDisplayStacks().removeIf(
                    ModItems::isUnwantedEssenceArrow
            );

            output.getSearchTabStacks().removeIf(
                    ModItems::isUnwantedEssenceArrow
            );
        });

        EvokerOnlyRaid.LOGGER.info(
                "Registering Evoker's Omen items."
        );
    }

    private static boolean isUnwantedEssencePotionVariant(
            ItemStack stack
    ) {
        if (!stack.is(Items.SPLASH_POTION)
                && !stack.is(Items.LINGERING_POTION)) {
            return false;
        }

        return hasEvokersOmenEssence(stack);
    }

    private static boolean isUnwantedEssenceArrow(
            ItemStack stack
    ) {
        if (!stack.is(Items.TIPPED_ARROW)) {
            return false;
        }

        return hasEvokersOmenEssence(stack);
    }

    private static boolean hasEvokersOmenEssence(
            ItemStack stack
    ) {
        PotionContents contents =
                stack.get(DataComponents.POTION_CONTENTS);

        if (contents == null
                || contents.potion().isEmpty()) {
            return false;
        }

        Holder<Potion> potion =
                contents.potion().get();

        return potion.equals(ModPotions.EVOKERS_OMEN_I_BASE)
                || potion.equals(ModPotions.EVOKERS_OMEN_II_BASE)
                || potion.equals(ModPotions.EVOKERS_OMEN_III_BASE)
                || potion.equals(ModPotions.EVOKERS_OMEN_III_EMERALD)
                || potion.equals(ModPotions.EVOKERS_OMEN_III_BANNER)
                || potion.equals(ModPotions.EVOKERS_OMEN_IV_BASE)
                || potion.equals(ModPotions.EVOKERS_OMEN_V_BASE);
    }

    private ModItems() {
    }
}