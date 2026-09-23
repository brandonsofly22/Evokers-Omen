package com.brandon.evokeronlyraid.mixin;

import com.brandon.evokeronlyraid.access.EvokerRaidMobData;
import net.minecraft.world.entity.monster.Evoker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Evoker.class)
public abstract class EvokerMixin implements EvokerRaidMobData {

    @Unique
    private int evokerOnlyRaid$omenLevel = 0;

    @Override
    public int evokerOnlyRaid$getOmenLevel() {
        return evokerOnlyRaid$omenLevel;
    }

    @Override
    public void evokerOnlyRaid$setOmenLevel(int level) {
        this.evokerOnlyRaid$omenLevel = level;
    }
}