package deez.togglesneak;

import deez.togglesneak.config.TSConfigScreen;
import deez.togglesneak.hud.RenderTextToHUD;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public class FabricToggleSneakMod implements ClientModInitializer {
    private final ToggleSneakEvents events = new ToggleSneakEvents();

    @Override
    public void onInitializeClient() {
        ToggleSneakMod.init(FabricLoader.getInstance().getConfigDir().toFile());

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                events.onTick(client.player);
            }
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            client.options.toggleCrouch().set(false);
            client.options.toggleSprint().set(false);
        });

        HudElementRegistry.attachElementAfter(VanillaHudElements.BOSS_BAR,
                Identifier.fromNamespaceAndPath("togglesneak", "settings"),
                (GuiGraphics guiGraphics, DeltaTracker tickDelta) -> RenderTextToHUD.render(guiGraphics)
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("togglesneak")
                    .executes(context -> {
                        // Due to Fabric API hooking before the chat window closes,
                        // the chat window will call onClose() AFTER we set the screen to our config,
                        // which immediately sets the screen to null. Therefore, we need to delay this by a tick.
                        Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(new TSConfigScreen()));
                        return 1;
                    }));
        });
    }
}
