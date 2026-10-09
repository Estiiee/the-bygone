package com.jamiedev.bygone.core.mixin;

import com.jamiedev.bygone.Bygone;
import com.jamiedev.bygone.common.weather.weather_types.HauntingsCategoryHolder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(NaturalSpawner.class)
public class NaturalSpawnerMixin {
    
    @WrapOperation(
            method = "spawnForChunk",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/NaturalSpawner$SpawnState;canSpawnForCategory(Lnet/minecraft/world/entity/MobCategory;Lnet/minecraft/world/level/ChunkPos;)Z"
            )
    )
    private static boolean bygone$canSpawnForHauntingsCategory(
            NaturalSpawner.SpawnState spawnState, MobCategory category, ChunkPos pos, Operation<Boolean> original,
            ServerLevel level
    ) {
        if (category == HauntingsCategoryHolder.HAUNTING_MOB_CATEGORY
                && !HauntingsCategoryHolder.checkHauntingsActive(level)) return false;
        return original.call(spawnState, category, pos);
    }
    
    @Inject(method = "getRandomSpawnMobAt", at = @At("HEAD"), cancellable = true)
    private static void bygone$getHauntingsMobPool(
            ServerLevel level, StructureManager structureManager, ChunkGenerator generator,
            MobCategory category, RandomSource random, BlockPos pos,
            CallbackInfoReturnable<Optional<MobSpawnSettings.SpawnerData>> cir
    ) {
        if (category != HauntingsCategoryHolder.HAUNTING_MOB_CATEGORY) return;
        if (!HauntingsCategoryHolder.checkHauntingsActive(level)) return;
        
        List<EntityType<?>> mobs = BuiltInRegistries.ENTITY_TYPE.stream()
                .filter(type -> type.getCategory() == category).toList();
        EntityType<?> chosen = mobs.get(random.nextInt(mobs.size()));
        
        cir.setReturnValue(Optional.of(new MobSpawnSettings.SpawnerData(
                chosen, 10, 1, 2
        )));
    }
    
    @Inject(method = "isValidSpawnPostitionForType", at = @At("HEAD"), cancellable = true)
    private static void bygone$allowHauntingsOnValidGround(
            ServerLevel level, MobCategory category, StructureManager structureManager,
            ChunkGenerator generator, MobSpawnSettings.SpawnerData data, BlockPos.MutableBlockPos pos,
            double distance, CallbackInfoReturnable<Boolean> cir
    ) {
        if (category != HauntingsCategoryHolder.HAUNTING_MOB_CATEGORY) return;
        if (data.type.getCategory() != HauntingsCategoryHolder.HAUNTING_MOB_CATEGORY) return;
        if (!HauntingsCategoryHolder.checkHauntingsActive(level)) return;
        
        cir.setReturnValue(level.noCollision(data.type.getAABB(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D)));
    }
    
    @Inject(method = "isValidPositionForMob", at = @At("HEAD"), cancellable = true)
    private static void bygone$allowHauntingsMobPosition(
            ServerLevel level, Mob mob, double distance, CallbackInfoReturnable<Boolean> cir
    ) {
        if (mob.getType().getCategory() != HauntingsCategoryHolder.HAUNTING_MOB_CATEGORY) return;
        if (!HauntingsCategoryHolder.checkHauntingsActive(level)) return;
        
        cir.setReturnValue(true);
    }
}