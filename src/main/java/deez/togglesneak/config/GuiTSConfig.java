package deez.togglesneak.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;

public class GuiTSConfig extends Screen {
    private int headerPos;
    private int footerPos;

    // Local copy of the options, to be saved to file when hitting "Done"
    private boolean optionToggleSneak = ToggleSneakConfig.getInstance().getOptionToggleSneak().get();
    private boolean optionToggleSprint = ToggleSneakConfig.getInstance().getOptionToggleSprint().get();
    private boolean optionShowHUDText = ToggleSneakConfig.getInstance().getOptionShowHUDText().get();
    private boolean optionDoubleTap = ToggleSneakConfig.getInstance().getOptionDoubleTap().get();
    private boolean optionFlyBoost = ToggleSneakConfig.getInstance().getOptionEnableFlyBoost().get();
    private double optionFlyBoostAmount = ToggleSneakConfig.getInstance().getOptionFlyBoostAmount().get();
    private int optionHUDTextPosX = ToggleSneakConfig.getInstance().getOptionHUDTextPosX().get();
    private int optionHUDTextPosY = ToggleSneakConfig.getInstance().getOptionHUDTextPosY().get();
    private int optionThreshold = ToggleSneakConfig.getInstance().getOptionThreshold().get();

    public GuiTSConfig() {
        super(new StringTextComponent("ToggleSneak config"));
    }

    @Override
    public void init() {
        headerPos = this.height / 4 - 52;
        footerPos = this.height - 29;
        
        this.addButton(new Button(this.width / 2 - 98, getRowPos(1), 60, 20, new StringTextComponent(String.valueOf(this.optionToggleSneak)), (button) -> {
            this.optionToggleSneak = !this.optionToggleSneak;
            button.setMessage(new StringTextComponent(String.valueOf(this.optionToggleSneak)));
        }));
        this.addButton(new Button(this.width / 2 + 102, getRowPos(1), 60, 20, new StringTextComponent(String.valueOf(this.optionToggleSprint)), (button) -> {
            this.optionToggleSprint = !this.optionToggleSprint;
            button.setMessage(new StringTextComponent(String.valueOf(this.optionToggleSprint)));
        }));
        this.addButton(new Button(this.width / 2 + 2, getRowPos(2), 60, 20, new StringTextComponent(String.valueOf(this.optionShowHUDText)), (button) -> {
            this.optionShowHUDText = !this.optionShowHUDText;
            button.setMessage(new StringTextComponent(String.valueOf(this.optionShowHUDText)));
        }));
        this.addButton(new Slider(this.width / 2 + 2, getRowPos(3), 150, 20, new StringTextComponent("X Pos: "), new StringTextComponent(""), 1, 400, this.optionHUDTextPosX, false, true, (widget) -> {
            Slider slider = (Slider) widget;
            this.optionHUDTextPosX = slider.getValueInt();
        }));
        this.addButton(new Slider(this.width / 2 + 2, getRowPos(4), 150, 20, new StringTextComponent("Y Pos: "), new StringTextComponent(""), 1, 200, this.optionHUDTextPosY, false, true, (widget) -> {
            Slider slider = (Slider) widget;
            this.optionHUDTextPosY = slider.getValueInt();
        }));
        this.addButton(new Button(this.width / 2 + 2, getRowPos(5), 60, 20, new StringTextComponent(String.valueOf(this.optionDoubleTap)), (button) -> {
            this.optionDoubleTap = !this.optionDoubleTap;
            button.setMessage(new StringTextComponent(String.valueOf(this.optionDoubleTap)));
        }));
        this.addButton(new Button(this.width / 2 - 113, getRowPos(6), 60, 20, new StringTextComponent(String.valueOf(this.optionFlyBoost)), (button) -> {
            this.optionFlyBoost = !this.optionFlyBoost;
            button.setMessage(new StringTextComponent(String.valueOf(this.optionFlyBoost)));
        }));
        this.addButton(new Slider(this.width / 2 + 57, getRowPos(6), 150, 20, new StringTextComponent("x"), new StringTextComponent(""), 1.0F, 10.0F, this.optionFlyBoostAmount, true, true, (widget) -> {
            Slider slider = (Slider) widget;
            this.optionFlyBoostAmount = slider.getValue();
        }));
        this.addButton(new Slider(this.width / 2 + 57, getRowPos(7), 150, 20, new StringTextComponent(""), new StringTextComponent(" tick(s)"), 2, 20, this.optionThreshold, false, true, (widget) -> {
            Slider slider = (Slider) widget;
            this.optionThreshold = slider.getValueInt();
        }));
        this.addButton(new Button(this.width / 2 - 155, footerPos, 150, 20, new StringTextComponent("Save Settings"), (button) -> {
            saveSettings();
            GuiTSConfig.this.closeScreen();
        }));
        this.addButton(new Button(this.width / 2 + 5, footerPos, 150, 20, new StringTextComponent("Cancel Changes"), (button) -> GuiTSConfig.this.closeScreen()));
    }

    private void saveSettings() {
        ToggleSneakConfig.getInstance().setConfig(
                this.optionToggleSneak,
                this.optionToggleSprint,
                this.optionShowHUDText,
                this.optionDoubleTap,
                this.optionFlyBoost,
                this.optionFlyBoostAmount,
                this.optionThreshold,
                this.optionHUDTextPosX,
                this.optionHUDTextPosY
        );
        ToggleSneakConfig.getClientConfig().save();
    }

    public int getRowPos(int rowNumber) {
        byte byte0 = -16;
        return this.height / 4 + ((24 * rowNumber) - 24) + byte0;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        String lblToggleSneak = "Enable ToggleSneak";
        String lblToggleSprint = "Enable ToggleSprint";
        String lblShowHUDText = "Show status on HUD";
        String lblHUDTextPosX = "Horizontal HUD Location";
        String lblHUDTextPosY = "Vertical HUD Location";
        String lblDoubleTap = "Enable Double-Tapping";
        String lblFlyBoost = "Enable Fly Boost";
        String lblFlyBoostAmount = "Fly Boost Multiplier";
        String lblHoldingThreshold = "Holding threshold";

        this.renderDirtBackground(0);

        drawCenteredString(matrixStack, this.font, new StringTextComponent("ToggleSneak Settings"), this.width / 2, headerPos, 16777215);

        drawString(matrixStack, font, lblToggleSneak, this.width / 2 - 100 - this.font.getStringWidth(lblToggleSneak), getRowPos(1) + 6, 16777215);
        drawString(matrixStack, font, lblToggleSprint, this.width / 2 + 100 - this.font.getStringWidth(lblToggleSprint), getRowPos(1) + 6, 16777215);
        drawString(matrixStack, font, lblShowHUDText, this.width / 2 - 3 - this.font.getStringWidth(lblShowHUDText), getRowPos(2) + 6, 16777215);

        drawString(matrixStack, font, lblHUDTextPosX, this.width / 2 - 3 - this.font.getStringWidth(lblHUDTextPosX), getRowPos(3) + 6, 16777215);
        drawString(matrixStack, font, lblHUDTextPosY, this.width / 2 - 3 - this.font.getStringWidth(lblHUDTextPosY), getRowPos(4) + 6, 16777215);

        drawString(matrixStack, font, lblDoubleTap, this.width / 2 - 3 - this.font.getStringWidth(lblDoubleTap), getRowPos(5) + 6, 16777215);
        drawString(matrixStack, font, lblFlyBoost, this.width / 2 - 115 - this.font.getStringWidth(lblFlyBoost), getRowPos(6) + 6, 16777215);
        drawString(matrixStack, font, lblFlyBoostAmount, this.width / 2 + 50 - this.font.getStringWidth(lblFlyBoostAmount), getRowPos(6) + 6, 16777215);

        drawString(matrixStack, font, lblHoldingThreshold, this.width / 2 + 50 - this.font.getStringWidth(lblHoldingThreshold), getRowPos(7) + 6, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}