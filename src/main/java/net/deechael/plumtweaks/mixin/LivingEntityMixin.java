package net.deechael.plumtweaks.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    private final LivingEntity self = ((LivingEntity) (Object) this);
    public final List<ItemStack> arrowsInEntity = new ArrayList<>();

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
