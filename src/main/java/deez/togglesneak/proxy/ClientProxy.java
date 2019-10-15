package deez.togglesneak.proxy;

import api.player.client.ClientPlayerAPI;

import deez.togglesneak.*;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy
{
	@Override
	public void registerEvents(FMLPreInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(RenderTextToHUD.instance);
		MinecraftForge.EVENT_BUS.register(ToggleSneakEvents.instance);
		MinecraftForge.EVENT_BUS.register(PlayerEvent.instance);
	}
	
	@Override
	public void initMod()
	{
		RenderTextToHUD.SetHUDText(ToggleSneakMod.ModID + " for Forge - version " + ToggleSneakMod.ModVersion);
		ClientPlayerAPI.register("ToggleSneak", PlayerBase.class);
	}
}