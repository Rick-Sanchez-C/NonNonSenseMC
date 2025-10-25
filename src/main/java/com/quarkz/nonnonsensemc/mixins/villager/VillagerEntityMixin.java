package com.quarkz.nonnonsensemc.mixins.villager;

import com.quarkz.nonnonsensemc.NonNonSenseMC;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin {

    @Inject(method= "fillRecipes", at= @At("RETURN"))
    private void afterFillRecipes(CallbackInfo ci) {
        VillagerEntity self = (VillagerEntity)(Object)this;
        TradeOfferList offers = self.getOffers();
        for(TradeOffer offer: offers){
            try{
                Field maxUses = TradeOffer.class.getDeclaredField("maxUses");
                maxUses.setAccessible(true);
                maxUses.setInt(offer, Integer.MAX_VALUE);
                Field uses = TradeOffer.class.getDeclaredField("uses");
                uses.setInt(offer, Integer.MAX_VALUE);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                NonNonSenseMC.LOGGER.error("Error while getting maxUses", e);
            }
        }

    }

}
