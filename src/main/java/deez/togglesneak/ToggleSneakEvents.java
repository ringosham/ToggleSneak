package deez.togglesneak;

import deez.togglesneak.gui.GuiOptionsReplace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class ToggleSneakEvents
{
	public static ToggleSneakEvents instance = new ToggleSneakEvents();

	protected static final Minecraft mc = Minecraft.getMinecraft();

	private long startPressTime = 0;
	private long endPressTime = 0;
	
	@SubscribeEvent
	public void GuiOpenEvent(GuiOpenEvent event)
	{
		if(event.getGui() instanceof GuiOptions && mc.world != null)
		{
			event.setGui(new GuiOptionsReplace(new GuiIngameMenu(), mc.gameSettings));
		}
	}

	//KeyInputEvent only fires when the first key down occurs, it does not fire on subsequent key downs.
	@SubscribeEvent
	public void onKeyDown(InputEvent.KeyInputEvent event) {
		if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())) {
			endPressTime = System.currentTimeMillis();
			System.out.println("Sneak key down");
		} else {
			System.out.println("Sneak key up");
			if (endPressTime != 0) {
				System.out.println("Sneak hold time: " + (endPressTime - startPressTime) + "ms");
				endPressTime = 0;
			}
			startPressTime = System.currentTimeMillis();
		}
	}
}