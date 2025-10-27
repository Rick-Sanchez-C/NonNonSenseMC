package com.quarkz.nonnonsensemc.mixins.villager;

import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(targets = "net/minecraft/village/TradeOffers$EnchantBookFactory")
public class EnchantBookFactoryMixin {
    @Redirect(
            method = "create(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/random/Random;)Lnet/minecraft/village/TradeOffer;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/MathHelper;nextInt(Lnet/minecraft/util/math/random/Random;II)I"
            )
    )
    private int alwaysMaxLevel(Random random, int min, int max) {
        return max;
    }
}
