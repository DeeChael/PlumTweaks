package net.deechael.plumtweaks.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Bucketable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Bucketable.class)
public interface BucketableMixin {

    @Inject(at = @At("HEAD"), method = "tryBucket", cancellable = true, require = 0)
    private static <T extends LivingEntity & Bucketable> void tryBucket(PlayerEntity player, Hand hand, T entity, CallbackInfoReturnable<Optional<ActionResult>> info) {
        ItemStack stack = player.getStackInHand(hand);
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0 && stack.isOf(Items.WATER_BUCKET) && entity.isAlive()) {
            info.setReturnValue(Optional.empty());
        }
    }

}
