package com.brandon.evokeronlyraid;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.fabric.impl.recipe.ingredient.builtin.ComponentsIngredient;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.OminousBottleAmplifier;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

public final class ModBrewing {

    public static void register() {
        Ingredient ominousBottleI =
                createOminousBottleIngredient(0);

        Ingredient ominousBottleII =
                createOminousBottleIngredient(1);

        Ingredient ominousBottleIII =
                createOminousBottleIngredient(2);

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {

            // LEVEL I
            builder.registerPotionRecipe(
                    Potions.AWKWARD,
                    ominousBottleI,
                    ModPotions.EVOKERS_OMEN_I_BASE
            );

            // LEVEL II
            builder.registerPotionRecipe(
                    Potions.AWKWARD,
                    ominousBottleII,
                    ModPotions.EVOKERS_OMEN_II_BASE
            );

            // LEVEL III - Stage 1
            builder.registerPotionRecipe(
                    Potions.AWKWARD,
                    ominousBottleIII,
                    ModPotions.EVOKERS_OMEN_III_BASE
            );

            // LEVEL III - Emerald-first path
            builder.registerPotionRecipe(
                    ModPotions.EVOKERS_OMEN_III_BASE,
                    Ingredient.of(Items.EMERALD),
                    ModPotions.EVOKERS_OMEN_III_EMERALD
            );

            // LEVEL IV - Stage 1
            builder.registerPotionRecipe(
                    Potions.AWKWARD,
                    Ingredient.of(ModItems.EVOKERS_OMEN_III),
                    ModPotions.EVOKERS_OMEN_IV_BASE
            );

            // LEVEL V - Stage 1
            builder.registerPotionRecipe(
                    Potions.AWKWARD,
                    Ingredient.of(ModItems.EVOKERS_OMEN_IV),
                    ModPotions.EVOKERS_OMEN_V_BASE
            );
        });

        EvokerOnlyRaid.LOGGER.info(
                "Registering Evoker's Omen brewing recipes."
        );
    }

    private static Ingredient createOminousBottleIngredient(
            int amplifier
    ) {
        DataComponentPatch components =
                DataComponentPatch.builder()
                        .set(
                                DataComponents.OMINOUS_BOTTLE_AMPLIFIER,
                                new OminousBottleAmplifier(amplifier)
                        )
                        .build();

        return new ComponentsIngredient(
                Ingredient.of(Items.OMINOUS_BOTTLE),
                components
        ).toVanilla();
    }

    private ModBrewing() {
    }
}