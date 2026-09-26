package com.brandon.evokeronlyraid.mixin;

import com.brandon.evokeronlyraid.ModItems;
import com.brandon.evokeronlyraid.ModPotions;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatterns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PotionBrewing.class)
public abstract class PotionBrewingMixin {

    @Inject(
            method = "isIngredient",
            at = @At("HEAD"),
            cancellable = true
    )
    private void evokerOnlyRaid$isIngredient(
            ItemStack ingredient,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (ingredient.is(Items.EMERALD)
                || ingredient.is(Items.WHITE_BANNER)
                || ingredient.is(Items.EMERALD_BLOCK)
                || ingredient.is(Items.TOTEM_OF_UNDYING)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
            method = "hasMix",
            at = @At("HEAD"),
            cancellable = true
    )
    private void evokerOnlyRaid$hasMix(
            ItemStack source,
            ItemStack ingredient,
            CallbackInfoReturnable<Boolean> cir
    ) {
        // Reject Ominous Bottle IV and V when used with Awkward Potion.
        if (evokerOnlyRaid$isAwkwardPotion(source)
                && ingredient.is(Items.OMINOUS_BOTTLE)) {

            OminousBottleAmplifier amplifier =
                    ingredient.get(
                            DataComponents.OMINOUS_BOTTLE_AMPLIFIER
                    );

            if (amplifier != null
                    && amplifier.value() >= 3) {
                cir.setReturnValue(false);
                return;
            }
        }

        // LEVEL I
        if (evokerOnlyRaid$isPotion(
                source,
                ModPotions.EVOKERS_OMEN_I_BASE
        )) {
            cir.setReturnValue(
                    ingredient.is(Items.EMERALD)
            );
            return;
        }

        // LEVEL II
        if (evokerOnlyRaid$isPotion(
                source,
                ModPotions.EVOKERS_OMEN_II_BASE
        )) {
            cir.setReturnValue(
                    evokerOnlyRaid$isOminousBanner(ingredient)
            );
            return;
        }

        // LEVEL III - First ingredient can be Emerald OR Ominous Banner.
        if (evokerOnlyRaid$isPotion(
                source,
                ModPotions.EVOKERS_OMEN_III_BASE
        )) {
            cir.setReturnValue(
                    ingredient.is(Items.EMERALD)
                            || evokerOnlyRaid$isOminousBanner(ingredient)
            );
            return;
        }

        // LEVEL III - Emerald was added first, so Banner is required.
        if (evokerOnlyRaid$isPotion(
                source,
                ModPotions.EVOKERS_OMEN_III_EMERALD
        )) {
            cir.setReturnValue(
                    evokerOnlyRaid$isOminousBanner(ingredient)
            );
            return;
        }

        // LEVEL III - Banner was added first, so Emerald is required.
        if (evokerOnlyRaid$isPotion(
                source,
                ModPotions.EVOKERS_OMEN_III_BANNER
        )) {
            cir.setReturnValue(
                    ingredient.is(Items.EMERALD)
            );
            return;
        }

        // LEVEL IV
        if (evokerOnlyRaid$isPotion(
                source,
                ModPotions.EVOKERS_OMEN_IV_BASE
        )) {
            cir.setReturnValue(
                    ingredient.is(Items.EMERALD_BLOCK)
            );
            return;
        }

        // LEVEL V
        if (evokerOnlyRaid$isPotion(
                source,
                ModPotions.EVOKERS_OMEN_V_BASE
        )) {
            cir.setReturnValue(
                    ingredient.is(Items.TOTEM_OF_UNDYING)
            );
        }
    }

    @Inject(
            method = "mix",
            at = @At("HEAD"),
            cancellable = true
    )
    private void evokerOnlyRaid$mix(
            ItemStack ingredient,
            ItemStack source,
            CallbackInfoReturnable<ItemStack> cir
    ) {
        // Ominous Bottle IV and V must not brew from Awkward Potion.
        if (evokerOnlyRaid$isAwkwardPotion(source)
                && ingredient.is(Items.OMINOUS_BOTTLE)) {

            OminousBottleAmplifier amplifier =
                    ingredient.get(
                            DataComponents.OMINOUS_BOTTLE_AMPLIFIER
                    );

            if (amplifier != null
                    && amplifier.value() >= 3) {
                cir.setReturnValue(source);
                return;
            }
        }

        // LEVEL I
        if (ingredient.is(Items.EMERALD)
                && evokerOnlyRaid$isPotion(
                source,
                ModPotions.EVOKERS_OMEN_I_BASE
        )) {
            cir.setReturnValue(
                    new ItemStack(ModItems.EVOKERS_OMEN_I)
            );
            return;
        }

        // LEVEL II
        if (evokerOnlyRaid$isOminousBanner(ingredient)
                && evokerOnlyRaid$isPotion(
                source,
                ModPotions.EVOKERS_OMEN_II_BASE
        )) {
            cir.setReturnValue(
                    new ItemStack(ModItems.EVOKERS_OMEN_II)
            );
            return;
        }

        // LEVEL III - Banner first.
        if (evokerOnlyRaid$isOminousBanner(ingredient)
                && evokerOnlyRaid$isPotion(
                source,
                ModPotions.EVOKERS_OMEN_III_BASE
        )) {
            cir.setReturnValue(
                    evokerOnlyRaid$createPotionStack(
                            source,
                            ModPotions.EVOKERS_OMEN_III_BANNER
                    )
            );
            return;
        }

        // LEVEL III - Emerald first, then Banner.
        if (evokerOnlyRaid$isOminousBanner(ingredient)
                && evokerOnlyRaid$isPotion(
                source,
                ModPotions.EVOKERS_OMEN_III_EMERALD
        )) {
            cir.setReturnValue(
                    new ItemStack(ModItems.EVOKERS_OMEN_III)
            );
            return;
        }

        // LEVEL III - Banner first, then Emerald.
        if (ingredient.is(Items.EMERALD)
                && evokerOnlyRaid$isPotion(
                source,
                ModPotions.EVOKERS_OMEN_III_BANNER
        )) {
            cir.setReturnValue(
                    new ItemStack(ModItems.EVOKERS_OMEN_III)
            );
            return;
        }

        // LEVEL IV
        if (ingredient.is(Items.EMERALD_BLOCK)
                && evokerOnlyRaid$isPotion(
                source,
                ModPotions.EVOKERS_OMEN_IV_BASE
        )) {
            cir.setReturnValue(
                    new ItemStack(ModItems.EVOKERS_OMEN_IV)
            );
            return;
        }

        // LEVEL V
        if (ingredient.is(Items.TOTEM_OF_UNDYING)
                && evokerOnlyRaid$isPotion(
                source,
                ModPotions.EVOKERS_OMEN_V_BASE
        )) {
            cir.setReturnValue(
                    new ItemStack(ModItems.EVOKERS_OMEN_V)
            );
        }
    }

    @Unique
    private static ItemStack evokerOnlyRaid$createPotionStack(
            ItemStack source,
            Holder<Potion> potion
    ) {
        ItemStack result = source.copy();
        result.set(
                DataComponents.POTION_CONTENTS,
                new PotionContents(potion)
        );
        return result;
    }

    @Unique
    private static boolean evokerOnlyRaid$isAwkwardPotion(
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

        return potion.unwrapKey()
                .map(key ->
                        key.location()
                                .getPath()
                                .equals("awkward")
                )
                .orElse(false);
    }

    @Unique
    private static boolean evokerOnlyRaid$isPotion(
            ItemStack stack,
            Holder<Potion> potion
    ) {
        PotionContents contents =
                stack.get(DataComponents.POTION_CONTENTS);

        return contents != null
                && contents.potion().isPresent()
                && contents.potion().get().equals(potion);
    }

    @Unique
    private static boolean evokerOnlyRaid$isOminousBanner(
            ItemStack stack
    ) {
        if (!stack.is(Items.WHITE_BANNER)) {
            return false;
        }

        BannerPatternLayers patterns =
                stack.get(DataComponents.BANNER_PATTERNS);

        if (patterns == null) {
            return false;
        }

        List<BannerPatternLayers.Layer> layers =
                patterns.layers();

        if (layers.size() != 8) {
            return false;
        }

        return layers.get(0).pattern().is(
                BannerPatterns.RHOMBUS_MIDDLE)
                && layers.get(0).color() == DyeColor.CYAN

                && layers.get(1).pattern().is(
                BannerPatterns.STRIPE_BOTTOM)
                && layers.get(1).color() == DyeColor.LIGHT_GRAY

                && layers.get(2).pattern().is(
                BannerPatterns.STRIPE_CENTER)
                && layers.get(2).color() == DyeColor.GRAY

                && layers.get(3).pattern().is(
                BannerPatterns.BORDER)
                && layers.get(3).color() == DyeColor.LIGHT_GRAY

                && layers.get(4).pattern().is(
                BannerPatterns.STRIPE_MIDDLE)
                && layers.get(4).color() == DyeColor.BLACK

                && layers.get(5).pattern().is(
                BannerPatterns.HALF_HORIZONTAL)
                && layers.get(5).color() == DyeColor.LIGHT_GRAY

                && layers.get(6).pattern().is(
                BannerPatterns.CIRCLE_MIDDLE)
                && layers.get(6).color() == DyeColor.LIGHT_GRAY

                && layers.get(7).pattern().is(
                BannerPatterns.BORDER)
                && layers.get(7).color() == DyeColor.BLACK;
    }
}