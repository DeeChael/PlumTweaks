package net.deechael.plumtweaks.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;
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
public abstract class CrossbowItemMixin {

    @Shadow
    private static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated) {
    }

    @Shadow
    private static void postShoot(World world, LivingEntity entity, ItemStack stack) {
    }

    @Shadow
    private static float getSpeed(ItemStack stack) {
        return 0;
    }

    @Shadow
    private boolean charged;
    @Shadow
    private boolean loaded;

    @Shadow
    private static PersistentProjectileEntity createArrow(World world, LivingEntity entity, ItemStack crossbow, ItemStack arrow) {
        return null;
    }

    @Shadow
    private static boolean loadProjectile(LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean simulated, boolean creative) {
        return false;
    }

    private static int multishotLevel = 0;

    @ModifyConstant(method = "loadProjectiles(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)Z", constant = @Constant(intValue = 3))
    private static int loadProjectiles(int orig) {
        return multishotLevel * 2 + 1;
    }

    @Inject(method = "loadProjectiles", at = @At("HEAD"), cancellable = true)
    private static void loadProjectiles(LivingEntity shooter, ItemStack crossbow, CallbackInfoReturnable<Boolean> cir) {
        multishotLevel = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, crossbow);

        int i = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, crossbow);
        int j = i == 0 ? 1 : 3;
        boolean bl = shooter instanceof PlayerEntity && (((PlayerEntity)shooter).getAbilities().creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, crossbow) > 0 );
        ItemStack itemStack = shooter.getProjectileType(crossbow);
        ItemStack itemStack2 = itemStack.copy();
        for (int k = 0; k < j; ++k) {
            if (k > 0) {
                itemStack = itemStack2.copy();
            }
            if (itemStack.isEmpty() && bl) {
                itemStack = new ItemStack(Items.ARROW);
                itemStack2 = itemStack.copy();
            }
            if (loadProjectile(shooter, crossbow, itemStack, k > 0, bl)) continue;
            cir.setReturnValue(false);
        }
        cir.setReturnValue(true);
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

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (CrossbowItem.isCharged(itemStack)) {
            CrossbowItem.shootAll(world, user, hand, itemStack, getSpeed(itemStack), 1.0f);
            CrossbowItem.setCharged(itemStack, false);
            cir.setReturnValue(TypedActionResult.consume(itemStack));
        }
        if (!user.getProjectileType(itemStack).isEmpty() || EnchantmentHelper.getLevel(Enchantments.INFINITY, itemStack) > 0) {
            if (!CrossbowItem.isCharged(itemStack)) {
                this.charged = false;
                this.loaded = false;
                user.setCurrentHand(hand);
            }
            cir.setReturnValue(TypedActionResult.consume(itemStack));
        }
        cir.setReturnValue(TypedActionResult.fail(itemStack));
    }

    @Inject(method = "shoot", at = @At("HEAD"), cancellable = true)
    @Environment(EnvType.SERVER)
    private static void shoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated, CallbackInfo ci) {
        ProjectileEntity projectileEntity;

        if (world.isClient) {
            return;
        }
        boolean bl = projectile.isOf(Items.FIREWORK_ROCKET);
        if (bl) {
            projectileEntity = new FireworkRocketEntity(world, projectile, shooter, shooter.getX(), shooter.getEyeY() - (double)0.15f, shooter.getZ(), true);
        } else {
            PersistentProjectileEntity persistentProjectileEntity = createArrow(world, shooter, crossbow, projectile);
            projectileEntity = persistentProjectileEntity;
            if (creative || simulated != 0.0f || EnchantmentHelper.getLevel(Enchantments.INFINITY, crossbow) > 0) {
                persistentProjectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            }
            int k, j;
            if ((j = EnchantmentHelper.getLevel(Enchantments.POWER, crossbow)) > 0) {
                persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + (double)j * 0.5 + 0.5);
            }
            if ((k = EnchantmentHelper.getLevel(Enchantments.PUNCH, crossbow)) > 0) {
                persistentProjectileEntity.setPunch(k);
            }
            if (EnchantmentHelper.getLevel(Enchantments.FLAME, crossbow) > 0) {
                persistentProjectileEntity.setOnFireFor(100);
            }
        }
        if (shooter instanceof CrossbowUser crossbowUser) {
            crossbowUser.shoot(crossbowUser.getTarget(), crossbow, projectileEntity, simulated);
        } else {
            Vec3d vec3d = shooter.getOppositeRotationVector(1.0f);
            Quaternionf quaternionf = new Quaternionf().setAngleAxis(simulated * ((float)Math.PI / 180), vec3d.x, vec3d.y, vec3d.z);
            Vec3d vec3d2 = shooter.getRotationVec(1.0f);
            Vector3f vector3f = vec3d2.toVector3f().rotate(quaternionf);
            projectileEntity.setVelocity(vector3f.x(), vector3f.y(), vector3f.z(), speed, divergence);
        }
        crossbow.damage(bl ? 3 : 1, shooter, e -> e.sendToolBreakStatus(hand));
        world.spawnEntity(projectileEntity);
        world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0f, soundPitch);
        ci.cancel();
    }

}
