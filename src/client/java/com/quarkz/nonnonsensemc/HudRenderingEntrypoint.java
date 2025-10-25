package com.quarkz.nonnonsensemc;

import com.quarkz.nonnonsensemc.client.ClientVillagerHudState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HudRenderingEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Attach our rendering code to before the chat hud layer. Our layer will render right before the chat. The API will take care of z spacing.
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.of(NonNonSenseMCClient.MOD_ID, "before_chat"), HudRenderingEntrypoint::render);
    }

    static void render(DrawContext context, RenderTickCounter tickCounter) {
        HitResult hit = MinecraftClient.getInstance().crosshairTarget;
        if (!(hit instanceof EntityHitResult entityHit))  return;
        if (!(entityHit.getEntity() instanceof VillagerEntity client_villager)) return;

        // ---- ENVÍO DE REQUEST AL SERVIDOR (throttleado) ----
        NonNonSenseMCClient.maybeRequestFor(client_villager.getId());

        // Profesión y color
        String professionKey = client_villager.getVillagerData().profession().getIdAsString();
        String professionName = professionKey.substring(professionKey.lastIndexOf(":") + 1).replace("none", "Sin profesión");
        int professionColor = switch (professionName) {
            case "farmer" -> 0xFFBFFF00;
            case "librarian" -> 0xFF00BFFF;
            case "cleric" -> 0xFFBF00FF;
            case "armorer" -> 0xFF808080;
            case "butcher" -> 0xFFFF4040;
            case "cartographer" -> 0xFF40FF40;
            case "fisherman" -> 0xFF4080FF;
            case "fletcher" -> 0xFFFFA040;
            case "leatherworker" -> 0xFFB97A57;
            case "mason" -> 0xFFAAAAAA;
            case "shepherd" -> 0xFFFFC0CB;
            case "toolsmith" -> 0xFF404040;
            case "weaponsmith" -> 0xFF404080;
            default -> 0xFFFFFFFF;
        };
        String professionDisplay = "Job: " + professionName;

        // Nivel
        String level = "Level: " + client_villager.getVillagerData().level();
        int levelColor = 0xFF00FF00;

        // Datos del servidor (último paquete recibido)
        boolean scared = ClientVillagerHudState.scared;
        String scaredDisplay = "Scared: " + (scared ? "Yes" : "No");
        int scaredColor = scared ? 0xFFFF0000 : 0xFF00FF00;

        boolean isabletobreed = ClientVillagerHudState.readyToBreed;
        String isAbleToBreedDisplay = "Ready to breed: " + (isabletobreed ? "Yes" : "No");
        int isAbleToBreedColor = isabletobreed ? 0xFF00FF00 : 0xFFFF0000;
        // we extract from the data Villager's golem detected recently memory: true (ttl: 505) we want the true and ttl
        String villagerGolemDetectedMemory = ClientVillagerHudState.golemDetected;
        String villagerGolemDetectedDisplay = "Golem detected: N/A";
        if (!"None".equals(villagerGolemDetectedMemory)) {
            String formattedMemory = villagerGolemDetectedMemory.replace("Villager's golem detected recently memory: ", "");
            villagerGolemDetectedDisplay = "Golem detected: " + formattedMemory;
        }
        int golemDetectedColor = villagerGolemDetectedMemory.contains("true") ? 0xFFFF0000 : 0xFF00FF00;
        String villagerLastSleptMemory = ClientVillagerHudState.lastSlept;
        String villagerLastSleptDisplay = "Last slept: N/A";
        if (!"None".equals(villagerLastSleptMemory)) {
            String formattedMemory = villagerLastSleptMemory.replace("Villager's last slept memory: ", "");
            villagerLastSleptDisplay = "Last slept: " + formattedMemory;
        }
        int lastSleptColor = 0xFF00FFFF;
        String villagerHomeMemory = ClientVillagerHudState.home;
        String villagerHomeDisplay = "Home: N/A";
        if (!"None".equals(villagerHomeMemory)) {
            String formattedMemory =  extraerBlockPos(villagerHomeMemory);
            villagerHomeDisplay = "Home: " + (formattedMemory != null ? formattedMemory : villagerHomeMemory);
        }
        int homeColor = 0xFFFFFF00;



        // Posición de renderizado
        int x = MinecraftClient.getInstance().getWindow().getScaledWidth() / 2 + 10;
        int y = MinecraftClient.getInstance().getWindow().getScaledHeight() / 2 - 40;
        int lineHeight = 12;

        // Render
        var tr = MinecraftClient.getInstance().textRenderer;
        context.drawTextWithShadow(tr, professionDisplay, x, y, professionColor);
        context.drawTextWithShadow(tr, level, x, y + lineHeight, levelColor);
        context.drawTextWithShadow(tr, scaredDisplay, x, y + lineHeight * 2, scaredColor);
        context.drawTextWithShadow(tr, isAbleToBreedDisplay, x, y + lineHeight * 3, isAbleToBreedColor);
        context.drawTextWithShadow(tr, villagerGolemDetectedDisplay, x, y + lineHeight * 4, golemDetectedColor);
        context.drawTextWithShadow(tr, villagerLastSleptDisplay, x, y + lineHeight * 5, lastSleptColor);
        context.drawTextWithShadow(tr, villagerHomeDisplay, x, y + lineHeight * 6, homeColor);
    }
    private static String extraerBlockPos(String texto) {
        Pattern pattern = Pattern.compile("BlockPos\\{[^}]+}");
        Matcher matcher = pattern.matcher(texto);
        if (matcher.find()) {
            return matcher.group();
        }
        return null; // Si no se encuentra
    }
}
