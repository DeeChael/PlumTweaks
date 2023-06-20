package net.deechael.plumtweaks.mixin;

import net.deechael.plumtweaks.accessor.GrindstoneScreenHandlerAccessor;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GrindstoneScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.stream.Collectors;

@Mixin(GrindstoneScreenHandler.class)
public abstract class GrindstoneScreenHandlerMixin extends ScreenHandler implements GrindstoneScreenHandlerAccessor {

    @Shadow
    @Final
    Inventory input;

    @Final
    @Shadow
    private Inventory result;

    @Unique
    private PlayerEntity playerEntity;

    protected GrindstoneScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(at = @At("RETURN"), method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V")
    private void init(int syncId, PlayerInventory playerInventory, final ScreenHandlerContext context, CallbackInfo ci) {
        this.playerEntity = playerInventory.player;
    }

    @Inject(at = @At("RETURN"), method = "updateResult", cancellable = true)
    private void updateResult(CallbackInfo ci) {
        ItemStack input1 = this.input.getStack(0);
        ItemStack input2 = this.input.getStack(1);

        PlayerEntity player = this.playerEntity;

        if (!(input1.hasEnchantments() && input2.getItem() == Items.BOOK
                || input2.hasEnchantments() && input1.getItem() == Items.BOOK)) {
            return;
        }

        ItemStack result = new ItemStack(Items.ENCHANTED_BOOK);

        Map<Enchantment, Integer> map = EnchantmentHelper.get(input1.hasEnchantments() ? input1 : input2)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (map.isEmpty())
            return;

        for (Map.Entry<Enchantment, Integer> entry : map.entrySet()) {
            EnchantedBookItem.addEnchantment(result, new EnchantmentLevelEntry(entry.getKey(), entry.getValue()));
        }

        this.result.setStack(0, result);
        this.sendContentUpdates();
        ci.cancel();
    }

    @Override
    public Inventory getInput() {
        return input;
    }

    @Override
    public Inventory getResult() {
        return result;
    }

}
