package deez.togglesneak.config;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.function.Consumer;

public class TSConfigScreen extends Screen {
    private final TSConfig config = TSConfig.getInstance();

    private boolean optionToggleSneak = config.optionToggleSneak;
    private boolean optionToggleSprint = config.optionToggleSprint;
    private boolean optionShowHUDText = config.optionShowHUDText;
    private boolean optionFlyBoost = config.optionEnableFlyBoost;
    private double optionFlyBoostAmount = config.optionFlyBoostAmount;
    private int optionHUDTextPosX = config.optionHUDTextPosX;
    private int optionHUDTextPosY = config.optionHUDTextPosY;
    private int optionThreshold = config.optionThreshold;

    public TSConfigScreen() {
        super(Component.literal("ToggleSneak Config"));
    }

    @Override
    protected void init() {
        int footerPos = this.height - 29;

        // Toggle Sneak
        this.addRenderableWidget(Button.builder(Component.literal(String.valueOf(this.optionToggleSneak)), (button) -> {
            this.optionToggleSneak = !this.optionToggleSneak;
            button.setMessage(Component.literal(String.valueOf(this.optionToggleSneak)));
        }).bounds(this.width / 2 - 98, getRowPos(1), 60, 20).build());

        // Toggle Sprint
        this.addRenderableWidget(Button.builder(Component.literal(String.valueOf(this.optionToggleSprint)), (button) -> {
            this.optionToggleSprint = !this.optionToggleSprint;
            button.setMessage(Component.literal(String.valueOf(this.optionToggleSprint)));
        }).bounds(this.width / 2 + 102, getRowPos(1), 60, 20).build());

        // Show HUD Text
        this.addRenderableWidget(Button.builder(Component.literal(String.valueOf(this.optionShowHUDText)), (button) -> {
            this.optionShowHUDText = !this.optionShowHUDText;
            button.setMessage(Component.literal(String.valueOf(this.optionShowHUDText)));
        }).bounds(this.width / 2 + 2, getRowPos(2), 60, 20).build());

        // HUD Text X Pos
        this.addRenderableWidget(new SimpleSlider(this.width / 2 + 2, getRowPos(3), 150, 20, Component.literal("X Pos: "), 1, 400, this.optionHUDTextPosX,
                (val) -> this.optionHUDTextPosX = val.intValue(), Component.empty()));

        // HUD Text Y Pos
        this.addRenderableWidget(new SimpleSlider(this.width / 2 + 2, getRowPos(4), 150, 20, Component.literal("Y Pos: "), 1, 200, this.optionHUDTextPosY,
                (val) -> this.optionHUDTextPosY = val.intValue(), Component.empty()));

        // Fly Boost
        this.addRenderableWidget(Button.builder(Component.literal(String.valueOf(this.optionFlyBoost)), (button) -> {
            this.optionFlyBoost = !this.optionFlyBoost;
            button.setMessage(Component.literal(String.valueOf(this.optionFlyBoost)));
        }).bounds(this.width / 2 - 113, getRowPos(6), 60, 20).tooltip(Tooltip.create(Component.literal("Fly boosting only works in creative mode"))).build());

        // Fly Boost Amount
        this.addRenderableWidget(new SimpleSlider(this.width / 2 + 57, getRowPos(6), 150, 20, Component.literal("x"), 1.0, 10.0, this.optionFlyBoostAmount,
                (val) -> this.optionFlyBoostAmount = val, Component.empty(), Tooltip.create(Component.literal("Some servers may not like fly boosting at very high speed. Be careful with this option"))));

        // Holding Threshold
        this.addRenderableWidget(new SimpleSlider(this.width / 2 + 57, getRowPos(7), 150, 20, Component.literal(""), 1, 10, this.optionThreshold,
                (val) -> this.optionThreshold = val.intValue() + 1, Component.literal(" tick(s)"), Tooltip.create(Component.literal("How long to hold the sneak button to toggle. Each tick is 50ms"))));

        // Save
        this.addRenderableWidget(Button.builder(Component.literal("Save Settings"), (button) -> {
            saveSettings();
            this.onClose();
        }).bounds(this.width / 2 - 155, footerPos, 150, 20).build());

        // Cancel
        this.addRenderableWidget(Button.builder(Component.literal("Cancel Changes"), (button) -> {
            this.onClose();
        }).bounds(this.width / 2 + 5, footerPos, 150, 20).build());
    }

    private void saveSettings() {
        config.optionToggleSneak = this.optionToggleSneak;
        config.optionToggleSprint = this.optionToggleSprint;
        config.optionShowHUDText = this.optionShowHUDText;
        config.optionEnableFlyBoost = this.optionFlyBoost;
        config.optionFlyBoostAmount = this.optionFlyBoostAmount;
        config.optionThreshold = this.optionThreshold;
        config.optionHUDTextPosX = this.optionHUDTextPosX;
        config.optionHUDTextPosY = this.optionHUDTextPosY;
        TSConfig.save();
    }

    public int getRowPos(int rowNumber) {
        return this.height / 4 + ((24 * rowNumber) - 24) - 16;
    }

    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);

        int headerPos = this.height / 4 - 52;
        graphics.drawCenteredString(this.font, "ToggleSneak Settings", this.width / 2, headerPos, 0xFFFFFFFF);

        String lblToggleSneak = "Enable ToggleSneak";
        String lblToggleSprint = "Enable ToggleSprint";
        String lblShowHUDText = "Show status on HUD";
        String lblHUDTextPosX = "Horizontal HUD Location";
        String lblHUDTextPosY = "Vertical HUD Location";
        String lblFlyBoost = "Enable Fly Boost";
        String lblFlyBoostAmount = "Fly Boost Multiplier";
        String lblHoldingThreshold = "Sneak toggle threshold";

        graphics.drawString(this.font, lblToggleSneak, this.width / 2 - 100 - this.font.width(lblToggleSneak), getRowPos(1) + 6, 0xFFFFFFFF);
        graphics.drawString(this.font, lblToggleSprint, this.width / 2 + 100 - this.font.width(lblToggleSprint), getRowPos(1) + 6, 0xFFFFFFFF);
        graphics.drawString(this.font, lblShowHUDText, this.width / 2 - 3 - this.font.width(lblShowHUDText), getRowPos(2) + 6, 0xFFFFFFFF);

        graphics.drawString(this.font, lblHUDTextPosX, this.width / 2 - 3 - this.font.width(lblHUDTextPosX), getRowPos(3) + 6, 0xFFFFFFFF);
        graphics.drawString(this.font, lblHUDTextPosY, this.width / 2 - 3 - this.font.width(lblHUDTextPosY), getRowPos(4) + 6, 0xFFFFFFFF);

        graphics.drawString(this.font, lblFlyBoost, this.width / 2 - 115 - this.font.width(lblFlyBoost), getRowPos(6) + 6, 0xFFFFFFFF);
        graphics.drawString(this.font, lblFlyBoostAmount, this.width / 2 + 50 - this.font.width(lblFlyBoostAmount), getRowPos(6) + 6, 0xFFFFFFFF);

        graphics.drawString(this.font, lblHoldingThreshold, this.width / 2 + 50 - this.font.width(lblHoldingThreshold), getRowPos(7) + 6, 0xFFFFFFFF);
    }

    private static class SimpleSlider extends AbstractSliderButton {
        private final Component prefix;
        private final Component suffix;
        private final double min;
        private final double max;
        private final Consumer<Double> callback;

        public SimpleSlider(int x, int y, int width, int height, Component prefix, double min, double max, double defaultValue, Consumer<Double> callback, Component suffix) {
            super(x, y, width, height, Component.empty(), (defaultValue - min) / (max - min));
            this.prefix = prefix;
            this.suffix = suffix;
            this.min = min;
            this.max = max;
            this.callback = callback;
            this.updateMessage();
        }

        public SimpleSlider(int x, int y, int width, int height, Component prefix, double min, double max, double defaultValue, Consumer<Double> callback, Component suffix, Tooltip tooltip) {
            this(x, y, width, height, prefix, min, max, defaultValue, callback, suffix);
            this.setTooltip(tooltip);
        }

        @Override
        protected void updateMessage() {
            double value = min + (max - min) * this.value;
            if (max - min > 1.0 && (min == (int)min && max == (int)max)) {
                this.setMessage(Component.empty().append(prefix).append(String.valueOf((int) Math.round(value))).append(suffix));
            } else {
                this.setMessage(Component.empty().append(prefix).append(String.format("%.1f", value)).append(suffix));
            }
        }

        @Override
        protected void applyValue() {
            callback.accept(min + (max - min) * this.value);
        }
    }
}
