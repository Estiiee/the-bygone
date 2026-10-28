package com.jamiedev.bygone.core.network;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.client.ClientPacketHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class HauntingsTollS2C implements BygonePacket {

    public static HauntingsTollS2C read(FriendlyByteBuf buf) {
        return new HauntingsTollS2C();
    }

    @Override
    public void write(FriendlyByteBuf buf) {
    
    }
    
    @Override
    public void handle(Player player) {
        ClientPacketHandler.handle(this);
    }
    
    @Override
    public ResourceLocation id() {
        return Bygone.id("hauntings_toll");
    }
}