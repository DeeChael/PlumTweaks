package net.deechael.plumtweaks.mixin;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

    @Shadow
    private static boolean canAcceptRecipeOutput(DynamicRegistryManager registryManager, @Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count) {
        throw new AssertionError();
    }

    @Inject(at = @At(value = "HEAD", ordinal = 0), method = "craftRecipe", cancellable = true)
    private static void craftRecipe(DynamicRegistryManager registryManager, @Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count, CallbackInfoReturnable<Boolean> cir) {
        if (recipe != null && canAcceptRecipeOutput(registryManager, recipe, slots, count)) {
            ItemStack itemStack = slots.get(0);
            if (EnchantmentHelper.getLevel(Enchantments.INFINITY, slots.get(1)) > 0 && slots.get(1).isOf(Items.BUCKET)) {
                if (itemStack.isOf(Blocks.WET_SPONGE.asItem()) && !slots.get(1).isEmpty()) {
                    ItemStack iwb = new ItemStack(Items.WATER_BUCKET);
                    iwb.addEnchantment(Enchantments.INFINITY, 1);
                    slots.set(1, iwb);
                }
            }
        }
    }

}
