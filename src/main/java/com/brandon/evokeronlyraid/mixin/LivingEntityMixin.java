package com.brandon.evokeronlyraid.mixin;

import com.brandon.evokeronlyraid.ModItems;
import com.brandon.evokeronlyraid.access.EvokerRaidMobData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @ModifyArg(
            method = "dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;ZLnet/minecraft/resources/ResourceKey;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;dropFromLootTable(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;ZLnet/minecraft/resources/ResourceKey;Ljava/util/function/Consumer;)V"
            ),
            index = 4
    )
    private Consumer<ItemStack> evokerOnlyRaid$modifyEvokerDrops(
            Consumer<ItemStack> original
    ) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;

        if (!(livingEntity instanceof Evoker evoker)) {
            return original;
        }

        EvokerRaidMobData evokerData =
                (EvokerRaidMobData) evoker;

        int omenLevel =
                evokerData.evokerOnlyRaid$getOmenLevel();

        // Custom emerald drops for Evoker's Omen raids only.
        if (omenLevel >= 1 && omenLevel <= 5) {
            int minimum;
            int maximum;

            switch (omenLevel) {
                case 1 -> {
                    minimum = 1;
                    maximum = 5;
                }
                case 2 -> {
                    minimum = 2;
                    maximum = 5;
                }
                case 3 -> {
                    minimum = 2;
                    maximum = 6;
                }
                case 4 -> {
                    minimum = 3;
                    maximum = 6;
                }
                case 5 -> {
                    minimum = 3;
                    maximum = 7;
                }
                default -> {
                    minimum = 0;
                    maximum = 0;
                }
            }

            int customEmeraldAmount =
                    minimum + evoker.getRandom().nextInt(
                            maximum - minimum + 1
                    );

            original.accept(
                    new ItemStack(
                            Items.EMERALD,
                            customEmeraldAmount
                    )
            );
        }

        // At most ONE Evoker's Omen can drop from each Evoker.
        Item omenDrop = null;

        if (omenLevel == 0) {

            // Normal/world/summoned/vanilla-raid Evoker:
            // 5% chance for Evoker's Omen I.
            if (evoker.getRandom().nextDouble() < 0.05) {
                omenDrop = ModItems.EVOKERS_OMEN_I;
            }

        } else {

            // Custom raid Evoker.
            double roll = evoker.getRandom().nextDouble() * 100.0;

            if (omenLevel >= 5 && roll < 0.5) {
                omenDrop = ModItems.EVOKERS_OMEN_V;

            } else if (omenLevel >= 4 && roll < 1.5) {
                omenDrop = ModItems.EVOKERS_OMEN_IV;

            } else if (omenLevel >= 3 && roll < 3.5) {
                omenDrop = ModItems.EVOKERS_OMEN_III;

            } else if (omenLevel >= 2 && roll < 6.5) {
                omenDrop = ModItems.EVOKERS_OMEN_II;

            } else if (roll < 11.5) {
                omenDrop = ModItems.EVOKERS_OMEN_I;
            }
        }

        if (omenDrop != null) {
            original.accept(
                    new ItemStack(omenDrop)
            );
        }

        // Suppress vanilla emeralds only for custom Evoker raids.
        // All other vanilla loot is untouched.
        if (omenLevel >= 1 && omenLevel <= 5) {
            return stack -> {
                if (!stack.is(Items.EMERALD)) {
                    original.accept(stack);
                }
            };
        }

        return original;
    }
}