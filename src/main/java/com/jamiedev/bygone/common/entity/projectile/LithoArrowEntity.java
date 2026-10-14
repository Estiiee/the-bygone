package com.jamiedev.bygone.common.entity.projectile;

import com.jamiedev.bygone.core.registry.BGDamageTypes;
import com.jamiedev.bygone.core.registry.BGEntityTypes;
import com.jamiedev.bygone.core.registry.BGItems;
import com.jamiedev.bygone.core.registry.BGMobEffects;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Locale;
import java.util.function.Supplier;

public class LithoArrowEntity extends AbstractArrow {

    private int duration = 200;
    private ItemStack pickupItem = new ItemStack(BGItems.LITHOPLASM_ARROW.get());
    
    
    public LithoArrowEntity(EntityType<? extends LithoArrowEntity> entityType, Level world) {
        super(entityType, world);
    }

    public LithoArrowEntity(Level world, double x, double y, double z, ItemStack stack, @Nullable ItemStack shotFrom) {
        super((EntityType<? extends AbstractArrow>) BGEntityTypes.LITHOPLASM_ARROW.get(), x, y, z, world);
    }

    public LithoArrowEntity(Level world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom) {
        super(BGEntityTypes.LITHOPLASM_ARROW.get(), owner, world);
    }

    public static void dropArrow(Level world, BlockPos pos) {
        dropStack(world, pos, new ItemStack(BGItems.LITHOPLASM_ARROW.get(), 1));
    }

    private static void dropStack(Level world, Supplier<ItemEntity> itemEntitySupplier, ItemStack stack) {
        if (!world.isClientSide && !stack.isEmpty() && world.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) {
            ItemEntity itemEntity = itemEntitySupplier.get();
            itemEntity.setDefaultPickUpDelay();
            world.addFreshEntity(itemEntity);
        }
    }
    
    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide && !this.inGround) {
            this.level().addParticle(DustParticleOptions1.REDSTONE, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }
    }
    
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("Duration")) {
            this.duration = compound.getInt("Duration");
        }

    }
    
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Duration", this.duration);
    }
    
    @Override
    protected void doPostHurtEffects(LivingEntity living) {
        super.doPostHurtEffects(living);
        MobEffectInstance mobeffectinstance = new MobEffectInstance(BGMobEffects.HAUNTED.get().get(), this.duration, 0);
        living.hurt(BGDamageTypes.source(living.level(), BGDamageTypes.HAUNTED, living, living.getLastAttacker()), 1);
        living.addEffect(mobeffectinstance, this.getEffectSource());
    }

    public static void dropStack(Level world, BlockPos pos, ItemStack stack) {
        double d = (double) EntityType.ITEM.getHeight() / 2.0;
        double e = (double) pos.getX() + 0.5 + Mth.nextDouble(world.random, -0.25, 0.25);
        double f = (double) pos.getY() + 0.5 + Mth.nextDouble(world.random, -0.25, 0.25) - d;
        double g = (double) pos.getZ() + 0.5 + Mth.nextDouble(world.random, -0.25, 0.25);
        dropStack(world, () -> {
            return new ItemEntity(world, e, f, g, stack);
        }, stack);
    }
    
    @Override
    protected ItemStack getPickupItem() {
        return this.pickupItem.copy();
    }
}

class DustParticleOptions1 implements ParticleOptions {
    public static final Vector3f PLASM_PARTICLE_COLOR =
            Vec3.fromRGB24(14151396).toVector3f();
    
    public static final DustParticleOptions1 REDSTONE =
            new DustParticleOptions1(PLASM_PARTICLE_COLOR, 1.0F);
    
    public static final Codec<DustParticleOptions1> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ExtraCodecs.VECTOR3F.fieldOf("color")
                            .forGetter(DustParticleOptions1::getColor),
                    Codec.FLOAT.fieldOf("scale")
                            .forGetter(DustParticleOptions1::getScale)
            ).apply(instance, DustParticleOptions1::new));
    
    public static final ParticleOptions.Deserializer<DustParticleOptions1> DESERIALIZER =
            new ParticleOptions.Deserializer<>() {
                @Override
                public DustParticleOptions1 fromCommand(
                        ParticleType<DustParticleOptions1> type,
                        StringReader reader
                ) throws CommandSyntaxException
                {
                    reader.expect(' ');
                    float r = reader.readFloat();
                    reader.expect(' ');
                    float g = reader.readFloat();
                    reader.expect(' ');
                    float b = reader.readFloat();
                    reader.expect(' ');
                    float scale = reader.readFloat();
                    
                    return new DustParticleOptions1(
                            new Vector3f(r, g, b), scale
                    );
                }
                
                @Override
                public DustParticleOptions1 fromNetwork(
                        ParticleType<DustParticleOptions1> type,
                        FriendlyByteBuf buffer
                ) {
                    return new DustParticleOptions1(
                            new Vector3f(
                                    buffer.readFloat(),
                                    buffer.readFloat(),
                                    buffer.readFloat()
                            ),
                            buffer.readFloat()
                    );
                }
            };
    
    private final Vector3f color;
    private final float scale;
    
    public DustParticleOptions1(Vector3f color, float scale) {
        this.color = color;
        this.scale = scale;
    }
    
    @Override
    public ParticleType<DustParticleOptions> getType() {
        return ParticleTypes.DUST;
    }
    
    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeFloat(color.x());
        buffer.writeFloat(color.y());
        buffer.writeFloat(color.z());
        buffer.writeFloat(scale);
    }
    
    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %.2f",
                BuiltInRegistries.PARTICLE_TYPE.getKey(getType()),
                color.x(), color.y(), color.z(), scale);
    }
    
    public Vector3f getColor() {
        return color;
    }
    
    public float getScale() {
        return scale;
    }
}