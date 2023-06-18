package net.deechael.plumtweaks.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.enchantment.LuckEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LuckEnchantment.class)
public class LuckEnchantmentMixin {

    @ModifyReturnValue(method = "getMaxLevel()I", at = @At("RETURN"))
    private int getMaxLevel(int orig) {
        return 5;
    }

}
