package deez.togglesneak;

import deez.togglesneak.config.GuiTSConfig;
import deez.togglesneak.config.ToggleSneakConfig;
import deez.togglesneak.hud.RenderTextToHUD;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ToggleSneakMod.ModID)
public class ToggleSneakMod
{
	public static final String	ModID		= "togglesneak";

	public ToggleSneakMod() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ToggleSneakConfig.getClientConfig());
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> new GuiTSConfig());
		MinecraftForge.EVENT_BUS.register(new RenderTextToHUD());
		MinecraftForge.EVENT_BUS.register(new ToggleSneakEvents());
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
	}

	private void clientSetup(FMLClientSetupEvent e) {
		// We must override the vanilla settings or we'll conflict
		e.getMinecraftSupplier().get().gameSettings.toggleSprint = false;
		e.getMinecraftSupplier().get().gameSettings.toggleCrouch = false;
	}
}