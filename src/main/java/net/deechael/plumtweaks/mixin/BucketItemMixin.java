package net.deechael.plumtweaks.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
public abstract class BucketItemMixin {

    @Inject(at = @At("HEAD"), method = "getEmptiedStack", cancellable = true)
    private static void getEmptiedStack(ItemStack stack, PlayerEntity player, CallbackInfoReturnable<ItemStack> info) {
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0 && (stack.isOf(Items.WATER_BUCKET) || stack.isOf(Items.LAVA_BUCKET))) {
            info.setReturnValue(stack);
        }
    }

}
