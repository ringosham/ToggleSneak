package deez.togglesneak;

import deez.togglesneak.gui.GuiOptionsReplace;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.init.MobEffects;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ToggleSneakEvents {
    public static ToggleSneakEvents instance = new ToggleSneakEvents();

    protected static final Minecraft mc = Minecraft.getMinecraft();

    private long sneakPressStart;
    private long sprintPressStart;

    @SubscribeEvent
    public void GuiOpenEvent(GuiOpenEvent event) {
        if (event.getGui() instanceof GuiOptions && mc.world != null) {
            event.setGui(new GuiOptionsReplace(new GuiIngameMenu(), mc.gameSettings));
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        //Ignore server side events
        if (event.side.isServer())
            return;
        EntityPlayerSP player = (EntityPlayerSP) event.player;
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
        if (Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown() && !Status.INSTANCE.isSneakToggled())
            Status.INSTANCE.setSneakHeld(true);
        else if (Status.INSTANCE.isSneakToggled() || !Minecraft.getMinecraft().gameSettings.keyBindSneak.isKeyDown())
            Status.INSTANCE.setSneakHeld(false);

        //Toggle sprint
        //Disables sprinting when not enough hunger (unless creative), have blindness, using an item or the player is not moving.
        //Hunger is ignored when in creative mode
        if (Status.INSTANCE.isSprintToggled() && (player.getFoodStats().getFoodLevel() > 6 || player.isCreative()) && !player.isPotionActive(MobEffects.BLINDNESS) && player.movementInput.moveForward != 0 && !PlayerEvent.instance.isUseItem()) {
            player.setSprinting(true);
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
        //Overwrite sprintToggleTimer.
        if (!ToggleSneakMod.optionDoubleTap)
            ObfuscationReflectionHelper.setPrivateValue(EntityPlayerSP.class, player, 0, "field_71156_d");

        //Vanilla sprinting detection
        //E.g double tapping w, and releasing sprint button after holding it.
        int sprintToggleTimer = ObfuscationReflectionHelper.getPrivateValue(EntityPlayerSP.class, player, "field_71156_d");
        boolean vanillaSprint = (sprintToggleTimer == 7 || !Minecraft.getMinecraft().gameSettings.keyBindSprint.isKeyDown()) && player.isSprinting() && !Status.INSTANCE.isSprintToggled();
        Status.INSTANCE.setSprintVanilla(vanillaSprint);

    }

    //This event calls in every frame I believe
    @SubscribeEvent
    public void onMovementUpdate(InputUpdateEvent event) {
        //Set the sneak flag to true in EntityPlayer does nothing. We have to manipulate the player's movements directly.
        if (Status.INSTANCE.isSneakToggled()) {
            //Sneak animation
            event.getMovementInput().sneak = true;
            //Sneak walking speed. Without it you'll be sneaking at walking speed
            //Most anticheat will bounce you back and possibly ban without it.
            event.getMovementInput().moveStrafe = (float) ((double) event.getMovementInput().moveStrafe * 0.3D);
            event.getMovementInput().moveForward = (float) ((double) event.getMovementInput().moveForward * 0.3D);
        }
    }
}