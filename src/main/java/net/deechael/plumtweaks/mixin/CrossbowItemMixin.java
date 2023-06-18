package net.deechael.plumtweaks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = CrossbowItem.class)
public class CrossbowItemMixin {

    @Shadow
    private static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated) {
    }

    @Shadow
    private static void postShoot(World world, LivingEntity entity, ItemStack stack) {
    }

    private static int multishotLevel = 0;

    @Inject(method = "loadProjectiles(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"))
    private static void loadProjectiles(LivingEntity shooter, ItemStack projectile, CallbackInfoReturnable<Boolean> cir) {
        multishotLevel = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, projectile);
    }

    @ModifyConstant(method = "loadProjectiles(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)Z", constant = @Constant(intValue = 3))
    private static int loadProjectiles(int orig) {
        return multishotLevel * 2 + 1;
    }

    @Inject(method = "shootAll(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;FF)V", at = @At(ordinal = 0, value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/CrossbowItem;getSoundPitches(Lnet/minecraft/util/math/random/Random;)[F"), cancellable = true)
    private static void shootAll(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence, CallbackInfo ci, @Local List<ItemStack> list, @Local float[] fs) {
        float range = Math.max(10.0F, list.size() * 0.2F);

        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemStack = list.get(i);
            boolean bl = entity instanceof PlayerEntity && ((PlayerEntity) entity).getAbilities().creativeMode;
            if (!itemStack.isEmpty()) {
                if (i == 0) {
                    shoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, 0.0F);
                } else if (i % 2 != 0) {
                    shoot(world, entity, hand, stack, itemStack, fs[1], bl, speed, divergence, -range * (i / (float) list.size() / 2F));
                } else {
                    shoot(world, entity, hand, stack, itemStack, fs[2], bl, speed, divergence, range * (i / (float) list.size() / 2F));
                }
            }
        }

        postShoot(world, entity, stack);
        ci.cancel();
    }

}