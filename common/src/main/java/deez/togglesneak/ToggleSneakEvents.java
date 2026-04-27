package deez.togglesneak;

import deez.togglesneak.config.TSConfig;
import deez.togglesneak.hud.Status;
import deez.togglesneak.mixin.LocalPlayerMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;

public class ToggleSneakEvents {
    private long sneakPressStart;
    private long sprintPressStart;

    public void onTick(LocalPlayer player) {
        if (player == null) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        boolean isSneaking = false;

        // Toggle sneak
        if (mc.options.keyShift.isDown() && sneakPressStart == 0) {
            sneakPressStart = System.currentTimeMillis();
        } else if (!mc.options.keyShift.isDown()) {
            if (sneakPressStart != 0) {
                long diff = System.currentTimeMillis() - sneakPressStart;
                if (diff < TSConfig.getInstance().optionThreshold * 50L && !Status.INSTANCE.isRidingDismount() && TSConfig.getInstance().optionToggleSneak) {
                    Status.INSTANCE.setSneakToggled(!Status.INSTANCE.isSneakToggled());
                }
                sneakPressStart = 0;
            }
        }

        if (mc.options.keyShift.isDown() && !Status.INSTANCE.isSneakToggled()) {
            Status.INSTANCE.setSneakHeld(true);
            isSneaking = true;
        } else {
            Status.INSTANCE.setSneakHeld(false);
        }

        isSneaking = isSneaking || Status.INSTANCE.isSneakToggled();

        // Toggle sprint
        if (Status.INSTANCE.isSprintToggled()
                && (player.getFoodData().getFoodLevel() > 6 || player.getAbilities().instabuild)
                && !player.hasEffect(MobEffects.BLINDNESS)
                && player.zza > 0
                && !player.isUsingItem()
                && !isSneaking
                && !(player.getPose() == Pose.SWIMMING && !player.isInWater() && !player.isFallFlying())) {
            player.setSprinting(true);
        }

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

        if (mc.options.keySprint.isDown() && !Status.INSTANCE.isSprintToggled()) {
            Status.INSTANCE.setSprintHeld(true);
        } else {
            Status.INSTANCE.setSprintHeld(false);
        }

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
        Status.INSTANCE.setCrouching(player.getPose() == Pose.SWIMMING && !player.isInWater() && !player.isFallFlying());

        // Double Tap
        if (!TSConfig.getInstance().optionDoubleTap) {
            ((LocalPlayerMixin) player).setSprintTriggerTime(0);
        }

        boolean vanillaSprint = player.isSprinting() && !Status.INSTANCE.isSprintToggled();
        Status.INSTANCE.setSprintVanilla(vanillaSprint);
    }
}
