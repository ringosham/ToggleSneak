package deez.togglesneak.gui;

import net.minecraftforge.fml.client.IModGuiFactory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Set;

public class TSGuiFactoryHandler implements IModGuiFactory
{
	@Override
	public void initialize(Minecraft mcInstance)
	{		
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new GuiTSConfig(parentScreen);
	}
	
	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories()
	{
		return null;
	}
}
