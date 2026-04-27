package deez.togglesneak.hud;

import deez.togglesneak.config.TSConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public class RenderTextToHUD {
    public static void render(GuiGraphics guiGraphics, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        if (TSConfig.getInstance().optionShowHUDText) {
            String status = Status.INSTANCE.getStatusString();
            if (!status.isEmpty()) {
                guiGraphics.drawString(mc.font, status, TSConfig.getInstance().optionHUDTextPosX, TSConfig.getInstance().optionHUDTextPosY, 0xffffff);
            }
        }
    }
}
