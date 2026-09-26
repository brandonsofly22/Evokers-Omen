package com.brandon.evokeronlyraid;

import com.brandon.evokeronlyraid.access.EvokerRaidData;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.raid.Raid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EvokerOnlyRaid implements ModInitializer {

 public static final String MOD_ID = "evokers-omen";
 public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

 @Override
 public void onInitialize() {
  ModEffects.register();
  ModItems.register();
  ModPotions.register();
  ModBrewing.register();

  ServerLifecycleEvents.SERVER_STARTED.register(server -> {
   for (ServerLevel level : server.getAllLevels()) {
    EvokerRaidSavedData savedData =
            level.getDataStorage().computeIfAbsent(
                    EvokerRaidSavedData.TYPE
            );

    for (var entry : savedData.getRaidLevels().entrySet()) {
     int raidId = entry.getKey();
     int omenLevel = entry.getValue();

     Raid raid = level.getRaids().get(raidId);

     if (raid != null) {
      ((EvokerRaidData) raid)
              .evokerOnlyRaid$setOmenLevel(omenLevel);

      LOGGER.info(
              "Restored Evoker raid {} with Omen level {}.",
              raidId,
              omenLevel
      );
     }
    }
   }
  });

  LOGGER.info("Evoker's Omen initialized.");
 }
}