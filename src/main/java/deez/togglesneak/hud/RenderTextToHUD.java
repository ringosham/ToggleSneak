package deez.togglesneak.hud;

import deez.togglesneak.config.ToggleSneakConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderTextToHUD {
    private static final Minecraft mc = Minecraft.getInstance();

    @SubscribeEvent
    public void RenderGameOverlayEvent(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            if (ToggleSneakConfig.getInstance().getOptionShowHUDText().get()) {
                mc.fontRenderer.drawStringWithShadow(event.getMatrixStack(), Status.INSTANCE.getStatusString(), ToggleSneakConfig.getInstance().getOptionHUDTextPosX().get(), ToggleSneakConfig.getInstance().getOptionHUDTextPosY().get(), 0xffffff);
                //For debugging use
//                mc.fontRenderer.drawStringWithShadow("Strafing speed: " + mc.player.movementInput.moveStrafe, ToggleSneakMod.optionHUDTextPosX , ToggleSneakMod.optionHUDTextPosY + 10, 0xffffff);
//                mc.fontRenderer.drawStringWithShadow("Forward speed: " + mc.player.movementInput.moveForward, ToggleSneakMod.optionHUDTextPosX, ToggleSneakMod.optionHUDTextPosY + 20, 0xffffff);
            }
        }
    }
}