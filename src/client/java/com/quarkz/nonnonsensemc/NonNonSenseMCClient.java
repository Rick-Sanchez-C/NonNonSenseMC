package com.quarkz.nonnonsensemc;

import com.quarkz.nonnonsensemc.client.ClientVillagerHudState;
import com.quarkz.nonnonsensemc.network.VillagerInfoPacket;
import com.quarkz.nonnonsensemc.network.VillagerInfoRequestC2SPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.util.Identifier;

public class NonNonSenseMCClient implements ClientModInitializer {

    public static final String MOD_ID = "nonnonsensemc";

    @Override
    public void onInitializeClient() {
        // Registrar codecs de payload del lado cliente only if there are not registered yet
        if(!NonNonSenseMC.isRegistered) {
            PayloadTypeRegistry.playS2C().register(VillagerInfoPacket.ID, VillagerInfoPacket.CODEC);
            PayloadTypeRegistry.playC2S().register(VillagerInfoRequestC2SPayload.ID, VillagerInfoRequestC2SPayload.CODEC);


            NonNonSenseMC.isRegistered = true;
        }
        HudElementRegistry.attachElementAfter(VanillaHudElements.CROSSHAIR, Identifier.of(NonNonSenseMCClient.MOD_ID, "before_chat"), HudRenderingEntrypoint::render);

        ClientPlayNetworking.registerGlobalReceiver(VillagerInfoPacket.ID, (payload, context) -> {
            ClientVillagerHudState.updateFrom(payload);
        });
        NonNonSenseMC.LOGGER.info("Networking del cliente registrado");
    }
    public static void maybeRequestFor(int villagerEntityId) {
        long now = System.currentTimeMillis();
        if (!ClientVillagerHudState.beginRequestIfAllowed(villagerEntityId, now, 150L)) return;
        ClientPlayNetworking.send(new VillagerInfoRequestC2SPayload(villagerEntityId));
    }
}
