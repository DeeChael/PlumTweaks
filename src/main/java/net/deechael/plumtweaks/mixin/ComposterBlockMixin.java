package net.deechael.plumtweaks.mixin;

import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ComposterBlock.class)
public abstract class ComposterBlockMixin {

    @Shadow
    private static void registerCompostableItem(float levelIncreaseChance, ItemConvertible item) {
    }

    @Inject(method = "registerDefaultCompostableItems", at = @At("TAIL"))
    private static void registerDefaultCompostableItems(CallbackInfo ci) {
        registerCompostableItem(0.3F, Items.ROTTEN_FLESH);
        registerCompostableItem(0.3F, Items.SPIDER_EYE);
        registerCompostableItem(0.3F, Items.FERMENTED_SPIDER_EYE);
        registerCompostableItem(0.3F, Items.POISONOUS_POTATO);
        registerCompostableItem(0.65F, Items.RABBIT_FOOT);
    }

}
