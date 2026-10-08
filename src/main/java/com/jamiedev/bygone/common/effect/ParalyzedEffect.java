package com.jamiedev.bygone.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ParalyzedEffect extends MobEffect {
    
    public ParalyzedEffect() {
        super(MobEffectCategory.HARMFUL, 0x1B1B2F);
        this.addAttributeModifier(
                Attributes.MOVEMENT_SPEED,
                "7c1e3f52-9a4d-4b86-a0f3-2d58e6b17c94",
                -1.0,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
        //potentially only works on horses
        this.addAttributeModifier(
                Attributes.JUMP_STRENGTH,
                "e3a9d0b4-51c7-4f2e-8b6a-94f1c3025d7a",
                -1.0,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }
}