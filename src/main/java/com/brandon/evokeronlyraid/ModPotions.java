package com.brandon.evokeronlyraid;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.alchemy.Potion;
public final class ModPotions {
 public static final Holder<Potion> EVOKERS_OMEN_I_BASE=register("evokers_omen_1_base");
 public static final Holder<Potion> EVOKERS_OMEN_II_BASE=register("evokers_omen_2_base");
 public static final Holder<Potion> EVOKERS_OMEN_III_BASE=register("evokers_omen_3_base");
 public static final Holder<Potion> EVOKERS_OMEN_III_EMERALD=register("evokers_omen_3_emerald");
 public static final Holder<Potion> EVOKERS_OMEN_III_BANNER=register("evokers_omen_3_banner");
 public static final Holder<Potion> EVOKERS_OMEN_IV_BASE=register("evokers_omen_4_base");
 public static final Holder<Potion> EVOKERS_OMEN_V_BASE=register("evokers_omen_5_base");
 private static Holder<Potion> register(String name){return Registry.registerForHolder(BuiltInRegistries.POTION,Identifier.fromNamespaceAndPath(EvokerOnlyRaid.MOD_ID,name),new Potion(name));}
 public static void register(){EvokerOnlyRaid.LOGGER.info("Registering Evoker's Omen brewing potions.");}
 private ModPotions(){}
}