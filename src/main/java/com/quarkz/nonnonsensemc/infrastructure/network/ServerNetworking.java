package com.quarkz.nonnonsensemc.infrastructure.network;

import com.quarkz.nonnonsensemc.NonNonSenseMC;
import com.quarkz.nonnonsensemc.application.VillagerInfoService;
import com.quarkz.nonnonsensemc.network.VillagerInfoPacket;
import com.quarkz.nonnonsensemc.network.VillagerInfoRequestC2SPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Registro de networking del lado servidor. Depende de infraestructura (Fabric API) y usa el servicio de aplicación.
 */
public final class ServerNetworking {
    private ServerNetworking() {}

    private static final VillagerInfoService SERVICE = new VillagerInfoService();

    public static void register() {
        // Registrar tipos de payload
        PayloadTypeRegistry.playS2C().register(VillagerInfoPacket.ID, VillagerInfoPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(VillagerInfoRequestC2SPayload.ID, VillagerInfoRequestC2SPayload.CODEC);

        // Handler C2S
        ServerPlayNetworking.registerGlobalReceiver(VillagerInfoRequestC2SPayload.ID, (payload, context) -> {
            NonNonSenseMC.LOGGER.debug("Received VillagerInfoRequestC2SPayload packet");

            ServerPlayerEntity player = context.player();
            Entity entity = player.getEntityWorld().getEntityById(payload.entityId());
            if (!(entity instanceof VillagerEntity villager)) return;

            var info = SERVICE.extract(villager, NonNonSenseMC.LOGGER);

            ServerPlayNetworking.send(player, new VillagerInfoPacket(
                    info.scared(),
                    info.readyToBreed(),
                    info.homeMemory(),
                    info.lastSleptMemory(),
                    info.golemDetectedMemory()
            ));
        });

        NonNonSenseMC.LOGGER.info("Networking del servidor registrado");
        NonNonSenseMC.isRegistered=true;
    }
}

