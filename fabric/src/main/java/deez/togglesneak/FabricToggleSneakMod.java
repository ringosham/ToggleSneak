package deez.togglesneak;

import deez.togglesneak.config.TSConfigScreen;
import deez.togglesneak.hud.RenderTextToHUD;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public class FabricToggleSneakMod implements ClientModInitializer {
    private final ToggleSneakEvents events = new ToggleSneakEvents();
    private boolean initializedOptions = false;

    @Override
    public void onInitializeClient() {
        ToggleSneakMod.init(FabricLoader.getInstance().getConfigDir().toFile());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                if (!initializedOptions) {
                    client.options.toggleCrouch().set(false);
                    client.options.toggleSprint().set(false);
                    initializedOptions = true;
                }
                events.onTick(client.player);
            }
        });

        HudElementRegistry.attachElementAfter(VanillaHudElements.BOSS_BAR,
                Identifier.fromNamespaceAndPath("togglesneak", "settings"),
                (GuiGraphics guiGraphics, DeltaTracker tickDelta) -> RenderTextToHUD.render(guiGraphics, tickDelta.getGameTimeDeltaTicks())
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("togglesneak")
                    .executes(context -> {
                        Minecraft.getInstance().setScreen(new TSConfigScreen(null));
                        return 1;
                    }));
        });
    }
}
