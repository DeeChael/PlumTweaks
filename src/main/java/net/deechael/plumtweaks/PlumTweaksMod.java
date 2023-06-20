package net.deechael.plumtweaks;

import net.deechael.plumtweaks.recipe.CraftablePotionCombinationCraftingRecipe;
import net.fabricmc.api.ModInitializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlumTweaksMod implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(PlumConstants.MOD_ID);

    public static SpecialRecipeSerializer<CraftablePotionCombinationCraftingRecipe> CRAFTABLE_POTION_COMBINATION;

    @Override
    public void onInitialize() {
        CRAFTABLE_POTION_COMBINATION = Registry.register(Registries.RECIPE_SERIALIZER,
                new Identifier(PlumConstants.MOD_ID, "craftable_potion_combination"),
                new SpecialRecipeSerializer<>(CraftablePotionCombinationCraftingRecipe::new));
    }

}