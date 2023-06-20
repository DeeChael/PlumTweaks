package net.deechael.plumtweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    @Shadow
    public abstract ItemStack getStack();

    @ModifyReturnValue(method = "isFireImmune", at = @At("RETURN"))
    private boolean getMaxLevel(boolean orig) {
        if (EnchantmentHelper.getLevel(Enchantments.FIRE_PROTECTION, this.getStack()) > 0) {
            return true;
        }
        return orig;
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.isIn(DamageTypeTags.IS_EXPLOSION) && EnchantmentHelper.getLevel(Enchantments.BLAST_PROTECTION, this.getStack()) > 0) {
            cir.setReturnValue(false);
        }
    }

}
