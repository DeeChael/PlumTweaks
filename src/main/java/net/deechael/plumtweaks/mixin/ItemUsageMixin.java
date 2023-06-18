package net.deechael.plumtweaks.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemUsage.class)
public class ItemUsageMixin {

    @Inject(at = @At("HEAD"), method = "exchangeStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Z)Lnet/minecraft/item/ItemStack;", cancellable = true)
    private static void iwb$createFilledResult(ItemStack inputStack, PlayerEntity player, ItemStack outputStack, boolean creativeOverride, CallbackInfoReturnable<ItemStack> info) {
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, inputStack) > 0) {
            if (inputStack.isOf(Items.BUCKET)) {
                info.setReturnValue(inputStack);
            }
            if (inputStack.isOf(Items.WATER_BUCKET) && outputStack.isOf(Items.BUCKET)) {
                info.setReturnValue(inputStack);
            }
            if (inputStack.isOf(Items.LAVA_BUCKET) && outputStack.isOf(Items.BUCKET)) {
                info.setReturnValue(inputStack);
            }
        }
    }

}
