package deez.togglesneak;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.MobEffects;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.API;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.DecimalFormat;

public class PlayerEvent {
    public static final PlayerEvent instance = new PlayerEvent();
    private static boolean isUseItem = false;
    private boolean sprintHeldAndReleased;
    private boolean isDisabled;
    private boolean sprintDoubleTapped;
    private boolean sprint;

    private PlayerEvent() {
    }

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

    public void UpdateSprint(boolean newValue, boolean doubleTapped) {
        this.sprint = newValue;
        this.sprintDoubleTapped = doubleTapped;
    }

    private void updateStatus(MovementInputFromOptions options, EntityPlayerSP thisPlayer, GameSettings settings) {
        if (ToggleSneakMod.optionShowHUDText) {
            String output = "";

            boolean isFlying = thisPlayer.capabilities.isFlying;
            boolean isRiding = thisPlayer.isRiding();
            boolean isHoldingSneak = settings.keyBindSneak.isKeyDown();
            boolean isHoldingSprint = settings.keyBindSprint.isKeyDown();
            boolean isElytraFlying = thisPlayer.isElytraFlying();

            if (isFlying) {
                DecimalFormat numFormat = new DecimalFormat("#.00");
                if (ToggleSneakMod.optionEnableFlyBoost && isHoldingSprint)
                    output += "[Flying (" + numFormat.format(ToggleSneakMod.optionFlyBoostAmount) + "x boost)]  ";
                else output += "[Flying]  ";
            }

            if (isElytraFlying)
                output += "[Elytra flying]  ";

            if (isRiding) output += "[Riding]  ";

            if (options.sneak) {
                if (isFlying) output += "[Descending]  ";
                else if (isRiding) output += "[Dismounting]  ";
                else if (isHoldingSneak) output += "[Sneaking (Key Held)]  ";
                else output += "[Sneaking (Toggled)]  ";
            } else if (thisPlayer.isSprinting()) {
                if (!isFlying && !isRiding) {
                    //  Detect Vanilla conditions - ToggleSprint disabled, DoubleTapped and Hold & Release
                    boolean isVanilla = this.sprintHeldAndReleased || isDisabled || this.sprintDoubleTapped;

                    if (isHoldingSprint) output += "[Sprinting (Key Held)]";
                    else if (isVanilla) output += "[Sprinting (Vanilla)]";
                    else output += "[Sprinting (Toggled)]";
                }
            }
            RenderTextToHUD.SetHUDText(output);
        }
    }
}
