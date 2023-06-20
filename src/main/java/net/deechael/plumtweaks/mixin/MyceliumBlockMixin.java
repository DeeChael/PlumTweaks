package net.deechael.plumtweaks.mixin;

import net.minecraft.block.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(MyceliumBlock.class)
@Implements(@Interface(iface = Fertilizable.class, prefix = "fertilizable$"))
public class MyceliumBlockMixin extends Block {

    public MyceliumBlockMixin(Settings settings) {
        super(settings);
    }

    public boolean fertilizable$isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    public boolean fertilizable$canGrow(World world, Random random, BlockPos pos, BlockState state) {
        return world.getBlockState(pos.add(0, 1, 0)).isAir();
    }

    public void fertilizable$grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        if (Random.create().nextBoolean())
            world.setBlockState(pos.add(0, 1, 0), Blocks.BROWN_MUSHROOM.getDefaultState());
        else
            world.setBlockState(pos.add(0, 1, 0), Blocks.RED_MUSHROOM.getDefaultState());
    }

}
