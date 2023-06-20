package net.deechael.plumtweaks.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GrindstoneScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GrindstoneScreen.class)
public abstract class GrindstoneScreenMixin extends HandledScreen<GrindstoneScreenHandler> {

    public GrindstoneScreenMixin(GrindstoneScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        ItemStack input1 = this.handler.getSlot(0).getStack();
        ItemStack input2 = this.handler.getSlot(1).getStack();

        boolean stack1Book = input1.getItem() == Items.BOOK;
        ItemStack enchantedItemStack = stack1Book ? input2 : input1;

        int cost = (int) Math.ceil(EnchantmentHelper.get(enchantedItemStack)
                .entrySet()
                .stream()
                .mapToDouble(entry -> entry.getKey().getMinPower(entry.getValue()))
                .sum() * 0.3 + 8.0);

        if (cost > 0) {
            int j = 8453920;
            Text text;

            if (!this.handler.getSlot(2).hasStack()) {
                text = null;
            } else {
                text = Text.translatable("container.repair.cost", cost);
                assert this.client != null;
                if (!this.handler.getSlot(2).canTakeItems(this.client.player)) {
                    j = 16736352;
                }
            }

            if (text != null) {
                int k = this.backgroundWidth - 8 - this.textRenderer.getWidth(text) - 2;
                context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
                context.drawTextWithShadow(this.textRenderer, text, k, 69, j);
            }
        }
    }

}
