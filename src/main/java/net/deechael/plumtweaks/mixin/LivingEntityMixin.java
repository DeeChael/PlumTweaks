package net.deechael.plumtweaks.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Redirect(method = "tryUseTotem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"))
    private void tryUseTotem(ItemStack totemOfDyingItemStack, int decrementAmount) {
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, totemOfDyingItemStack) <= 0) {
            totemOfDyingItemStack.decrement(decrementAmount);
        }
    }

}
