package deez.togglesneak;

import deez.togglesneak.config.ToggleSneakConfig;
import deez.togglesneak.hud.Status;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Pose;
import net.minecraft.potion.Effects;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;

public class ToggleSneakEvents {
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
        //In case EntityPlayerSP is not initialized yet
        if (event.player == null) {
            sprintToggleTimer = null;
            return;
        }
        //Ignore other EntityPlayers in multiplayer, or custom modded EntityPlayers
        if (!(event.player instanceof ClientPlayerEntity))
            return;

        ClientPlayerEntity player = (ClientPlayerEntity) event.player;
        if (sprintToggleTimer == null)
            sprintToggleTimer = ObfuscationReflectionHelper.findField(ClientPlayerEntity.class, "field_71156_d");

        boolean isSneaking = false;
        //Toggle sneak
        if (Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown() && sneakPressStart == 0) {
            sneakPressStart = System.currentTimeMillis();
        } else if (!Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown()) {
            long diff = System.currentTimeMillis() - sneakPressStart;
            //Minecraft polls for key presses are tied to client game ticks (in other words, 20 times per second)
            //Therefore the fastest key press (down and release) possible is 100ms, which is 2 ticks.
            //Also, prevent activating toggle sneak when dismounting, as players would likely get stuck in sneaking if they want to escape.
            if (diff / (ToggleSneakConfig.getInstance().getOptionThreshold().get() * 50) < ToggleSneakConfig.getInstance().getOptionThreshold().get() && !Status.INSTANCE.isRidingDismount() && ToggleSneakConfig.getInstance().getOptionToggleSneak().get()) {
                Status.INSTANCE.setSneakToggled(!Status.INSTANCE.isSneakToggled());
            }
            sneakPressStart = 0;
        }
        if (Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown() && !Status.INSTANCE.isSneakToggled()) {
            Status.INSTANCE.setSneakHeld(true);
            isSneaking = true;
        } else if (Status.INSTANCE.isSneakToggled() || !Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown())
            Status.INSTANCE.setSneakHeld(false);

        isSneaking = isSneaking || Status.INSTANCE.isSneakToggled();
        //Toggle sprint
        //Disables sprinting when the following conditions met:
        // 1. Not enough hunger (unless creative)
        // 2. Have blindness
        // 3. Using an item (Holding right click with an item)
        // 4 The player is not moving,
        // 5. Sneaking
        // 6. Crouching

        //Obtain the use count (Basically if the player is using an item, like shields, food, bows, potions, etc.)
        //Why not use Forge events?
        //Because you can't detect if the player switches to another item while using it,
        //essentially stopping using the item without trigger any of the forge events
        if (Status.INSTANCE.isSprintToggled()
                && (player.getFoodStats().getFoodLevel() > 6 || player.isCreative())
                && !player.isPotionActive(Effects.BLINDNESS)
                && player.movementInput.moveForward > 0
                && player.getItemInUseCount() <= 0
                && !isSneaking
                && !(player.getPose() == Pose.SWIMMING && !player.isInWater() && !player.isElytraFlying())) {
            player.setSprinting(true);
        }

        if (Minecraft.getInstance().gameSettings.keyBindSprint.isKeyDown() && sprintPressStart == 0) {
            sprintPressStart = System.currentTimeMillis();
        } else if (!Minecraft.getInstance().gameSettings.keyBindSprint.isKeyDown()) {
            long diff = System.currentTimeMillis() - sprintPressStart;
            if (diff / (ToggleSneakConfig.getInstance().getOptionThreshold().get() * 50) < ToggleSneakConfig.getInstance().getOptionThreshold().get() && ToggleSneakConfig.getInstance().getOptionToggleSprint().get())
                Status.INSTANCE.setSprintToggled(!Status.INSTANCE.isSprintToggled());
            sprintPressStart = 0;
        }
        //Disable sprint if not enough hunger or being inflicted blindness
        if (Minecraft.getInstance().gameSettings.keyBindSprint.isKeyDown() && !Status.INSTANCE.isSprintToggled())
            Status.INSTANCE.setSprintHeld(true);
        else if (Status.INSTANCE.isSneakToggled() || !Minecraft.getInstance().gameSettings.keyBindSprint.isKeyDown())
            Status.INSTANCE.setSprintHeld(false);

        //Fly boost
        //Fly boost is purposely only available for creative only
        //While there are cases where servers will let you fly in survival mode, this is to prevent any anticheat from banning players.
        if (player.abilities.isFlying && player.abilities.isCreativeMode && Minecraft.getInstance().gameSettings.keyBindSprint.isKeyDown() && ToggleSneakConfig.getInstance().getOptionEnableFlyBoost().get()) {
            Status.INSTANCE.setFlyBoost(true);
            player.abilities.setFlySpeed(0.05F * ToggleSneakConfig.getInstance().getOptionFlyBoostAmount().get().floatValue());
            if (Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown())
                player.setMotion(player.getMotion().subtract(0, 0.15D * ToggleSneakConfig.getInstance().getOptionFlyBoostAmount().get() , 0));
            if (Minecraft.getInstance().gameSettings.keyBindJump.isKeyDown())
                player.setMotion(player.getMotion().add(0, 0.15D * ToggleSneakConfig.getInstance().getOptionFlyBoostAmount().get() , 0));
        } else if (player.abilities.getFlySpeed() != 0.05F) {
            //Fly boost breaks the original fly speed. Must be manually set to normal values.
            player.abilities.setFlySpeed(0.05F);
            Status.INSTANCE.setFlyBoost(false);
        }

        //Status text
        Status.INSTANCE.setElytra(player.isElytraFlying());

        Status.INSTANCE.setFly(player.abilities.isFlying);

        Status.INSTANCE.setRiding(player.getRidingEntity() != null);

        Status.INSTANCE.setRidingDismount((player.getRidingEntity() != null && Minecraft.getInstance().gameSettings.keyBindSneak.isKeyDown()));

        Status.INSTANCE.setSwimming(player.isSwimming());

        // The definition of crouching internally is different
        // In code, crouching is defined when you are in the process of getting in or out of a 1 block hold
        // The "crouching" we see is actually a swimming pose, except the player is not in water
        Status.INSTANCE.setCrouching(player.getPose() == Pose.SWIMMING && !player.isInWater() && !player.isElytraFlying() && !player.isOnLadder());

        //Double Tapping sprint
        //Overwrite sprintToggleTimer. Double tapping W will set this to 7. Simply setting back to 0 every tick will disable double tapping.
        if (!ToggleSneakConfig.getInstance().getOptionDoubleTap().get()) {
            try {
                sprintToggleTimer.set(player, 0);
            } catch (IllegalAccessException ignored) {
            }
        }

        //Vanilla sprinting detection
        //E.g. double tapping w, and releasing sprint button after holding it.
        int doubleTapTimer = 0;
        try {
            doubleTapTimer = (int) sprintToggleTimer.get(player);
        } catch (IllegalAccessException ignored) {
        }
        boolean vanillaSprint = (doubleTapTimer == 7 || !Minecraft.getInstance().gameSettings.keyBindSprint.isKeyDown()) && player.isSprinting() && !Status.INSTANCE.isSprintToggled();
        Status.INSTANCE.setSprintVanilla(vanillaSprint);

    }

    //This event calls in every tick I believe, but allows direct player movement manipulation.
    @SubscribeEvent
    public void onMovementUpdate(InputUpdateEvent event) {
        //Set the sneak flag to true in EntityPlayer does nothing. We have to manipulate the player's movements directly.
        if (Status.INSTANCE.isSneakToggled()) {
            event.getMovementInput().sneaking = true;
        }
    }
}