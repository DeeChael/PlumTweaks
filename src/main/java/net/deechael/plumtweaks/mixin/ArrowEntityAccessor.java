package net.deechael.plumtweaks.mixin;

import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ArrowEntity.class)
public interface ArrowEntityAccessor {

    @Invoker("asItemStack")
    ItemStack getArrowItemStack();

}
