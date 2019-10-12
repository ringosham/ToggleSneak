package deez.togglesneak.gui;

import java.text.DecimalFormat;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiSlideControl extends GuiButton
{
	public String	label;			// Text displayed next to value
	public float	curValue;		// Current Slider position, from 0 to 1
	public float	minValue;		// Smallest value represented by the Slider
	public float	maxValue;		// Largest value represented by the Slider
	public boolean	isSliding;		// Is Control currently being manipulated?
	public boolean	useIntegers;	// Use Integers to display values
	
	private static DecimalFormat numFormat = new DecimalFormat("#.00");
	
	public GuiSlideControl(int id, int x, int y, int width, int height, String displayString, float minVal, float maxVal, float curVal, boolean useInts)
	{
		super(id, x, y, width, height, (useInts ? displayString + ((int)curVal) : displayString + numFormat.format(curVal)));
		
		this.label			=	displayString;
		this.minValue		=	minVal;
		this.maxValue		=	maxVal;
		this.curValue		=	(curVal - minVal) / (maxVal - minVal);
		this.useIntegers	=	useInts;
	}

	public float GetValueAsFloat()
	{
		return (maxValue - minValue) * curValue + minValue;
	}
	
	public int GetValueAsInt()
	{
		return (int)((maxValue - minValue) * curValue + minValue);
	}
	
    protected float roundValue(float value)
    {
        value = 0.01F * (float)Math.round(value / 0.01F);
        return value;
    }
    
	public String GetLabel()
	{
		if(useIntegers)		return label + GetValueAsInt();
		else				return label + numFormat.format(GetValueAsFloat());
	}
	
	protected void SetLabel()
	{
		displayString = GetLabel();
	}
	
	@Override
	protected int getHoverState(boolean isMouseOver)
	{
		return 0;
	}
	
	@Override
	protected void mouseDragged(Minecraft mc, int mousePosX, int mousePosY)
	{
		if(visible)
		{
			if(this.isSliding)
			{
				curValue = roundValue((float)(mousePosX - (x + 4)) / (float)(width - 8));
				
				if(curValue < 0.0F)
				{
					curValue = 0.0F;
				}
				if(curValue > 1.0F)
				{
					curValue = 1.0F;
				}
				
				SetLabel();
			}
			
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			this.drawTexturedModalRect(x + (int)(curValue * (float)(width - 8)), y, 0, 66, 4, 20);
			this.drawTexturedModalRect(x + (int)(curValue * (float)(width - 8)) + 4, y, 196, 66, 4, 20);
		}
	}
	
	@Override
	public boolean mousePressed(Minecraft mc, int mousePosX, int mousePosY)
	{
		if(super.mousePressed(mc, mousePosX, mousePosY))
		{
			curValue = roundValue((float)(mousePosX - (x + 4)) / (float)(width - 8));
			
			if(curValue < 0.0F)
			{
				curValue = 0.0F;
			}
			if(curValue > 1.0F)
			{
				curValue = 1.0F;
			}
			
			SetLabel();
			this.isSliding = true;
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public void mouseReleased(int mousePosX, int mousePosY)
	{
		this.isSliding = false;
	}
}