package com.jamiedev.bygone.client.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

// thank you tazer :O
// https://github.com/Alchemists-Of-Yore/No-Mans-Land/blob/1.21.1/src/main/java/com/farcr/nomansland/client/model/BuddyModel.java
public abstract class HauntingsMobModel<E extends Entity> extends HierarchicalModel<E> {
    private float fadeAlpha = 1.0f;
   
    public void setFadeAlpha(float newAlpha) {
        this.fadeAlpha = Mth.clamp(newAlpha, 0.0F, 1.0F);
    }
    
    public float modifyColor(float a) {
        return fadeAlpha * a;
    }
    
    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float r, float g, float b, float a) {
        super.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, r, g, b, this.modifyColor(a));
    }
}