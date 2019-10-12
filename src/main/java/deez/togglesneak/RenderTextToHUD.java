package deez.togglesneak;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class RenderTextToHUD
{
	public static RenderTextToHUD instance = new RenderTextToHUD();
	
	private static Minecraft mc = Minecraft.getMinecraft();
	private	static String textForHUD = "";
	
    @SubscribeEvent
    public void RenderGameOverlayEvent(RenderGameOverlayEvent event)
    {   	
    	if(event.getType() == RenderGameOverlayEvent.ElementType.TEXT)
    	{
    		if(ToggleSneakMod.optionShowHUDText)
    		{
    			mc.fontRenderer.drawStringWithShadow(textForHUD, ToggleSneakMod.optionHUDTextPosX, ToggleSneakMod.optionHUDTextPosY, 0xffffff);
    		}
    	}
    }
	    
    public static void SetHUDText(String text)
    {
    	textForHUD = text;
    }
}