package net.deechael.plumtweaks.mixin;

import net.deechael.plumtweaks.accessor.GrindstoneScreenHandlerAccessor;
import net.deechael.plumtweaks.utils.TaxFreeLevels;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.world.WorldEvents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;

@Mixin(targets = "net/minecraft/screen/GrindstoneScreenHandler$4")
public class GrindstoneScreenHandler4Mixin extends Slot {

    @Final
    @Shadow
    ScreenHandlerContext field_16779;

    @Shadow
    @Final
    GrindstoneScreenHandler field_16780;

    public GrindstoneScreenHandler4Mixin(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Inject(method = "onTakeItem", at = @At("HEAD"), cancellable = true)
    private void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        Inventory input = ((GrindstoneScreenHandlerAccessor) this.field_16780).getInput();

        ItemStack input1 = input.getStack(0);
        ItemStack input2 = input.getStack(1);

        if (!(input1.hasEnchantments() && input2.getItem() == Items.BOOK
                || input2.hasEnchantments() && input1.getItem() == Items.BOOK)) {
            return;
        }

        boolean stack1Book = input1.getItem() == Items.BOOK;
        ItemStack enchantedItemStack = stack1Book ? input2 : input1;
        ItemStack bookItemStack = stack1Book ? input1 : input2;

        if (!player.getAbilities().creativeMode) {
            int cost = (int) Math.ceil(EnchantmentHelper.get(enchantedItemStack)
                    .entrySet()
                    .stream()
                    .mapToDouble(entry -> entry.getKey().getMinPower(entry.getValue()))
                    .sum() * 0.3 + 8.0);
            TaxFreeLevels.applyFlattenedXpCost(player, cost);
        }

        ItemStack itemStack = enchantedItemStack.copy();

        EnchantmentHelper.set(new HashMap<>(), itemStack);
        itemStack.setRepairCost(0);

        if (itemStack.getItem() == Items.ENCHANTED_BOOK) {
            itemStack = new ItemStack(Items.BOOK);
            if (enchantedItemStack.hasCustomName()) {
                itemStack.setCustomName(enchantedItemStack.getName());
            }
        }

        input.setStack(stack1Book ? 1 : 0, itemStack);

        if (bookItemStack.getCount() == 1)
            input.setStack(stack1Book ? 0 : 1, ItemStack.EMPTY);
        else {
            ItemStack bookNew = bookItemStack.copy();
            bookNew.setCount(bookItemStack.getCount() - 1);
            input.setStack(stack1Book ? 0 : 1, bookNew);
        }

        this.field_16779.run((world, pos) -> world.syncWorldEvent(WorldEvents.GRINDSTONE_USED, pos, 0));
        ci.cancel();
    }

    @Override
    public boolean canTakeItems(PlayerEntity player) {
        Inventory input = ((GrindstoneScreenHandlerAccessor) this.field_16780).getInput();

        ItemStack input1 = input.getStack(0);
        ItemStack input2 = input.getStack(1);

        if (input1.hasEnchantments() && input2.getItem() == Items.BOOK
                || input2.hasEnchantments() && input1.getItem() == Items.BOOK) {

            boolean stack1Book = input1.getItem() == Items.BOOK;
            ItemStack enchantedItemStack = stack1Book ? input2 : input1;

            int cost = (int) Math.ceil(EnchantmentHelper.get(enchantedItemStack)
                    .entrySet()
                    .stream()
                    .mapToDouble(entry -> entry.getKey().getMinPower(entry.getValue()))
                    .sum() * 0.3 + 8.0);

            return player.getAbilities().creativeMode || player.experienceLevel >= cost;
        }
        return true;
    }

}
