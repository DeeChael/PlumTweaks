package net.deechael.plumtweaks.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/block/dispenser/DispenserBehavior$9")
public abstract class DispenserBehavior9Mixin extends ItemDispenserBehavior {

    @Final
    @Shadow
    private ItemDispenserBehavior fallbackBehavior;

    @Inject(at = @At(value = "HEAD", ordinal = 0), method = "dispenseSilently", cancellable = true)
    private void iwb$getEmptyBucket(BlockPointer pointer, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0 && (stack.isOf(Items.BUCKET) || stack.isOf(Items.LAVA_BUCKET))) {
            ServerWorld serverLevel = pointer.getWorld();
            BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
            BlockState blockState = serverLevel.getBlockState(blockPos);
            Block block = blockState.getBlock();

            if (block instanceof FluidDrainable) {
                ItemStack itemStack2 = ((FluidDrainable) block).tryDrainFluid(serverLevel, blockPos, blockState);
                if (itemStack2.isEmpty())
                    cir.setReturnValue(super.dispenseSilently(pointer, stack));
                serverLevel.emitGameEvent(null, GameEvent.FLUID_PICKUP, blockPos);
            } else {
                cir.setReturnValue(super.dispenseSilently(pointer, stack));
            }
            cir.setReturnValue(stack);
        }
    }

}
