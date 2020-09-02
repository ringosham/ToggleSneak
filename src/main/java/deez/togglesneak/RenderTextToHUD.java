package deez.togglesneak;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderTextToHUD {
    public static RenderTextToHUD instance = new RenderTextToHUD();

    private static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void RenderGameOverlayEvent(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            if (ToggleSneakMod.optionShowHUDText) {
                mc.fontRenderer.drawStringWithShadow(Status.INSTANCE.getStatusString(), ToggleSneakMod.optionHUDTextPosX, ToggleSneakMod.optionHUDTextPosY, 0xffffff);
                //For debugging use
//                mc.fontRenderer.drawStringWithShadow("Strafing speed: " + mc.player.movementInput.moveStrafe, ToggleSneakMod.optionHUDTextPosX , ToggleSneakMod.optionHUDTextPosY + 10, 0xffffff);
//                mc.fontRenderer.drawStringWithShadow("Forward speed: " + mc.player.movementInput.moveForward, ToggleSneakMod.optionHUDTextPosX, ToggleSneakMod.optionHUDTextPosY + 20, 0xffffff);
            }
        }
    }
}