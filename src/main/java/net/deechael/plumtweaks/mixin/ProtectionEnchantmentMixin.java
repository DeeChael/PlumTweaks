package net.deechael.plumtweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.ProtectionEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ProtectionEnchantment.class)
public class ProtectionEnchantmentMixin {

    @Inject(method = "canAccept(Lnet/minecraft/enchantment/Enchantment;)Z", at = @At("HEAD"), cancellable = true)
    private void canAccept(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        if (other instanceof ProtectionEnchantment)
            cir.setReturnValue(true);
    }

    @ModifyReturnValue(method = "getMaxLevel()I", at = @At("RETURN"))
    private int getMaxLevel(int orig) {
        return 10;
    }

}
