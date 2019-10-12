package deez.togglesneak;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import deez.togglesneak.gui.GuiOptionsReplace;

public class ToggleSneakEvents
{
	public static ToggleSneakEvents instance = new ToggleSneakEvents();

	protected static final Minecraft mc = Minecraft.getMinecraft();
	
	@SubscribeEvent
	public void GuiOpenEvent(GuiOpenEvent event)
	{
		if(event.getGui() instanceof GuiOptions && mc.world != null)
		{
			event.setGui(new GuiOptionsReplace(new GuiIngameMenu(), mc.gameSettings));
		}
	}
}