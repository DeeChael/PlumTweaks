package net.deechael.plumtweaks.mixin;

import net.deechael.plumtweaks.PlumConstants;
import net.deechael.plumtweaks.interf.LivingEntityMixinInterf;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements LivingEntityMixinInterf {

    private final LivingEntity self = ((LivingEntity) (Object) this);

    private boolean hasVoidLevitation  = false;
    private boolean hasVoidSlowFalling = false;
    public final List<ItemStack> arrowsInEntity = new ArrayList<>();

    @Override
    public boolean hasVoidLevitation() {
        return this.hasVoidLevitation;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean(PlumConstants.MOD_ID + ".hasVoidLevitation"  , this.hasVoidLevitation  );
        nbt.putBoolean(PlumConstants.MOD_ID + ".hasVoidSlowFalling" , this.hasVoidSlowFalling );
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.hasVoidLevitation  = nbt.getBoolean(PlumConstants.MOD_ID + ".hasVoidLevitation"  );
        this.hasVoidSlowFalling = nbt.getBoolean(PlumConstants.MOD_ID + ".hasVoidSlowFalling" );
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (this.hasVoidLevitation) {
            if (! self.hasStatusEffect(StatusEffects.LEVITATION)) {
                this.hasVoidLevitation = false;
            } else if (self.getY() >= (double) self.getWorld().getBottomY()) {
                self.removeStatusEffect(StatusEffects.LEVITATION);
                self.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, -1, 127, true, true, true));
                this.hasVoidSlowFalling = true;
            }
        }
        if (this.hasVoidSlowFalling) {
            if (! self.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
                this.hasVoidSlowFalling = false;
            } else if (self.isOnGround()) {
                self.removeStatusEffect(StatusEffects.SLOW_FALLING);
                this.hasVoidSlowFalling = false;
            }
        }
    }

    @Inject(method = "tickInVoid", at = @At("HEAD"), cancellable = true)
    private void tickInVoid(CallbackInfo ci) {
        if (this.hasVoidLevitation) {
            ci.cancel();
        }
    }

    @Redirect(method = "tryUseTotem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageSource;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"))
    private boolean tryUseTotemBypass(DamageSource instance, TagKey<DamageType> tag) {
        return !instance.isOf(DamageTypes.OUT_OF_WORLD) && instance.isIn(tag);
    }

    @Inject(method = "tryUseTotem", at = @At(value = "RETURN"))
    private void tryUseTotemLevitation(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && source.isOf(DamageTypes.OUT_OF_WORLD)) {
            self.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, -1, 127, true, true, true));
            self.removeStatusEffect(StatusEffects.SLOW_FALLING);
            this.hasVoidLevitation  = true;
            this.hasVoidSlowFalling = false;
        }
    }

    @Redirect(method = "tryUseTotem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    private void tryUseTotem(ItemStack totemOfDyingItemStack, int decrementAmount) {
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, totemOfDyingItemStack) <= 0) {
            totemOfDyingItemStack.decrement(decrementAmount);
        }
    }

    @Inject(method = "applyDamage", at = @At("TAIL"))
    public void addArrowsOnDamage(DamageSource source, float amount, CallbackInfo ci) {
        var srcEntity = source.getSource();
        if (!(srcEntity instanceof ArrowEntity || srcEntity instanceof SpectralArrowEntity)
                || ((PersistentProjectileEntity) srcEntity).pickupType
                == PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY) {
            return;
        }
        if (srcEntity instanceof ArrowEntity) {
            ItemStack damagingArrow = ((ArrowEntityAccessor) srcEntity).getArrowItemStack();
            addToArrowsInEntity(damagingArrow);
        } else {
            addToArrowsInEntity(new ItemStack(Items.SPECTRAL_ARROW));
        }
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    public void dropArrowsOnKill(DamageSource damageSource, CallbackInfo ci) {
        if (!arrowsInEntity.isEmpty()) {
            for (var stack : arrowsInEntity) {
                this.self.getWorld().spawnEntity(new ItemEntity(
                        this.self.getWorld(),
                        this.self.getX(),
                        this.self.getY(),
                        this.self.getZ(),
                        stack
                ));
            }
        }
    }

    private void addToArrowsInEntity(ItemStack stack) {
        boolean didCompress = false;
        for (var i : arrowsInEntity) {
            if (i.isOf(stack.getItem()) && i.getCount() + stack.getCount() <= i.getMaxCount()) {
                i.increment(stack.getCount());
                didCompress = true;
                break;
            }
        }
        if (!didCompress) {
            arrowsInEntity.add(stack);
        }
    }

}
