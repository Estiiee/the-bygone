package com.jamiedev.bygone.client.renderer.weather;

import com.jamiedev.bygone.common.weather.weather_types.FogWeatherEvent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.world.level.Level;

public class FogWeatherRenderer extends WeatherRenderer<FogWeatherEvent> {
    public FogWeatherRenderer(FogWeatherEvent instance) { super(instance); }
    
    @Override public void tick(Level level) {}
    @Override public void render(Level level, LightTexture lightTexture, float partialTick, double camX, double camY, double camZ) {}
}