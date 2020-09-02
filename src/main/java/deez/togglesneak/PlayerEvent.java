package deez.togglesneak;

import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerEvent {
    public static final PlayerEvent instance = new PlayerEvent();
    private static boolean isUseItem = false;

    private PlayerEvent() {
    }

    //These events are to simply check if the player is using an item. Like food, potions and bucket of milk.
    //As you can't sprint when you're using an item.
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

    public boolean isUseItem() {
        return isUseItem;
    }
}
