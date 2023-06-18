package net.deechael.plumtweaks.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.MultishotEnchantment;
import net.minecraft.enchantment.PiercingEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({
        MultishotEnchantment.class, PiercingEnchantment.class
})
public class CrossbowEnchantmentsMixin {

    @Inject(method = "canAccept(Lnet/minecraft/enchantment/Enchantment;)Z", at = @At("HEAD"), cancellable = true)
    private void canAccept(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        if (other == Enchantments.MULTISHOT || other == Enchantments.PIERCING) {
            cir.setReturnValue(true);
        }
    }

}
