package net.deechael.plumtweaks.mixin;

import net.deechael.plumtweaks.utils.TaxFreeLevels;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Shadow
    public int experienceLevel;

    @Unique
    private int previousLevel;

    @Inject(method = "applyEnchantmentCosts", at = @At(value = "HEAD"))
    public void rememberExperienceLevel(ItemStack enchantedItem, int experienceLevels, CallbackInfo ci) {
        this.previousLevel = experienceLevel;
    }

    @Inject(method = "applyEnchantmentCosts", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerEntity;experienceLevel:I", ordinal = 2))
    public void flattenEnchantmentCost(ItemStack enchantedItem, int experienceLevels, CallbackInfo ci) {
        int levelCost = this.previousLevel - experienceLevel;

        experienceLevel = this.previousLevel;
        TaxFreeLevels.applyFlattenedXpCost((PlayerEntity) (Object) this, levelCost);
    }

}
