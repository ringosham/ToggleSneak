package deez.togglesneak.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ToggleSneakConfig {
    private static final ToggleSneakConfig INSTANCE;
    private static final ForgeConfigSpec clientConfig;

    // Config values
    private final ForgeConfigSpec.BooleanValue optionToggleSprint;
    private final ForgeConfigSpec.BooleanValue optionToggleSneak;
    private final ForgeConfigSpec.BooleanValue optionShowHUDText;
    private final ForgeConfigSpec.BooleanValue optionDoubleTap;
    private final ForgeConfigSpec.BooleanValue optionEnableFlyBoost;
    private final ForgeConfigSpec.DoubleValue optionFlyBoostAmount;
    private final ForgeConfigSpec.IntValue optionThreshold;
    private final ForgeConfigSpec.IntValue optionHUDTextPosX;
    private final ForgeConfigSpec.IntValue optionHUDTextPosY;


    static {
        Pair<ToggleSneakConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ToggleSneakConfig::new);
        clientConfig = specPair.getRight();
        INSTANCE = specPair.getLeft();
    }

    private ToggleSneakConfig(ForgeConfigSpec.Builder configSpecBuilder) {
        optionToggleSprint = configSpecBuilder.comment("If true, use Sprint Toggling - If false, use vanilla sprinting").define("optionToggleSprint", true);
        optionToggleSneak = configSpecBuilder.comment("If true, use Sneak Toggling - If false, use vanilla sneaking").define("optionToggleSneak", true);
        optionShowHUDText = configSpecBuilder.comment("Show movement status (Sneaking, Sprinting, etc) on the HUD.").define("optionShowHUDText", true);
        optionDoubleTap = configSpecBuilder.comment("Allow double-tapping the forward key (W) to begin sprinting").define("optionDoubleTap", false);
        optionEnableFlyBoost = configSpecBuilder.comment("Enable speed boost when flying in creative mode").define("optionEnableFlyBoost", false);
        optionFlyBoostAmount = configSpecBuilder.comment("The multiplier to use when boosting fly speed").defineInRange("optionFlyBoostAmount", 4.0, 1.0, 10.0);
        optionThreshold = configSpecBuilder.comment("Controls how short the player can tap the sneak key to toggle in ticks. [Minimum = 2 (100ms), Maximum = 20 (1s)]").defineInRange("optionThreshold", 5, 2, 20);
        optionHUDTextPosX = configSpecBuilder.comment("Sets the horizontal position of the HUD Info. [Far Left = 1, Far Right = 400]").defineInRange("optionHUDTextPosX", 1, 1, 400);
        optionHUDTextPosY = configSpecBuilder.comment("Sets the vertical position of the HUD Info. [Top line = 1, Bottom line = 200]").defineInRange("optionHUDTextPosY", 1, 1, 200);
    }

    public static ToggleSneakConfig getInstance() {
        return INSTANCE;
    }

    public static ForgeConfigSpec getClientConfig() {
        return clientConfig;
    }

    public void setConfig(boolean optionToggleSprint, boolean optionToggleSneak, boolean optionShowHUDText, boolean optionDoubleTap, boolean optionEnableFlyBoost, double optionFlyBoostAmount, int optionThreshold, int optionHUDTextPosX, int optionHUDTextPosY) {
        this.optionToggleSprint.set(optionToggleSprint);
        this.optionToggleSneak.set(optionToggleSneak);
        this.optionShowHUDText.set(optionShowHUDText);
        this.optionDoubleTap.set(optionDoubleTap);
        this.optionEnableFlyBoost.set(optionEnableFlyBoost);
        this.optionFlyBoostAmount.set(optionFlyBoostAmount);
        this.optionThreshold.set(optionThreshold);
        this.optionHUDTextPosX.set(optionHUDTextPosX);
        this.optionHUDTextPosY.set(optionHUDTextPosY);
    }

    public ForgeConfigSpec.BooleanValue getOptionToggleSprint() {
        return optionToggleSprint;
    }

    public ForgeConfigSpec.BooleanValue getOptionToggleSneak() {
        return optionToggleSneak;
    }

    public ForgeConfigSpec.BooleanValue getOptionShowHUDText() {
        return optionShowHUDText;
    }

    public ForgeConfigSpec.BooleanValue getOptionDoubleTap() {
        return optionDoubleTap;
    }

    public ForgeConfigSpec.BooleanValue getOptionEnableFlyBoost() {
        return optionEnableFlyBoost;
    }

    public ForgeConfigSpec.DoubleValue getOptionFlyBoostAmount() {
        return optionFlyBoostAmount;
    }

    public ForgeConfigSpec.IntValue getOptionThreshold() {
        return optionThreshold;
    }

    public ForgeConfigSpec.IntValue getOptionHUDTextPosX() {
        return optionHUDTextPosX;
    }

    public ForgeConfigSpec.IntValue getOptionHUDTextPosY() {
        return optionHUDTextPosY;
    }
}
