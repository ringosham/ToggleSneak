package deez.togglesneak;

import api.player.client.ClientPlayerAPI;
import api.player.client.ClientPlayerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class PlayerBase extends ClientPlayerBase {
    private Minecraft mc = Minecraft.getMinecraft();
    private CustomMovementInput customMovementInput = new CustomMovementInput();
    private GameSettings settings = mc.gameSettings;

    public PlayerBase(ClientPlayerAPI api) {
        super(api);
    }

    /*
     * 		EntityPlayerSP.onLivingUpdate() - Adapted to PlayerAPI
     */
    @SuppressWarnings("ConstantConditions")
    @Override
    public void onLivingUpdate() {
        /*
         *  Since the player's onLivingUpdate is overridden. The entire method needs to be copied to here with some slight editing.
         */
        ++this.player.sprintingTicksLeft;

        if (this.playerAPI.getSprintToggleTimerField() > 0) {
            this.playerAPI.setSprintToggleTimerField(this.playerAPI.getSprintToggleTimerField() - 1);
        }

        this.player.prevTimeInPortal = this.player.timeInPortal;

        if (this.playerAPI.getInPortalField()) {
            if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
                if (this.mc.currentScreen instanceof GuiContainer) {
                    this.closeScreen();
                }

                this.mc.displayGuiScreen(null);
            }

            if (this.player.timeInPortal == 0.0F) {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_PORTAL_TRIGGER, this.playerAPI.getRandField().nextFloat() * 0.4F + 0.8F));
            }

            this.player.timeInPortal += 0.0125F;

            if (this.player.timeInPortal >= 1.0F) {
                this.player.timeInPortal = 1.0F;
            }

            this.playerAPI.setInPortalField(false);
        } else if (this.player.isPotionActive(MobEffects.NAUSEA) && this.player.getActivePotionEffect(MobEffects.NAUSEA).getDuration() > 60) {
            this.player.timeInPortal += 0.006666667F;

            if (this.player.timeInPortal > 1.0F) {
                this.player.timeInPortal = 1.0F;
            }
        } else {
            if (this.player.timeInPortal > 0.0F) {
                this.player.timeInPortal -= 0.05F;
            }

            if (this.player.timeInPortal < 0.0F) {
                this.player.timeInPortal = 0.0F;
            }
        }

        if (this.player.timeUntilPortal > 0) {
            --this.player.timeUntilPortal;
        }

        boolean flag = this.player.movementInput.jump;
        boolean flag1 = this.player.movementInput.sneak;
        boolean flag2 = this.player.movementInput.moveForward >= 0.8F;
        this.player.movementInput.updatePlayerMoveState();
        net.minecraftforge.client.ForgeHooksClient.onInputUpdate(player, this.player.movementInput);
        this.mc.getTutorial().handleMovement(this.player.movementInput);

        if (this.player.isHandActive() && !this.player.isRiding()) {
            this.player.movementInput.moveStrafe *= 0.2F;
            this.player.movementInput.moveForward *= 0.2F;
            this.playerAPI.setSprintToggleTimerField(0);
        }

        boolean flag3 = false;

        if (this.playerAPI.getAutoJumpRequiredField() > 0) {
            this.playerAPI.setAutoJumpRequiredField(this.playerAPI.getAutoJumpRequiredField() - 1);
            flag3 = true;
            this.player.movementInput.jump = true;
        }

        AxisAlignedBB axisalignedbb = this.player.getEntityBoundingBox();
        net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent event = new net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent(player, axisalignedbb);
        if (!net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event)) {
            axisalignedbb = event.getEntityBoundingBox();
            this.pushOutOfBlocks(this.player.posX - (double) this.player.width * 0.35D, axisalignedbb.minY + 0.5D, this.player.posZ + (double) this.player.width * 0.35D);
            this.pushOutOfBlocks(this.player.posX - (double) this.player.width * 0.35D, axisalignedbb.minY + 0.5D, this.player.posZ - (double) this.player.width * 0.35D);
            this.pushOutOfBlocks(this.player.posX + (double) this.player.width * 0.35D, axisalignedbb.minY + 0.5D, this.player.posZ - (double) this.player.width * 0.35D);
            this.pushOutOfBlocks(this.player.posX + (double) this.player.width * 0.35D, axisalignedbb.minY + 0.5D, this.player.posZ + (double) this.player.width * 0.35D);
        }
        boolean flag4 = (float) this.player.getFoodStats().getFoodLevel() > 6.0F || this.player.capabilities.allowFlying;

        /*
            Begin ToggleSneak functionality
         */

        float minSpeed = 0.8f;
        boolean isSprintDisabled = !ToggleSneakMod.optionToggleSprint;
        boolean canDoubleTap = ToggleSneakMod.optionDoubleTap;
        boolean enoughHunger = (float) this.player.getFoodStats().getFoodLevel() > 6.0f || this.player.capabilities.isFlying;
        boolean isMovingForward = this.player.movementInput.moveForward >= minSpeed;
        this.customMovementInput.update(mc, (MovementInputFromOptions) this.player.movementInput, this.player);

        // Detect when ToggleSprint was disabled in the in-game options menu
        if (ToggleSneakMod.wasSprintDisabled) {
            this.player.setSprinting(false);
            customMovementInput.UpdateSprint(false, false);
            ToggleSneakMod.wasSprintDisabled = false;
        }

        if (isSprintDisabled) {
            if (ToggleSneakMod.optionDoubleTap && this.player.onGround && !flag1 && !flag2 && this.player.movementInput.moveForward >= 0.8F && !this.isSprinting() && flag4 && !this.player.isHandActive() && !this.player.isPotionActive(MobEffects.BLINDNESS)) {
                if (this.playerAPI.getSprintToggleTimerField() <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
                    this.playerAPI.setSprintToggleTimerField(7);
                } else {
                    this.setSprinting(true);
                    customMovementInput.UpdateSprint(true, false);
                }
            }
            if (!this.isSprinting() && this.player.movementInput.moveForward >= 0.8F && flag4 && !this.player.isHandActive() && !this.player.isPotionActive(MobEffects.BLINDNESS) && this.mc.gameSettings.keyBindSprint.isKeyDown()) {
                this.setSprinting(true);
                customMovementInput.UpdateSprint(true, false);
            }
        } else {
            boolean state = this.customMovementInput.sprint;

            // Only handle changes in state under the following conditions:
            // On ground, not hungry, not eating/using item, not blind, and not Vanilla
            //
            // 5/6/14 - onGround check removed to match vanilla's 'start sprint while jumping' behavior.
            //if(this.player.onGround && enoughHunger && !this.player.isUsingItem() && !this.player.isPotionActive(Potion.blindness) && !this.customMovementInput.sprintHeldAndReleased)

            if (enoughHunger && !PlayerEvent.instance.isUseItem() && !this.player.isPotionActive(MobEffects.BLINDNESS) && !this.customMovementInput.sprintHeldAndReleased) {
                if (!canDoubleTap || !this.player.isSprinting()) {
                    this.player.setSprinting(state);
                }
            }

            if (canDoubleTap && !state && this.player.onGround && !isMovingForward && this.player.movementInput.moveForward >= minSpeed && !this.player.isSprinting() && enoughHunger && !PlayerEvent.instance.isUseItem() && !this.player.isPotionActive(MobEffects.BLINDNESS)) {
                if (this.playerAPI.getSprintToggleTimerField() == 0) {
                    this.playerAPI.setSprintToggleTimerField(7);
                } else {
                    this.player.setSprinting(true);
                    customMovementInput.UpdateSprint(true, true);
                    this.playerAPI.setSprintToggleTimerField(0);
                }
            }
        }

        //If the player stops moving, too hungry or colliding with a wall head on.
        if (this.isSprinting() && (this.player.movementInput.moveForward < 0.8F || this.player.collidedHorizontally || !flag4)) {
            this.setSprinting(false);
            // Undo toggle if we resumed vanilla operation due to Hold&Release, DoubleTap, Fly, Ride, elytra flying
            if (customMovementInput.sprintHeldAndReleased || isSprintDisabled || customMovementInput.sprintDoubleTapped || this.player.capabilities.isFlying || this.player.isRiding() || this.player.isElytraFlying()) {
                customMovementInput.UpdateSprint(false, false);
            }
        }

        /*
            End of ToggleSneak
         */

        /*
            Fly boosting
         */

        if (ToggleSneakMod.optionEnableFlyBoost && this.player.capabilities.isFlying && this.settings.keyBindSprint.isKeyDown() && this.player.capabilities.isCreativeMode) {
            this.player.capabilities.setFlySpeed(0.05F * (float) ToggleSneakMod.optionFlyBoostAmount);

            if (this.player.movementInput.sneak)
                this.player.motionY -= 0.15D * ToggleSneakMod.optionFlyBoostAmount;
            if (this.player.movementInput.jump)
                this.player.motionY += 0.15D * ToggleSneakMod.optionFlyBoostAmount;

        } else if (this.player.capabilities.getFlySpeed() != 0.05F) {
            this.player.capabilities.setFlySpeed(0.05F);
        }

        if (this.player.capabilities.allowFlying) {
            if (this.mc.playerController.isSpectatorMode()) {
                if (!this.player.capabilities.isFlying) {
                    this.player.capabilities.isFlying = true;
                    this.player.sendPlayerAbilities();
                }
            } else if (!flag && this.player.movementInput.jump && !flag3) {
                if (this.playerAPI.getFlyToggleTimerField() == 0) {
                    this.playerAPI.setFlyToggleTimerField(7);
                } else {
                    this.player.capabilities.isFlying = !this.player.capabilities.isFlying;
                    this.player.sendPlayerAbilities();
                    this.playerAPI.setFlyToggleTimerField(0);
                }
            }
        }

        if (this.player.movementInput.jump && !flag && !this.player.onGround && this.player.motionY < 0.0D && !this.player.isElytraFlying() && !this.player.capabilities.isFlying) {
            ItemStack itemstack = this.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

            if (itemstack.getItem() == Items.ELYTRA && ItemElytra.isUsable(itemstack)) {
                this.player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_FALL_FLYING));
            }
        }

        ObfuscationReflectionHelper.setPrivateValue(EntityPlayerSP.class, this.player, this.player.isElytraFlying(), "field_189813_ct");
        //Too bad PlayerAPI hasn't implemented this yet.
        //this.player.wasFallFlying = this.player.isElytraFlying();

        if (this.player.capabilities.isFlying && mc.getRenderViewEntity() == this.player) {
            if (this.player.movementInput.sneak) {
                this.player.movementInput.moveStrafe = (float) ((double) this.player.movementInput.moveStrafe / 0.3D);
                this.player.movementInput.moveForward = (float) ((double) this.player.movementInput.moveForward / 0.3D);
                this.player.motionY -= (double) (this.player.capabilities.getFlySpeed() * 3.0F);
            }

            if (this.player.movementInput.jump) {
                this.player.motionY += (double) (this.player.capabilities.getFlySpeed() * 3.0F);
            }
        }

        if (this.player.isRidingHorse()) {
            IJumpingMount ijumpingmount = (IJumpingMount) this.player.getRidingEntity();

            if (this.playerAPI.getHorseJumpPowerCounterField() < 0) {
                this.playerAPI.setHorseJumpPowerCounterField(this.playerAPI.getHorseJumpPowerCounterField() + 1);

                if (this.playerAPI.getHorseJumpPowerCounterField() == 0) {
                    this.playerAPI.setHorseJumpPowerField(0);
                }
            }

            if (flag && !this.player.movementInput.jump) {
                this.playerAPI.setHorseJumpPowerCounterField(-10);
                ijumpingmount.setJumpPower(MathHelper.floor(this.playerAPI.getHorseJumpPowerField() * 100.0F));

                //Original below. Avoid reflection
                //this.player.sendHorseJump();
                this.player.connection.sendPacket(new CPacketEntityAction(player, CPacketEntityAction.Action.START_RIDING_JUMP, MathHelper.floor(this.playerAPI.getHorseJumpPowerField() * 100.0F)));
            } else if (!flag && this.player.movementInput.jump) {
                this.playerAPI.setHorseJumpPowerCounterField(0);
                this.playerAPI.setHorseJumpPowerField(0);
            } else if (flag) {
                this.playerAPI.setHorseJumpPowerCounterField(this.playerAPI.getHorseJumpPowerCounterField() + 1);

                if (this.playerAPI.getHorseJumpPowerCounterField() < 10) {
                    this.playerAPI.setHorseJumpPowerField(this.playerAPI.getHorseJumpPowerCounterField() * 0.1f);
                } else {
                    this.playerAPI.setHorseJumpPowerField(0.8F + 2.0F / (float) (this.playerAPI.getHorseJumpPowerCounterField() - 9) * 0.1F);
                }
            }
        } else {
            this.playerAPI.setHorseJumpPowerField(0);
        }

        super.onLivingUpdate();

        if (this.player.onGround && this.player.capabilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
            this.player.capabilities.isFlying = false;
            this.player.sendPlayerAbilities();
        }
    }
}