package com.brandon.evokeronlyraid;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public final class EvokerRaidSavedData extends SavedData {

    private static final String FILE_ID =
            EvokerOnlyRaid.MOD_ID + "_evoker_raid_data";

    public static final SavedData.Factory<EvokerRaidSavedData> FACTORY =
            new SavedData.Factory<>(
                    EvokerRaidSavedData::new,
                    EvokerRaidSavedData::load,
                    DataFixTypes.SAVED_DATA_RAIDS
            );

    private final Map<Integer, Integer> raidLevels;

    public EvokerRaidSavedData() {
        this.raidLevels = new HashMap<>();
    }

    private static EvokerRaidSavedData load(
            CompoundTag tag,
            HolderLookup.Provider provider
    ) {
        EvokerRaidSavedData data = new EvokerRaidSavedData();

        CompoundTag raidLevelsTag =
                tag.getCompound("raid_levels");

        for (String key : raidLevelsTag.getAllKeys()) {
            try {
                int raidId = Integer.parseInt(key);
                int omenLevel = raidLevelsTag.getInt(key);

                data.raidLevels.put(raidId, omenLevel);
            } catch (NumberFormatException ignored) {
                EvokerOnlyRaid.LOGGER.warn(
                        "Ignoring invalid saved Evoker raid ID: {}",
                        key
                );
            }
        }

        return data;
    }

    @Override
    public CompoundTag save(
            CompoundTag tag,
            HolderLookup.Provider
    ) {
        CompoundTag raidLevelsTag = new CompoundTag();

        for (Map.Entry<Integer, Integer> entry : raidLevels.entrySet()) {
            raidLevelsTag.putInt(
                    Integer.toString(entry.getKey()),
                    entry.getValue()
            );
        }

        tag.put("raid_levels", raidLevelsTag);

        return tag;
    }

    public void setRaidLevel(int raidId, int omenLevel) {
        if (omenLevel <= 0) {
            raidLevels.remove(raidId);
        } else {
            raidLevels.put(raidId, omenLevel);
        }

        setDirty();
    }

    public int getRaidLevel(int raidId) {
        return raidLevels.getOrDefault(raidId, 0);
    }

    public Map<Integer, Integer> getRaidLevels() {
        return Map.copyOf(raidLevels);
    }

    public static String getFileId() {
        return FILE_ID;
    }
}