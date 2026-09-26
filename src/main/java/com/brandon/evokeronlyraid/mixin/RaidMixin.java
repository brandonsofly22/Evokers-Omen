package com.brandon.evokeronlyraid.mixin;

import com.brandon.evokeronlyraid.EvokerRaidSavedData;
import com.brandon.evokeronlyraid.access.EvokerRaidData;
import com.brandon.evokeronlyraid.access.EvokerRaidMobData;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.OptionalInt;

@Mixin(targets = "net.minecraft.world.entity.raid.Raid")
public abstract class RaidMixin implements EvokerRaidData {

    @Unique
    private int evokerOnlyRaid$omenLevel = 0;

    @Unique
    private int evokerOnlyRaid$scalingCounter = 0;

    @Unique
    private int evokerOnlyRaid$lastScaledWave = -1;

    @Override
    public int evokerOnlyRaid$getOmenLevel() {
        return evokerOnlyRaid$omenLevel;
    }

    @Override
    public void evokerOnlyRaid$setOmenLevel(int level) {
        this.evokerOnlyRaid$omenLevel = level;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void evokerOnlyRaid$restoreSavedOmenLevel(ServerLevel level, CallbackInfo ci) {
        if (evokerOnlyRaid$omenLevel > 0) {
            return;
        }

        Raid raid = (Raid) (Object) this;
        OptionalInt raidId = level.getRaids().getId(raid);

        if (raidId.isEmpty()) {
            return;
        }

        EvokerRaidSavedData savedData =
                level.getDataStorage().computeIfAbsent(EvokerRaidSavedData.TYPE);

        int savedOmenLevel = savedData.getRaidLevel(raidId.getAsInt());

        if (savedOmenLevel > 0) {
            evokerOnlyRaid$omenLevel = savedOmenLevel;
        }
    }

    @ModifyReturnValue(method = "getRaidOmenLevel", at = @At("RETURN"))
    private int evokerOnlyRaid$blockOmenDuringCustomRaid(int original) {
        if (evokerOnlyRaid$omenLevel > 0) {
            Raid raid = (Raid) (Object) this;
            return raid.getMaxRaidOmenLevel();
        }
        return original;
    }

    @Redirect(method = "spawnGroup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/EntitySpawnReason;)Lnet/minecraft/world/entity/Entity;"))
    private Entity evokerOnlyRaid$replaceWithEvoker(EntityType<?> originalType, Level level, EntitySpawnReason reason) {
        if (evokerOnlyRaid$omenLevel > 0) {
            Entity evoker = EntityType.EVOKER.create(level, reason);
            if (evoker instanceof EvokerRaidMobData evokerData) evokerData.evokerOnlyRaid$setOmenLevel(evokerOnlyRaid$omenLevel);
            return evoker;
        }
        return originalType.create(level, reason);
    }

    @ModifyExpressionValue(method = "spawnGroup", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/raid/Raid$RaiderType;entityType:Lnet/minecraft/world/entity/EntityType;", ordinal = 1))
    private EntityType<?> evokerOnlyRaid$skipRavagerRiderBlock(EntityType<?> originalType) {
        if (evokerOnlyRaid$omenLevel > 0) return EntityType.EVOKER;
        return originalType;
    }

    @Redirect(method = "spawnGroup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/raid/Raid;joinRaid(Lnet/minecraft/server/level/ServerLevel;ILnet/minecraft/world/entity/raid/Raider;Lnet/minecraft/core/BlockPos;Z)V"))
    private void evokerOnlyRaid$joinAndScale(Raid raid, ServerLevel level, int groupNumber, Raider raider, BlockPos pos, boolean exists) {
        raid.joinRaid(level, groupNumber, raider, pos, exists);
        if (evokerOnlyRaid$omenLevel <= 1) return;
        if (evokerOnlyRaid$lastScaledWave != groupNumber) { evokerOnlyRaid$lastScaledWave = groupNumber; evokerOnlyRaid$scalingCounter = 0; }
        evokerOnlyRaid$scalingCounter++;
        int extraEvokers = switch (evokerOnlyRaid$omenLevel) {
            case 2 -> evokerOnlyRaid$scalingCounter % 4 == 0 ? 1 : 0;
            case 3 -> evokerOnlyRaid$scalingCounter % 2 == 0 ? 1 : 0;
            case 4 -> evokerOnlyRaid$scalingCounter % 4 == 0 ? 3 : 0;
            case 5 -> 1;
            default -> 0;
        };
        for (int i = 0; i < extraEvokers; i++) {
            Raider extraEvoker = (Raider) EntityType.EVOKER.create(level, EntitySpawnReason.EVENT);
            if (extraEvoker != null) {
                if (extraEvoker instanceof EvokerRaidMobData evokerData) evokerData.evokerOnlyRaid$setOmenLevel(evokerOnlyRaid$omenLevel);
                raid.joinRaid(level, groupNumber, extraEvoker, pos, false);
            }
        }
    }
}
