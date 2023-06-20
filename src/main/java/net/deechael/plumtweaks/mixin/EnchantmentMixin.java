package net.deechael.plumtweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.*;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Enchantment.class)
public abstract class EnchantmentMixin {

    @Inject(method = "isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void isAcceptableItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Enchantment enchantment = (Enchantment) (Object) this;
        if (enchantment == Enchantments.FIRE_ASPECT || enchantment == Enchantments.KNOCKBACK || enchantment == Enchantments.LOOTING) {
            if (!(stack.getItem() instanceof AxeItem || stack.getItem() instanceof TridentItem))
                return;

            cir.setReturnValue(true);
        }

        if (enchantment == Enchantments.INFINITY) {
            if (stack.getItem() instanceof BucketItem || stack.getItem() instanceof CrossbowItem || stack.isOf(Items.TOTEM_OF_UNDYING)) {
                cir.setReturnValue(true);
            }
        }

        if (enchantment instanceof ProtectionEnchantment) {
            if (stack.getItem() instanceof ElytraItem) {
                cir.setReturnValue(true);
            }
        }

        if (enchantment == Enchantments.THORNS) {
            if (stack.getItem() instanceof ElytraItem) {
                cir.setReturnValue(true);
            }
        }

        if (enchantment == Enchantments.FIRE_PROTECTION || enchantment == Enchantments.BLAST_PROTECTION) {
            cir.setReturnValue(true);
        }

        if ((enchantment instanceof PowerEnchantment || enchantment instanceof FlameEnchantment || enchantment instanceof PunchEnchantment) && stack.getItem() instanceof CrossbowItem) {
            cir.setReturnValue(true);
        }

        if (enchantment == Enchantments.LOOTING && stack.getItem() instanceof BowItem) {
            cir.setReturnValue(true);
        }
    }

    @ModifyReturnValue(method = "getMaxLevel()I", at = @At("RETURN"))
    private int getMaxLevel(int orig) {
        Enchantment self = (Enchantment) (Object) this;
        if (self instanceof FlameEnchantment) {
            return 5;
        } else if (self instanceof SweepingEnchantment) {
            return 5;
        } else if (self instanceof SoulSpeedEnchantment || self instanceof FrostWalkerEnchantment || self instanceof DepthStriderEnchantment) {
            return 5;
        } else if (self instanceof PowerEnchantment) {
            return 10;
        } else if (self instanceof UnbreakingEnchantment) {
            return 10;
        } else if (self instanceof EfficiencyEnchantment) {
            return 10;
        } else if (self instanceof MultishotEnchantment) {
            return 5;
        }
        return orig;
    }

}
