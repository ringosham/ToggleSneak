package deez.togglesneak;

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
        //The only way to override sneaking is through Forge.
        event.getMovementInput().sneak = CustomMovementInput.sneak;
    }

    public boolean isUseItem() {
        return isUseItem;
    }
}
