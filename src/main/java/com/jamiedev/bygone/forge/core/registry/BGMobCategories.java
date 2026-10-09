package com.jamiedev.bygone.forge.core.registry;

import net.minecraft.world.entity.MobCategory;

//judging by the 1.21 fabric version using a mixin (amd create() looking like a stub by default) I am guessing this
//is a forge only way, hence in the forge package. might be worth checking if it works on both though
public final class BGMobCategories {
    
    public static final MobCategory HAUNTINGS_MOB = MobCategory.create(
            "BYGONE_HAUNTINGS_MOB",
            "bygone_hauntings_mobs",
            70,
            false,
            false,
            64
    );
    
    private BGMobCategories() {
    }
    
    public static void init() {
    }
}