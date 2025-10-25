package com.quarkz.nonnonsensemc.network;

import com.quarkz.nonnonsensemc.NonNonSenseMC;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class VillagerInfoPacket implements CustomPayload {

    // ID estático y tipado
    public static final CustomPayload.Id<VillagerInfoPacket> ID =
            new CustomPayload.Id<>(Identifier.of(NonNonSenseMC.MOD_ID, "villager_info"));

    // Campos (usa primitivos cuando puedas)
    private final boolean scared;
    private final boolean readytobreed;
    private final String villager_golem_detected;
    private final String villager_home;
    private final String villager_last_slept;

    public VillagerInfoPacket(boolean scared, boolean readytobreed, String villagerHome, String villagerLastSlept, String villagerGolemDetected) {
        this.scared = scared;
        this.readytobreed = readytobreed;
        this.villager_home = villagerHome;
        this.villager_last_slept = villagerLastSlept;
        this.villager_golem_detected = villagerGolemDetected;
    }

    // Getters que extraen del objeto (no del buffer)
    public boolean isScared() {
        return scared;
    }

    public boolean isReadyToBreed() {
        return readytobreed;
    }
    public String getVillagerHome() {
        return villager_home;
    }
    public String getVillagerLastSlept() {
        return villager_last_slept;
    }
    public String getVillagerGolemDetected() {
        return villager_golem_detected;
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    // CODEC con PacketByteBuf + getters correctos + constructor
    public static final PacketCodec<PacketByteBuf, VillagerInfoPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, VillagerInfoPacket::isScared,
            PacketCodecs.BOOLEAN, VillagerInfoPacket::isReadyToBreed,
            PacketCodecs.STRING, packet -> packet.villager_home,
            PacketCodecs.STRING, packet -> packet.villager_last_slept,
            PacketCodecs.STRING, packet -> packet.villager_golem_detected,
            VillagerInfoPacket::new
    );
}
