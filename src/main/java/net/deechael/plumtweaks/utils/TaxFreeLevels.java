package net.deechael.plumtweaks.utils;

import net.minecraft.entity.player.PlayerEntity;

public class TaxFreeLevels {

    public static int getXpDifference(PlayerEntity player, int from, int to) {
        int currentLevel = player.experienceLevel;

        int xpSum = 0;
        for (int l = from; l < to; l++) {
            player.experienceLevel = l;
            xpSum += player.getNextLevelExperience();
        }

        player.experienceLevel = currentLevel;

        return xpSum;
    }

    public static void addNoScoreExperience(PlayerEntity player, int xp) {
        player.experienceProgress += (float) xp / (float) player.getNextLevelExperience();
        player.addExperience(0);
    }

    public static int getFlattenedXpCost(PlayerEntity player, int levelCost) {
        int pretendLevel = Math.min(player.experienceLevel, 30);

        int from = Math.max(pretendLevel - levelCost, 0);
        return TaxFreeLevels.getXpDifference(player, from, from + levelCost);
    }

    public static void applyFlattenedXpCost(PlayerEntity player, int levelCost) {
        addNoScoreExperience(player, -getFlattenedXpCost(player, levelCost));
    }

}
