package com.quarkz.nonnonsensemc.client;

import com.quarkz.nonnonsensemc.network.VillagerInfoPacket;

/**
 * Estado del HUD del cliente y control de throttling para requests al servidor.
 * Es deliberadamente simple y thread-safe mediante campos volatile.
 */
public final class ClientVillagerHudState {
    private ClientVillagerHudState() {}

    // Últimos datos recibidos
    public static volatile boolean scared = true;
    public static volatile boolean readyToBreed = true;
    public static volatile String home = "None";
    public static volatile String lastSlept = "None";
    public static volatile String golemDetected = "None";

    // Throttling (lado cliente)
    public static volatile int lastVillagerId = -1;
    public static volatile long lastRequestMs = 0L;

    public static void updateFrom(VillagerInfoPacket payload) {
        scared = payload.isScared();
        readyToBreed = payload.isReadyToBreed();
        home = payload.getVillagerHome();
        lastSlept = payload.getVillagerLastSlept();
        golemDetected = payload.getVillagerGolemDetected();
    }

    /**
     * Intenta iniciar una request aplicando throttling. Devuelve true si se debe enviar la request.
     */
    public static boolean beginRequestIfAllowed(int villagerEntityId, long now, long minIntervalMs) {
        boolean sameTarget = (lastVillagerId == villagerEntityId);
        boolean tooSoon = (now - lastRequestMs) < minIntervalMs;
        if (sameTarget && tooSoon) return false;
        lastVillagerId = villagerEntityId;
        lastRequestMs = now;
        return true;
    }
}

