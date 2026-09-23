package com.brandon.evokeronlyraid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.Map;

public final class EvokerRaidSavedData extends SavedData {

    private static final String FILE_ID =
            EvokerOnlyRaid.MOD_ID + "_evoker_raid_data";

    public static final Codec<EvokerRaidSavedData> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            Codec.unboundedMap(Codec.STRING, Codec.INT)
                                    .optionalFieldOf("raid_levels", Map.of())
                                    .forGetter(EvokerRaidSavedData::getSerializedRaidLevels)
                    ).apply(instance, EvokerRaidSavedData::new)
            );

    public static final SavedDataType<EvokerRaidSavedData> TYPE =
            new SavedDataType<>(
                    FILE_ID,
                    EvokerRaidSavedData::new,
                    CODEC,
                    DataFixTypes.SAVED_DATA_RAIDS
            );

    private final Map<Integer, Integer> raidLevels;

    public EvokerRaidSavedData() {
        this.raidLevels = new HashMap<>();
    }

    private EvokerRaidSavedData(Map<String, Integer> serializedRaidLevels) {
        this.raidLevels = new HashMap<>();

        for (Map.Entry<String, Integer> entry : serializedRaidLevels.entrySet()) {
            try {
                int raidId = Integer.parseInt(entry.getKey());
                this.raidLevels.put(raidId, entry.getValue());
            } catch (NumberFormatException ignored) {
                EvokerOnlyRaid.LOGGER.warn(
                        "Ignoring invalid saved Evoker raid ID: {}",
                        entry.getKey()
                );
            }
        }
    }

    private Map<String, Integer> getSerializedRaidLevels() {
        Map<String, Integer> serialized = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : raidLevels.entrySet()) {
            serialized.put(
                    Integer.toString(entry.getKey()),
                    entry.getValue()
            );
        }

        return serialized;
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
}