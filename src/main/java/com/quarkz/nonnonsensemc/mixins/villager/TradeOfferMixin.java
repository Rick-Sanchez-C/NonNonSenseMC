package com.quarkz.nonnonsensemc.mixins.villager;

import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TradeOffer.class)
public class TradeOfferMixin {
    @Shadow
    private int demandBonus;
    @Shadow
    private int maxUses;
    @Shadow
    private int uses;
    @Unique
    private int virtualUses = 0;

    @Inject(method="use", at = @At("HEAD"), cancellable = true)
    private void noIncrementRealUses(CallbackInfo ci) {
        this.virtualUses++;
        this.demandBonus++;
        ci.cancel();
    }

    @Inject(method = "updateDemandBonus", at= @At("HEAD"), cancellable = true)
    private void recalcDemandWithVirtualUses(CallbackInfo ci) {
        this.demandBonus = this.demandBonus + this.virtualUses - (this.maxUses - this.virtualUses);
        ci.cancel();
    }

}
