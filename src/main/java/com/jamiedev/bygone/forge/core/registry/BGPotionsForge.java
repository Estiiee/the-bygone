package com.jamiedev.bygone.forge.core.registry;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.core.registry.BGMobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BGPotionsForge {
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, Bygone.MOD_ID);
    
    public static final RegistryObject<Potion> HAUNTED_POTION = POTIONS.register("haunted_potion",
            () -> new Potion("haunted_potion", new MobEffectInstance(BGMobEffects.HAUNTED.get().get(), 3600, 0)));
    
    public static final RegistryObject<Potion>  LONG_HAUNTED_POTION = POTIONS.register("long_haunted_potion",
            () -> new Potion("haunted_potion", new MobEffectInstance(BGMobEffects.HAUNTED.get().get(), 9600, 0)));
    
    public static void init(IEventBus eventBus) {
        POTIONS.register(eventBus);
    }
}