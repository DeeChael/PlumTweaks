package net.deechael.plumtweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DamageEnchantment.class)
public abstract class DamageEnchantmentMixin {

    @Inject(method = "canAccept(Lnet/minecraft/enchantment/Enchantment;)Z", at = @At("HEAD"), cancellable = true)
    private void canAccept(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        if (other instanceof DamageEnchantment)
            cir.setReturnValue(true);
    }

    @ModifyReturnValue(method = "getMaxLevel()I", at = @At("RETURN"))
    private int getMaxLevel(int orig) {
        return 10;
    }

    @ModifyReturnValue(method = "isAcceptableItem", at = @At("RETURN"))
    public boolean isAcceptableItem(boolean original, ItemStack stack) {
        return stack.getItem() instanceof TridentItem || original;
    }

}
