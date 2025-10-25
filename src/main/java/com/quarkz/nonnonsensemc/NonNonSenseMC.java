package com.quarkz.nonnonsensemc;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quarkz.nonnonsensemc.infrastructure.network.ServerNetworking;

public class NonNonSenseMC implements ModInitializer {

    public static final String MOD_ID = "nonnonsensemc";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static boolean isRegistered = false;

    @Override
    public void onInitialize() {
        // Registro de networking del lado servidor
        ServerNetworking.register();
        LOGGER.info("{} inicializado", MOD_ID);
    }
}

// Registro de los canales de red y handlers para la comunicación cliente-servidor de información de aldeanos
