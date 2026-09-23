package com.brandon.evokeronlyraid;

import com.brandon.evokeronlyraid.access.EvokerRaidData;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;

import java.util.OptionalInt;

public final class ModEffects {

    public static final Holder<MobEffect> EVOKERS_OMEN =
            Registry.registerForHolder(
                    BuiltInRegistries.MOB_EFFECT,
                    Identifier.fromNamespaceAndPath(
                            EvokerOnlyRaid.MOD_ID,
                            "evokers_omen"
                    ),
                    new EvokersOmenEffect()
            );

    public static final Holder<MobEffect> EVOKER_RAID_OMEN =
            Registry.registerForHolder(
                    BuiltInRegistries.MOB_EFFECT,
                    Identifier.fromNamespaceAndPath(
                            EvokerOnlyRaid.MOD_ID,
                            "evoker_raid_omen"
                    ),
                    new EvokerRaidOmenEffect()
            );

    public static void register() {
        EvokerOnlyRaid.LOGGER.info("Registering Evoker's Omen effects.");
    }

    private static final class EvokersOmenEffect extends MobEffect {

        private EvokersOmenEffect() {
            super(MobEffectCategory.NEUTRAL, 0x5B3A75);
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(
                int remainingDuration,
                int amplification
        ) {
            return true;
        }

        @Override
        public boolean applyEffectTick(
                ServerLevel level,
                LivingEntity mob,
                int amplification
        ) {
            if (mob instanceof ServerPlayer player) {
                if (!player.isSpectator()
                        && level.getDifficulty() != Difficulty.PEACEFUL
                        && level.isVillage(player.blockPosition())) {

                    Raid raid = level.getRaidAt(player.blockPosition());

                    if (raid == null
                            || raid.getRaidOmenLevel() < raid.getMaxRaidOmenLevel()) {

                        player.addEffect(
                                new MobEffectInstance(
                                        EVOKER_RAID_OMEN,
                                        200,
                                        amplification
                                )
                        );

                        player.setRaidOmenPosition(player.blockPosition());

                        return false;
                    }
                }
            }

            return true;
        }
    }

    private static final class EvokerRaidOmenEffect extends MobEffect {

        private EvokerRaidOmenEffect() {
            super(MobEffectCategory.NEUTRAL, 0x4A235A);
        }

        @Override
        public boolean shouldApplyEffectTickThisTick(
                int remainingDuration,
                int amplification
        ) {
            return remainingDuration == 1;
        }

        @Override
        public boolean applyEffectTick(
                ServerLevel level,
                LivingEntity mob,
                int amplification
        ) {
            if (mob instanceof ServerPlayer player) {
                if (!player.isSpectator()) {
                    var raidOmenPosition = player.getRaidOmenPosition();

                    if (raidOmenPosition != null) {
                        Raid raid = level.getRaids().createOrExtendRaid(
                                player,
                                raidOmenPosition
                        );

                        if (raid != null) {
                            int evokerOmenLevel = amplification + 1;

                            ((EvokerRaidData) raid)
                                    .evokerOnlyRaid$setOmenLevel(evokerOmenLevel);

                            OptionalInt raidId = level.getRaids().getId(raid);

                            if (raidId.isPresent()) {
                                EvokerRaidSavedData savedData =
                                        level.getDataStorage().computeIfAbsent(
                                                EvokerRaidSavedData.TYPE
                                        );

                                savedData.setRaidLevel(
                                        raidId.getAsInt(),
                                        evokerOmenLevel
                                );
                            }
                        }

                        player.clearRaidOmenPosition();

                        return false;
                    }
                }
            }

            return true;
        }
    }

    private ModEffects() {
    }
}