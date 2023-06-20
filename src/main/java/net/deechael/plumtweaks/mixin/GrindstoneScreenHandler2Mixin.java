package net.deechael.plumtweaks.mixin;

import net.deechael.plumtweaks.accessor.GrindstoneScreenHandlerAccessor;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/screen/GrindstoneScreenHandler$2")
public class GrindstoneScreenHandler2Mixin extends Slot {

    @Shadow
    @Final
    GrindstoneScreenHandler field_16777;

    public GrindstoneScreenHandler2Mixin(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Inject(method = "canInsert(Lnet/minecraft/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
    private void canInsert(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Inventory input = ((GrindstoneScreenHandlerAccessor) this.field_16777).getInput();

        cir.setReturnValue(cir.getReturnValueZ() || stack.getItem() == Items.BOOK && !input.getStack(1).isOf(Items.BOOK));
    }

}
