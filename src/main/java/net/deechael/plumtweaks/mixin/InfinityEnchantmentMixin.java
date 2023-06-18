package net.deechael.plumtweaks.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.InfinityEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InfinityEnchantment.class)
public class InfinityEnchantmentMixin {

    @Inject(method = "canAccept(Lnet/minecraft/enchantment/Enchantment;)Z", at = @At("HEAD"), cancellable = true)
    private void canAccept(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
        if (other == Enchantments.MENDING) {
            cir.setReturnValue(true);
        }
    }

}
