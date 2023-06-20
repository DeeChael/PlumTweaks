package net.deechael.plumtweaks.mixin;

import net.deechael.plumtweaks.utils.TaxFreeLevels;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

    @Shadow
    @Final
    private Property levelCost;

    @Unique
    private PlayerEntity playerEntity;

    private AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "updateResult()V", at = @At("TAIL"))
    private void updateResult(CallbackInfo ci) {
        if (this.input.getStack(1).isEmpty())
            levelCost.set(1);
    }

    @ModifyConstant(method = "updateResult()V", constant = @Constant(ordinal = 2, intValue = 40))
    private int updateResult(int orig) {
        return Integer.MAX_VALUE;
    }

    @Inject(method = "onTakeOutput", at = @At("HEAD"))
    public void taxfreelevels$capturePlayer(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        this.playerEntity = player;
    }

    @ModifyArg(method = "onTakeOutput", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;addExperienceLevels(I)V"), index = 0)
    public int onTakeOutput(int negativeLevelCost) {
        TaxFreeLevels.applyFlattenedXpCost(this.playerEntity, -negativeLevelCost);
        return 0;
    }

}
