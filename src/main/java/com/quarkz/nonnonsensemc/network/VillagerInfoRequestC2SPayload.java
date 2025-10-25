package com.quarkz.nonnonsensemc.network;


import com.quarkz.nonnonsensemc.NonNonSenseMC;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record VillagerInfoRequestC2SPayload(int entityId) implements CustomPayload {
    public static final Identifier ID_RAW = Identifier.of(NonNonSenseMC.MOD_ID, "villager_info_request");
    public static final CustomPayload.Id<VillagerInfoRequestC2SPayload> ID = new CustomPayload.Id<>(ID_RAW);

    public static final PacketCodec<RegistryByteBuf, VillagerInfoRequestC2SPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, VillagerInfoRequestC2SPayload::entityId,
            VillagerInfoRequestC2SPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() { return ID; }
}
