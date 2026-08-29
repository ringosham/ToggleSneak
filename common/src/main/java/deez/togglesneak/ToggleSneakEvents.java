package deez.togglesneak;

import deez.togglesneak.config.TSConfig;
import deez.togglesneak.hud.Status;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;

public class ToggleSneakEvents {
    private long sneakPressTicks;
    private long sprintPressStart;

    public void onTick(LocalPlayer player) {
        if (player == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        boolean isSneaking = false;

        // Toggle sneak
        if (mc.options.keyShift.isDown()) {
            sneakPressTicks++;
        }
        // Instead of comparing timestamps, counting ticks fixed a lot of issues with rapid sneaking.
        // This should allow speedbridging again
        if (!mc.options.keyShift.isDown()) {
            int sneakMode = TSConfig.getInstance().optionSneakMode;
            if (sneakMode == 1) {
                // Long-press to toggle: hold >= threshold ticks to toggle on
                if (sneakPressTicks >= TSConfig.getInstance().optionThreshold && !Status.INSTANCE.isRidingDismount()) {
                    Status.INSTANCE.setSneakToggled(!Status.INSTANCE.isSneakToggled());
                } else if (Status.INSTANCE.isSneakToggled() && sneakPressTicks != 0) {
                    Status.INSTANCE.setSneakToggled(false);
                }
            } else if (sneakMode == 2) {
                // Short-press to toggle: tap < threshold ticks to toggle (original 1.8 behaviour)
                // Caveat: This prevents speedbridging or anything that spams sneak.
                if (sneakPressTicks > 0 && sneakPressTicks < TSConfig.getInstance().optionThreshold && !Status.INSTANCE.isRidingDismount()) {
                    Status.INSTANCE.setSneakToggled(!Status.INSTANCE.isSneakToggled());
                }
            } else {
                // clear any stale toggle state on key press
                if (Status.INSTANCE.isSneakToggled() && sneakPressTicks != 0) {
                    Status.INSTANCE.setSneakToggled(false);
                }
            }
            sneakPressTicks = 0;
        }

        if (mc.options.keyShift.isDown() && !Status.INSTANCE.isSneakToggled()) {
            Status.INSTANCE.setSneakHeld(true);
            isSneaking = true;
        } else {
            Status.INSTANCE.setSneakHeld(false);
        }

        // Disable toggle sneak if the player is swimming
        // Otherwise the player would get stuck at the edge of blocks
        if (player.isSwimming())
            Status.INSTANCE.setSneakToggled(false);

        isSneaking = isSneaking || Status.INSTANCE.isSneakToggled();

        // Toggle sprint
        if (Status.INSTANCE.isSprintToggled()
                && (player.getFoodData().getFoodLevel() > 6 || player.getAbilities().instabuild)
                && !player.hasEffect(MobEffects.BLINDNESS)
                && player.input.hasForwardImpulse()
                && !player.isUsingItem()
                && !isSneaking
                && !(player.getPose() == Pose.SWIMMING && !player.isInWater() && !player.isFallFlying())) {
            player.setSprinting(true);
        }

        // This still uses the old code
        // If it ain't broke, don't fix it
        if (mc.options.keySprint.isDown() && sprintPressStart == 0) {
            sprintPressStart = System.currentTimeMillis();
        } else if (!mc.options.keySprint.isDown()) {
            if (sprintPressStart != 0) {
                long diff = System.currentTimeMillis() - sprintPressStart;
                if (diff < TSConfig.getInstance().optionThreshold * 50L && TSConfig.getInstance().optionToggleSprint) {
                    Status.INSTANCE.setSprintToggled(!Status.INSTANCE.isSprintToggled());
                }
                sprintPressStart = 0;
            }
        }

        Status.INSTANCE.setSprintHeld(mc.options.keySprint.isDown() && !Status.INSTANCE.isSprintToggled());

        // Fly boost
        if (player.getAbilities().flying && player.getAbilities().instabuild && mc.options.keySprint.isDown() && TSConfig.getInstance().optionEnableFlyBoost) {
            Status.INSTANCE.setFlyBoost(true);
            player.getAbilities().setFlyingSpeed(0.05F * (float)TSConfig.getInstance().optionFlyBoostAmount);
            Vec3 motion = player.getDeltaMovement();
            if (mc.options.keyShift.isDown())
                player.setDeltaMovement(motion.add(0, -0.15 * TSConfig.getInstance().optionFlyBoostAmount, 0));
            if (mc.options.keyJump.isDown())
                player.setDeltaMovement(motion.add(0, 0.15 * TSConfig.getInstance().optionFlyBoostAmount, 0));
        } else if (player.getAbilities().getFlyingSpeed() != 0.05F) {
            player.getAbilities().setFlyingSpeed(0.05F);
            Status.INSTANCE.setFlyBoost(false);
        }

        // Status updates
        Status.INSTANCE.setElytra(player.isFallFlying());
        Status.INSTANCE.setFly(player.getAbilities().flying);
        Status.INSTANCE.setRiding(player.getVehicle() != null);
        Status.INSTANCE.setRidingDismount(player.getVehicle() != null && mc.options.keyShift.isDown());
        Status.INSTANCE.setSwimming(player.isSwimming());
        Status.INSTANCE.setCrawling(player.getPose() == Pose.SWIMMING && !player.isInWater() && !player.isFallFlying());

        boolean vanillaSprint = player.isSprinting() && !Status.INSTANCE.isSprintToggled();
        Status.INSTANCE.setSprintVanilla(vanillaSprint);
    }
}
