package deez.togglesneak;

import java.io.File;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import deez.togglesneak.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

@Mod
(
	modid = ToggleSneakMod.ModID,
	name = ToggleSneakMod.ModName,
	version = ToggleSneakMod.ModVersion,
	guiFactory = "deez.togglesneak.gui.TSGuiFactoryHandler"
)

public class ToggleSneakMod
{
	public static final String	ModID		= "togglesneak";
	public static final String	ModName		= "ToggleSneak";
	public static final String	ModVersion	= "3.1.1";
	
	public static Configuration config					= null;
	public static File			configFile				= null;
	
	public static boolean		optionToggleSprint		= true;
	public static boolean		optionToggleSneak		= true;
	public static boolean		optionShowHUDText		= true;
	public static int			optionHUDTextPosX		= 1;
	public static int			optionHUDTextPosY		= 1;
	public static boolean		optionDoubleTap			= false;
	public static boolean		optionEnableFlyBoost	= false;
	public static double		optionFlyBoostAmount	= 4.0;
	public static int			optionButtonPosition	= 1;
	
	public static boolean		wasSprintDisabled		= false;
	
	@Instance("ToggleSneak")
	public static ToggleSneakMod instance;
	
	@SidedProxy
	(
		clientSide = "deez.togglesneak.proxy.ClientProxy",
		serverSide = "deez.togglesneak.proxy.CommonProxy"
	)
	public static CommonProxy proxy;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent event)
	{
		updateConfig(event.getSuggestedConfigurationFile(), true);
		proxy.registerEvents(event);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.initMod();
	}
	
	public static void reloadConfig()
	{
		updateConfig(configFile, true);
	}
	
	public static void saveConfig()
	{
		updateConfig(configFile, false);
	}
		
	public static void updateConfig(File cfgFile, boolean isLoading)
	{
		Property property;
		
		if(isLoading)
		{
			config = new Configuration(cfgFile);
			config.load();
			configFile = cfgFile;
		}
		
		property = config.get("ToggleSneak", "optionToggleSprint", optionToggleSprint);
		property.setComment("If true, use Sprint Toggling - If false, use vanilla sprinting");
		if(isLoading)	optionToggleSprint = property.getBoolean(true);
		else			property.set(optionToggleSprint);
		
		property = config.get("ToggleSneak", "optionToggleSneak", optionToggleSneak);
		property.setComment("If true, use Sneak Toggling - If false, use vanilla sneaking");
		if(isLoading)	optionToggleSneak = property.getBoolean(true);
		else			property.set(optionToggleSneak);
		
		property = config.get("ToggleSneak", "optionShowHUDText", optionShowHUDText);
		property.setComment("Show movement status (Sneaking, Sprinting, etc) on the HUD.");
		if(isLoading)	optionShowHUDText = property.getBoolean(true);
		else			property.set(optionShowHUDText);

		property = config.get("ToggleSneak", "optionHUDTextPosX", optionHUDTextPosX);
		property.setComment("Sets the horizontal position of the HUD Info. [Far Left = 1, Far Right = 400]");
		if(isLoading)	optionHUDTextPosX = property.getInt();
		else			property.set(optionHUDTextPosX);
		
		property = config.get("ToggleSneak", "optionHUDTextPosY", optionHUDTextPosY);
		property.setComment("Sets the vertical position of the HUD Info. [Top Line = 1, Bottom Line = 200]");
		if(isLoading)	optionHUDTextPosY = property.getInt();
		else			property.set(optionHUDTextPosY);
		
		property = config.get("ToggleSneak", "optionDoubleTap", optionDoubleTap);
		property.setComment("Allow double-tapping the forward key (W) to begin sprinting");
		if(isLoading)	optionDoubleTap = property.getBoolean(false);
		else			property.set(optionDoubleTap);
		
		property = config.get("ToggleSneak", "optionEnableFlyBoost", optionEnableFlyBoost);
		property.setComment("Enable speed boost when flying in creative mode");
		if(isLoading)	optionEnableFlyBoost = property.getBoolean(false);
		else			property.set(optionEnableFlyBoost);
		
		property = config.get("ToggleSneak", "optionFlyBoostAmount", optionFlyBoostAmount);
		property.setComment("The multiplier to use when boosting fly speed");
		if(isLoading)	optionFlyBoostAmount = property.getDouble(4.0);
		else			property.set(optionFlyBoostAmount);
		
		property = config.get("ToggleSneak", "optionButtonPosition", optionButtonPosition);
		property.setComment("The position of the options button on the options screen [Left Column = 1, Right Column = 2]");
		if(isLoading)	optionButtonPosition = property.getInt();
		else			property.set(optionButtonPosition);
		
		config.save();
	}
}