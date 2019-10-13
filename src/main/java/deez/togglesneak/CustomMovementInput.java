package deez.togglesneak;

import java.text.DecimalFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.MovementInputFromOptions;

/*
 *		ToggleSneak's replacement for MovementInputFromOptions
 */
public class CustomMovementInput {
    public boolean isDisabled;
    public boolean canDoubleTap;

    public boolean sprint = false;
    public boolean sprintHeldAndReleased = false;
    public boolean sprintDoubleTapped = false;
    public static boolean sneak = false;

    private long lastPressed;
    private long lastSprintPressed;
    private boolean handledSneakPress;
    private boolean handledSprintPress;
    private boolean wasRiding;

    /*
     * 		MovementInputFromOptions.updatePlayerMoveState()
     */
    public void update(Minecraft mc, MovementInputFromOptions options, EntityPlayerSP thisPlayer) {
        options.moveStrafe = 0.0F;
        options.moveForward = 0.0F;

        GameSettings settings = mc.gameSettings;

        if (settings.keyBindForward.isKeyDown()) {
            ++options.moveForward;
        }

        if (settings.keyBindBack.isKeyDown()) {
            --options.moveForward;
        }

        if (settings.keyBindLeft.isKeyDown()) {
            ++options.moveStrafe;
        }

        if (settings.keyBindRight.isKeyDown()) {
            --options.moveStrafe;
        }

        options.jump = settings.keyBindJump.isKeyDown();

        //
        // Sneak Toggle - Essentially the same as old ToggleSneak
        //

        // Check to see if Enabled - Added 6/17/14 to provide option to disable Sneak Toggle
        if (ToggleSneakMod.optionToggleSneak) {
            // Key Pressed
            if (settings.keyBindSneak.isKeyDown() && !this.handledSneakPress) {
                // Descend if we are flying, note if we were riding (so we can unsneak once dismounted)
                if (thisPlayer.isRiding() || thisPlayer.capabilities.isFlying) {
                    options.sneak = true;
                    sneak = true;
                    this.wasRiding = thisPlayer.isRiding();
                } else {
                    options.sneak = !options.sneak;
                    sneak = options.sneak;
                }

                this.lastPressed = System.currentTimeMillis();
                this.handledSneakPress = true;
            }

            // Key Released
            if (!settings.keyBindSneak.isKeyDown() && this.handledSneakPress) {
                // If we are flying or riding, stop sneaking after descent/dismount.
                if (thisPlayer.capabilities.isFlying || this.wasRiding) {
                    options.sneak = false;
                    this.wasRiding = false;
                    sneak = false;
                }
                // If the key was held down for more than 300ms, stop sneaking upon release.
                else if (System.currentTimeMillis() - this.lastPressed > 300L) {
                    options.sneak = false;
					sneak = false;
                }

                this.handledSneakPress = false;
            }
        } else {
            options.sneak = settings.keyBindSneak.isKeyDown();
			sneak = options.sneak;
        }

        if (options.sneak) {
            options.moveStrafe = (float) ((double) options.moveStrafe * 0.3D);
            options.moveForward = (float) ((double) options.moveForward * 0.3D);
        }

        //
        //  Sprint Toggle - Updated 6/18/2014
        //

        // Establish conditions where we don't want to start a sprint - sneaking, riding, flying, hungry
        boolean enoughHunger = (float) thisPlayer.getFoodStats().getFoodLevel() > 6.0F || thisPlayer.capabilities.isFlying;
        boolean canSprint = !options.sneak && !thisPlayer.isRiding() && !thisPlayer.capabilities.isFlying && enoughHunger;

        isDisabled = !ToggleSneakMod.optionToggleSprint;
        canDoubleTap = ToggleSneakMod.optionDoubleTap;

        // Key Pressed
        if ((canSprint || isDisabled) && settings.keyBindSprint.isKeyDown() && !this.handledSprintPress) {
            if (!isDisabled) {
                this.sprint = !this.sprint;
                this.lastSprintPressed = System.currentTimeMillis();
                this.handledSprintPress = true;
                this.sprintHeldAndReleased = false;
            }
        }

        // Key Released
        if ((canSprint || isDisabled) && !settings.keyBindSprint.isKeyDown() && this.handledSprintPress) {
            // Was key held for longer than 300ms?  If so, mark it so we can resume vanilla behavior
            if (System.currentTimeMillis() - this.lastSprintPressed > 300L) {
                this.sprintHeldAndReleased = true;
            }
            this.handledSprintPress = false;
        }

        UpdateStatus(options, thisPlayer, settings);
    }

    public void UpdateSprint(boolean newValue, boolean doubleTapped) {
        this.sprint = newValue;
        this.sprintDoubleTapped = doubleTapped;
    }

    //
    //  Detect any changes in movement state and update HUD - Added 4/14/2014
    //
    private void UpdateStatus(MovementInputFromOptions options, EntityPlayerSP thisPlayer, GameSettings settings) {
        if (ToggleSneakMod.optionShowHUDText) {
            String output = "";

            boolean isFlying = thisPlayer.capabilities.isFlying;
            boolean isRiding = thisPlayer.isRiding();
            boolean isHoldingSneak = settings.keyBindSneak.isKeyDown();
            boolean isHoldingSprint = settings.keyBindSprint.isKeyDown();

            if (isFlying) {
                DecimalFormat numFormat = new DecimalFormat("#.00");
                if (ToggleSneakMod.optionEnableFlyBoost && isHoldingSprint)
                    output += "[Flying (" + numFormat.format(ToggleSneakMod.optionFlyBoostAmount) + "x boost)]  ";
                else output += "[Flying]  ";
            }
            if (isRiding) output += "[Riding]  ";

            if (options.sneak) {
                if (isFlying) output += "[Descending]  ";
                else if (isRiding) output += "[Dismounting]  ";
                else if (isHoldingSneak) output += "[Sneaking (Key Held)]  ";
                else output += "[Sneaking (Toggled)]  ";
            } else if (this.sprint) {
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