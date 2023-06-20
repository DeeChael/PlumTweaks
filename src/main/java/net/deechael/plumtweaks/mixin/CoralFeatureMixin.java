package net.deechael.plumtweaks.mixin;

import net.deechael.plumtweaks.interf.CoralFeatureMixinInterf;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.CoralFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CoralFeature.class)
public abstract class CoralFeatureMixin implements CoralFeatureMixinInterf {

    @Shadow
    protected abstract boolean generateCoral(WorldAccess world, Random random, BlockPos pos, BlockState state);

    public boolean generateForBlockState(
            StructureWorldAccess structureWorldAccess,
            Random random,
            BlockPos blockPos,
            BlockState blockState
    ) {
        return this.generateCoral(structureWorldAccess, random, blockPos, blockState);
    }

}
