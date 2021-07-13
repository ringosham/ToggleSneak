package deez.togglesneak;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public class ToggleSneakEvents {
    public static ToggleSneakEvents instance = new ToggleSneakEvents();

    protected static final Minecraft mc = Minecraft.getMinecraft();

    private long sneakPressStart;
    private long sprintPressStart;
    private Field sprintToggleTimer;

    //In order to handle key down durations, we must execute this in a tick loop.
    //KeyInputEvent is simply too buggy and unreliable to detect key presses
    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        //Ignore server side events
        if (event.side.isServer())
            return;
        //Ignore other EntityPlayers in multiplayer
        if (event.player instanceof EntityOtherPlayerMP)
            return;
        EntityPlayerSP player = (EntityPlayerSP) event.player;
        //In case EntityPlayerSP is not initialized yet
        if (player == null)
            return;
        if (sprintToggleTimer == null)
            sprintToggleTimer = ReflectionHelper.findField(EntityPlayerSP.class, "field_71156_d", "sprintToggleTimer");

        boolean isSneaking = false;
        //Toggle sneak
        if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown() && sneakPressStart == 0) {
            sneakPressStart = System.currentTimeMillis();
        } else if (!Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown()) {
            long diff = System.currentTimeMillis() - sneakPressStart;
            //In debugging, the threshold may not function correctly on low numbers.
            //Also, prevent activating toggle sneak when dismounting, as players would likely get stuck in sneaking if they want to escape.
            if (diff < ToggleSneakMod.optionThreshold && !Status.INSTANCE.isRidingDismount() && ToggleSneakMod.optionToggleSneak) {
                Status.INSTANCE.setSneakToggled(!Status.INSTANCE.isSneakToggled());
            }
            sneakPressStart = 0;
        }
        if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown() && !Status.INSTANCE.isSneakToggled()) {
            Status.INSTANCE.setSneakHeld(true);
            isSneaking = true;
        } else if (Status.INSTANCE.isSneakToggled() || !Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown())
            Status.INSTANCE.setSneakHeld(false);

        isSneaking = isSneaking | Status.INSTANCE.isSneakToggled();
        //Toggle sprint
        //Disables sprinting when not enough hunger (unless creative), have blindness, using an item or the player is not moving, or is already sneaking
        //Hunger is ignored when in creative mode
        if (Status.INSTANCE.isSprintToggled() && (player.getFoodStats().getFoodLevel() > 6 || player.isCreative()) && !player.isPotionActive(MobEffects.BLINDNESS) && player.movementInput.moveForward != 0 && !PlayerEvent.instance.isUseItem() && !isSneaking) {
            player.setSprinting(true);
            //Hopefully this should fix toggle sprint sometimes stops working
            try {
                sprintToggleTimer.set(player, 7);
            } catch (IllegalAccessException ignored) {
            }
        }
        if (Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown() && sprintPressStart == 0) {
            sprintPressStart = System.currentTimeMillis();
        } else if (!Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown()) {
            long diff = System.currentTimeMillis() - sprintPressStart;
            if (diff < ToggleSneakMod.optionThreshold && ToggleSneakMod.optionToggleSprint)
                Status.INSTANCE.setSprintToggled(!Status.INSTANCE.isSprintToggled());
            sprintPressStart = 0;
        }
        //Disable sprint if not enough hunger or being inflicted blindness
        if (Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown() && !Status.INSTANCE.isSprintToggled())
            Status.INSTANCE.setSprintHeld(true);
        else if (Status.INSTANCE.isSneakToggled() || !Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown())
            Status.INSTANCE.setSprintHeld(false);

        //Fly boost
        //Fly boost is purposely only available for creative only
        //While there are cases where servers will let you fly in survival mode, this is to prevent any anticheat from banning players.
        if (player.capabilities.isFlying && player.capabilities.isCreativeMode && Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown() && ToggleSneakMod.optionEnableFlyBoost) {
            Status.INSTANCE.setFlyBoost(true);
            player.capabilities.setFlySpeed(0.05F * (float) ToggleSneakMod.optionFlyBoostAmount);
            if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown())
                player.motionY -= 0.15D * ToggleSneakMod.optionFlyBoostAmount;
            if (Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown())
                player.motionY += 0.15D * ToggleSneakMod.optionFlyBoostAmount;
        } else if (player.capabilities.getFlySpeed() != 0.05F) {
            //Fly boost breaks the original fly speed. Must be manually set to normal values.
            player.capabilities.setFlySpeed(0.05F);
            Status.INSTANCE.setFlyBoost(false);
        }

        //Status text
        Status.INSTANCE.setElytra(player.isElytraFlying());

        Status.INSTANCE.setFly(player.capabilities.isFlying);

        Status.INSTANCE.setRiding(player.isRiding());

        Status.INSTANCE.setRidingDismount((player.isRiding() && Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown()));

        //Double Tapping sprint
        //Overwrite sprintToggleTimer. Double tapping W will set this to 7. Simply setting back to 0 every tick will disable double tapping.
        if (!ToggleSneakMod.optionDoubleTap) {
            try {
                sprintToggleTimer.set(player, 0);
            } catch (IllegalAccessException ignored) {
            }
        }

        //Vanilla sprinting detection
        //E.g double tapping w, and releasing sprint button after holding it.
        int doubleTapTimer = 0;
        try {
            doubleTapTimer = (int) sprintToggleTimer.get(player);
        } catch (IllegalAccessException ignored) {
        }
        boolean vanillaSprint = (doubleTapTimer == 7 || !Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown()) && player.isSprinting() && !Status.INSTANCE.isSprintToggled();
        Status.INSTANCE.setSprintVanilla(vanillaSprint);

    }

    //This event calls in every tick I believe, but allows direct player movement manipulation.
    @SubscribeEvent
    public void onMovementUpdate(InputUpdateEvent event) {
        //Set the sneak flag to true in EntityPlayer does nothing. We have to manipulate the player's movements directly.
        if (Status.INSTANCE.isSneakToggled()) {
            //Sneak animation
            event.getMovementInput().sneak = true;
            //Slow down sneak walking speed. Without it you'll be sneaking at walking speed
            //Most anticheat will bounce you back and possibly ban without it.
            event.getMovementInput().moveStrafe = (float) ((double) event.getMovementInput().moveStrafe * 0.3D);
            event.getMovementInput().moveForward = (float) ((double) event.getMovementInput().moveForward * 0.3D);
        }
    }
}