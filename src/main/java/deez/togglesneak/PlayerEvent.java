package deez.togglesneak;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerEvent {
    public static final PlayerEvent instance = new PlayerEvent();
    private static boolean isUseItem = false;

    private PlayerEvent() {}

    @SubscribeEvent
    public void onUseItemStart(LivingEntityUseItemEvent.Start event) {
        isUseItem = true;
    }

    @SubscribeEvent
    public void onUseItemStop(LivingEntityUseItemEvent.Stop event) {
        isUseItem = false;
    }

    @SubscribeEvent
    public void onUseItemStop(LivingEntityUseItemEvent.Finish event) {
        isUseItem = false;
    }

    @SubscribeEvent
    public void onMovementInput(InputUpdateEvent event) {
        //The only way to override sneaking controls constantly is through Forge.
        //Overriding onLivingUpdate simply doesn't handle sneaking correctly.
        event.getMovementInput().sneak = CustomMovementInput.sneak;
        //We need to override the sneaking speed as well. Otherwise we would be sneaking at walking speed.
        //Do not apply slow down if the sneak keybind is down. That would result in doubling slow speed.
        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        if (CustomMovementInput.sneak && !settings.keyBindSneak.isKeyDown()) {
            event.getMovementInput().moveStrafe = (float) ((double) event.getMovementInput().moveStrafe * 0.3D);
            event.getMovementInput().moveForward = (float) ((double) event.getMovementInput().moveForward * 0.3D);
        }
    }

    public boolean isUseItem() {
        return isUseItem;
    }
}
