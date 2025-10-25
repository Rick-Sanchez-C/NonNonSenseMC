package com.quarkz.nonnonsensemc.domain;

/**
 * DTO inmutable con la información relevante de un aldeano.
 * Mantiene tipos simples; el formateo se deja a la capa de presentación (HUD).
 */
public record VillagerInfo(
        boolean scared,
        boolean readyToBreed,
        String homeMemory,
        String lastSleptMemory,
        String golemDetectedMemory
) {
}

