package com.quarkz.nonnonsensemc.application;

import com.quarkz.nonnonsensemc.domain.VillagerInfo;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.PanicTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Caso de uso: construir un VillagerInfo a partir de un VillagerEntity.
 * Sin dependencias de I/O (red, UI). Testeable y reutilizable.
 */
public class VillagerInfoService {

    public VillagerInfo extract(VillagerEntity villager, Logger logger) {
        boolean scared = isPanicking(villager);
        boolean readyToBreed = villager.isReadyToBreed();

        Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> memories = villager.getBrain().getMemories();

        AtomicReference<String> homeRef = new AtomicReference<>("None");
        memories.get(MemoryModuleType.HOME).ifPresent(memory -> {
            if (logger != null) logger.debug("Villager home memory: {}", memory);
            homeRef.set(memory.toString());
        });

        AtomicReference<String> lastSleptRef = new AtomicReference<>("None");
        memories.get(MemoryModuleType.LAST_SLEPT).ifPresent(memory -> {
            if (logger != null) logger.debug("Villager last slept memory: {}", memory);
            lastSleptRef.set(memory.toString());
        });

        AtomicReference<String> golemDetectedRef = new AtomicReference<>("None");
        memories.get(MemoryModuleType.GOLEM_DETECTED_RECENTLY).ifPresent(memory -> {
            if (logger != null) logger.debug("Villager golem detected recently memory: {}", memory);
            golemDetectedRef.set(memory.toString());
        });

        return new VillagerInfo(
                scared,
                readyToBreed,
                homeRef.get(),
                lastSleptRef.get(),
                golemDetectedRef.get()
        );
    }

    private boolean isPanicking(VillagerEntity villager) {
        List<Task<? super VillagerEntity>> tasks = villager.getBrain().getRunningTasks();
        if (tasks == null || tasks.isEmpty()) return false;
        AtomicBoolean scared = new AtomicBoolean(false);
        tasks.forEach(task -> { if (task instanceof PanicTask) scared.set(true); });
        return scared.get();
    }
}
