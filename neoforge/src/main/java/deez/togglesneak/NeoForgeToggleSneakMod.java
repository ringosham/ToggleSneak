package deez.togglesneak;

import deez.togglesneak.config.TSConfigScreen;
import deez.togglesneak.hud.RenderTextToHUD;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.commands.Commands;

@Mod(ToggleSneakMod.MOD_ID)
public class NeoForgeToggleSneakMod {
    private final ToggleSneakEvents events = new ToggleSneakEvents();

    public NeoForgeToggleSneakMod(IEventBus modEventBus) {
        ToggleSneakMod.init(FMLPaths.CONFIGDIR.get().toFile());

        modEventBus.addListener(this::onClientSetup);
        NeoForge.EVENT_BUS.addListener(this::onClientTick);
        NeoForge.EVENT_BUS.addListener(this::onRenderGui);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        Minecraft.getInstance().options.toggleCrouch().set(false);
        Minecraft.getInstance().options.toggleSprint().set(false);
    }

    private void onClientTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player != null) {
            events.onTick(Minecraft.getInstance().player);
        }
    }

    private void onRenderGui(RenderGuiEvent.Post event) {
        RenderTextToHUD.render(event.getGuiGraphics(), event.getPartialTick().getGameTimeDeltaTicks());
    }

    private void onRegisterCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("togglesneak")
                .executes(context -> {
                    Minecraft.getInstance().setScreen(new TSConfigScreen(null));
                    return 1;
                }));
    }
}
