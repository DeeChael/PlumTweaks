package net.deechael.plumtweaks.mixin.client;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {

    @ModifyConstant(method = "drawForeground(Lnet/minecraft/client/gui/DrawContext;II)V", constant = @Constant(intValue = 40))
    private int drawForeground(int orig) {
        return Integer.MAX_VALUE;
    }

}
