package net.deechael.plumtweaks.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.SpongeBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpongeBlock.class)
@Implements(@Interface(iface = Fertilizable.class, prefix = "fertilizable$"))
public class SpongeBlockMixin extends Block {
    public SpongeBlockMixin(Settings settings) {
        super(settings);
    }

    public boolean fertilizable$isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    public boolean fertilizable$canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return true;
    }

    public void fertilizable$grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        Block.dropStack(world, pos, new ItemStack(this));
    }
}