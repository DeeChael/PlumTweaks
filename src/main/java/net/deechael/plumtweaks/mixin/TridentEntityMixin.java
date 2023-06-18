package net.deechael.plumtweaks.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TridentEntity.class)
public abstract class TridentEntityMixin extends ProjectileEntity {

    @Shadow
    @Final
    private static TrackedData<Byte> LOYALTY;

    @Shadow
    private boolean dealtDamage;

    protected TridentEntityMixin(EntityType<ProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (dataTracker.get(LOYALTY) == 0 || this.dealtDamage) return;

        if (this.getY() <= this.getWorld().getBottomY()) {
            this.dealtDamage = true;
            this.setVelocity(0, 0, 0);
        }
    }

}
