package com.brandon.evokeronlyraid.mixin;

import com.brandon.evokeronlyraid.ModPotions;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import net.minecraft.world.item.crafting.BrewingInput;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatterns;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BrewingRecipe.class)
public abstract class PotionBrewingMixin {

    @Inject(
            method = "matches(Lnet/minecraft/world/item/crafting/BrewingInput;)Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private void evokerOnlyRaid$validateSpecialIngredients(
            BrewingInput brewingInput,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!cir.getReturnValue()) {
            return;
        }

        BrewingRecipe recipe =
                (BrewingRecipe) (Object) this;

        ItemStack source = brewingInput.input();
        ItemStack reagent = brewingInput.reagent();
        ItemStack output = recipe.getOutput().create();

        if (source.is(Items.POTION)
                && reagent.is(Items.OMINOUS_BOTTLE)) {

            if (evokerOnlyRaid$isPotion(
                    output,
                    ModPotions.EVOKERS_OMEN_I_BASE
            )) {
                cir.setReturnValue(
                        evokerOnlyRaid$hasOminousBottleAmplifier(
                                reagent,
                                0
                        )
                );
                return;
            }

            if (evokerOnlyRaid$isPotion(
                    output,
                    ModPotions.EVOKERS_OMEN_II_BASE
            )) {
                cir.setReturnValue(
                        evokerOnlyRaid$hasOminousBottleAmplifier(
                                reagent,
                                1
                        )
                );
                return;
            }

            if (evokerOnlyRaid$isPotion(
                    output,
                    ModPotions.EVOKERS_OMEN_III_BASE
            )) {
                cir.setReturnValue(
                        evokerOnlyRaid$hasOminousBottleAmplifier(
                                reagent,
                                2
                        )
                );
                return;
            }
        }

        if (reagent.is(Items.BANNER.white())
                && evokerOnlyRaid$isEvokersOmenBrewingStage(source)) {

            cir.setReturnValue(
                    evokerOnlyRaid$isOminousBanner(reagent)
            );
        }
    }

    @Unique
    private static boolean evokerOnlyRaid$hasOminousBottleAmplifier(
            ItemStack stack,
            int requiredAmplifier
    ) {
        OminousBottleAmplifier amplifier =
                stack.get(
                        DataComponents.OMINOUS_BOTTLE_AMPLIFIER
                );

        return amplifier != null
                && amplifier.value() == requiredAmplifier;
    }

    @Unique
    private static boolean evokerOnlyRaid$isEvokersOmenBrewingStage(
            ItemStack stack
    ) {
        return evokerOnlyRaid$isPotion(
                stack,
                ModPotions.EVOKERS_OMEN_II_BASE
        )
                || evokerOnlyRaid$isPotion(
                stack,
                ModPotions.EVOKERS_OMEN_III_BASE
        )
                || evokerOnlyRaid$isPotion(
                stack,
                ModPotions.EVOKERS_OMEN_III_EMERALD
        );
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
        if (!stack.is(Items.BANNER.white())) {
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
