package deez.togglesneak;

import api.player.client.ClientPlayerAPI;
import api.player.client.ClientPlayerBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class PlayerBase extends ClientPlayerBase {
    private Minecraft mc = Minecraft.getMinecraft();
    private CustomMovementInput customMovementInput = new CustomMovementInput();
    private GameSettings settings = mc.gameSettings;

//	private boolean showDebug = true;
//	private boolean handledDebugPress = false;

    public PlayerBase(ClientPlayerAPI api) {
        super(api);
    }

    /*
     * 		EntityPlayerSP.onLivingUpdate() - Adapted to PlayerAPI
     */
    @SuppressWarnings("ConstantConditions")
	@Override
    public void onLivingUpdate() {
        if (this.player.sprintingTicksLeft > 0) {
            --this.player.sprintingTicksLeft;
            if (this.player.sprintingTicksLeft == 0) {
                this.player.setSprinting(false);
            }
        }

        if (this.playerAPI.getSprintToggleTimerField() > 0) {
            this.playerAPI.setSprintToggleTimerField(this.playerAPI.getSprintToggleTimerField() - 1);
        }

		/*
		if(this.mc.playerController.enableEverythingIsScrewedUpMode())
		{
			this.player.posX = this.player.posZ = 0.5D;
			this.player.posX = 0.0D;
			this.player.posZ = 0.0D;
			this.player.rotationYaw = (float)this.player.ticksExisted / 12.0F;
			this.player.rotationPitch = 10.0F;
			this.player.posY = 68.5D;
		}
		else
		{
		*/
        this.player.prevTimeInPortal = this.player.timeInPortal;
        if (this.playerAPI.getInPortalField()) {
            if (this.mc.currentScreen != null) {
                this.mc.displayGuiScreen((GuiScreen) null);
            }

            if (this.player.timeInPortal == 0.0F) {
                this.mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvent.REGISTRY.getObject(new ResourceLocation("minecraft", "")), this.player.getRNG().nextFloat() * 0.4F + 0.8F));
            }

            this.player.timeInPortal += 0.0125F;

            if (this.player.timeInPortal >= 1.0F) {
                this.player.timeInPortal = 1.0F;
            }

            this.playerAPI.setInPortalField(false);
        } else if (this.player.isPotionActive(Potion.getPotionById(9)) && this.player.getActivePotionEffect(Potion.getPotionById(9)).getDuration() > 60) {
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

        boolean isJumping = this.player.movementInput.jump;

        float minSpeed = 0.8F;
        boolean isMovingForward = this.player.movementInput.moveForward >= minSpeed;
        this.customMovementInput.update(this.mc, (MovementInputFromOptions) this.player.movementInput, this.player);

        if (settings.keyBindUseItem.isKeyDown() && !this.player.isRiding()) {
            this.player.movementInput.moveStrafe *= 0.2F;
            this.player.movementInput.moveForward *= 0.2F;
            this.playerAPI.setSprintToggleTimerField(0);
        }
			
			/* TODO: removed in vanilla 1.8 - investigate

			if(this.player.movementInput.sneak && this.player.ySize < 0.2F)
			{
				this.player.ySize = 0.2F;
			}
			
			*/

        this.playerAPI.localPushOutOfBlocks(this.player.posX - (double) this.player.width * 0.35D, this.player.getEntityBoundingBox().minY + 0.5D, this.player.posZ + (double) this.player.width * 0.35D);
        this.playerAPI.localPushOutOfBlocks(this.player.posX - (double) this.player.width * 0.35D, this.player.getEntityBoundingBox().minY + 0.5D, this.player.posZ - (double) this.player.width * 0.35D);
        this.playerAPI.localPushOutOfBlocks(this.player.posX + (double) this.player.width * 0.35D, this.player.getEntityBoundingBox().minY + 0.5D, this.player.posZ - (double) this.player.width * 0.35D);
        this.playerAPI.localPushOutOfBlocks(this.player.posX + (double) this.player.width * 0.35D, this.player.getEntityBoundingBox().minY + 0.5D, this.player.posZ + (double) this.player.width * 0.35D);
        boolean enoughHunger = (float) this.player.getFoodStats().getFoodLevel() > 6.0F || this.player.capabilities.isFlying;

        /*
         * 		Begin ToggleSneak Changes - ToggleSprint
         */

        boolean isSprintDisabled = !ToggleSneakMod.optionToggleSprint;
        boolean canDoubleTap = ToggleSneakMod.optionDoubleTap;

        // Detect when ToggleSprint was disabled in the in-game options menu
        if (ToggleSneakMod.wasSprintDisabled) {
            this.player.setSprinting(false);
            customMovementInput.UpdateSprint(false, false);
            ToggleSneakMod.wasSprintDisabled = false;
        }

        // Default Sprint routine converted to PlayerAPI, use if ToggleSprint is disabled
        if (isSprintDisabled) {
            if (ToggleSneakMod.optionDoubleTap && this.player.onGround && !isMovingForward && this.player.movementInput.moveForward >= minSpeed && !this.player.isSprinting() && enoughHunger && !settings.keyBindUseItem.isKeyDown() && !this.player.isPotionActive(Potion.getPotionById(15))) {
                if (this.playerAPI.getSprintToggleTimerField() <= 0 && !this.settings.keyBindSprint.isKeyDown()) {
                    this.playerAPI.setSprintToggleTimerField(7);
                } else {
                    this.player.setSprinting(true);
                    customMovementInput.UpdateSprint(true, false);
                }
            }

            if (!this.player.isSprinting() && this.player.movementInput.moveForward >= minSpeed && enoughHunger && !settings.keyBindUseItem.isKeyDown() && !this.player.isPotionActive(Potion.getPotionById(15)) && this.settings.keyBindSprint.isKeyDown()) {
                this.player.setSprinting(true);
                customMovementInput.UpdateSprint(true, false);
            }
        } else {
            boolean state = this.customMovementInput.sprint;

            // Only handle changes in state under the following conditions:
            // On ground, not hungry, not eating/using item, not blind, and not Vanilla
            //
            // 5/6/14 - onGround check removed to match vanilla's 'start sprint while jumping' behavior.
            //if(this.player.onGround && enoughHunger && !this.player.isUsingItem() && !this.player.isPotionActive(Potion.blindness) && !this.customMovementInput.sprintHeldAndReleased)

            if (enoughHunger && !settings.keyBindUseItem.isKeyDown() && !this.player.isPotionActive(Potion.getPotionById(15)) && !this.customMovementInput.sprintHeldAndReleased) {
                if (!canDoubleTap || !this.player.isSprinting()) {
                    this.player.setSprinting(state);
                }
            }

            if (canDoubleTap && !state && this.player.onGround && !isMovingForward && this.player.movementInput.moveForward >= minSpeed && !this.player.isSprinting() && enoughHunger && !settings.keyBindUseItem.isKeyDown() && !this.player.isPotionActive(Potion.getPotionById(15))) {
                if (this.playerAPI.getSprintToggleTimerField() == 0) {
                    this.playerAPI.setSprintToggleTimerField(7);
                } else {
                    this.player.setSprinting(true);
                    customMovementInput.UpdateSprint(true, true);
                    this.playerAPI.setSprintToggleTimerField(0);
                }
            }
        }

        // If sprinting, break the sprint in appropriate circumstances:
        // Player stops moving forward, runs into something, or gets too hungry
        if (this.player.isSprinting() && (this.player.movementInput.moveForward < minSpeed || this.player.collidedHorizontally || !enoughHunger)) {
            this.player.setSprinting(false);

            // Undo toggle if we resumed vanilla operation due to Hold&Release, DoubleTap, Fly, Ride
            if (customMovementInput.sprintHeldAndReleased || isSprintDisabled || customMovementInput.sprintDoubleTapped || this.player.capabilities.isFlying || this.player.isRiding()) {
                customMovementInput.UpdateSprint(false, false);
            }
        }

        /*
         * 		End ToggleSneak Changes - ToggleSprint
         */

//			//
//			//  Debug Framework - Added 5/7/2014
//			//
//			if (this.showDebug && this.settings.keyBindPickBlock.isKeyDown() && !this.handledDebugPress)
//			{
//				this.player.addChatMessage(new ChatComponentText("+--------------------------------------+"));
//				this.player.addChatMessage(new ChatComponentText("|        ToggleSneak Debug Info        |"));
//				this.player.addChatMessage(new ChatComponentText("+--------------------------------------+"));
//				this.player.addChatMessage(new ChatComponentText("                                        "));
//				this.player.addChatMessage(new ChatComponentText("isFlying       - " + (this.player.capabilities.isFlying == true ? "True" : "False")));
//				this.player.addChatMessage(new ChatComponentText("isCreative     - " + (this.player.capabilities.isCreativeMode == true ? "True" : "False")));
//				this.player.addChatMessage(new ChatComponentText("enableFlyBoost - " + (ToggleSneakMod.optionEnableFlyBoost == true ? "True" : "False")));
//				this.player.addChatMessage(new ChatComponentText("flyBoostAmount - " + ToggleSneakMod.optionFlyBoostAmount));
//				this.player.addChatMessage(new ChatComponentText("                                        "));
//				this.player.addChatMessage(new ChatComponentText("keybindSprint  - " + (this.settings.keyBindSprint.isKeyDown() == true ? "True" : "False")));
//				this.player.addChatMessage(new ChatComponentText("keybindSneak   - " + (this.settings.keyBindSneak.isKeyDown() == true ? "True" : "False")));
//				this.player.addChatMessage(new ChatComponentText("keybindJump    - " + (this.settings.keyBindJump.isKeyDown() == true ? "True" : "False")));
//				this.player.addChatMessage(new ChatComponentText("                                        "));
//				this.player.addChatMessage(new ChatComponentText("                                        "));
//				
//				this.handledDebugPress = true;
//			}
//			else if (this.showDebug && !this.settings.keyBindPickBlock.isKeyDown() && this.handledDebugPress)
//			{
//				this.handledDebugPress = false;
//			}

        //
        //  Fly Speed Boosting - Added 5/7/2014
        //
        if (ToggleSneakMod.optionEnableFlyBoost && this.player.capabilities.isFlying && this.settings.keyBindSprint.isKeyDown() && this.player.capabilities.isCreativeMode) {
            this.player.capabilities.setFlySpeed(0.05F * (float) ToggleSneakMod.optionFlyBoostAmount);

            if (this.player.movementInput.sneak)
                this.player.motionY -= 0.15D * ToggleSneakMod.optionFlyBoostAmount;
            if (this.player.movementInput.jump)
                this.player.motionY += 0.15D * ToggleSneakMod.optionFlyBoostAmount;

        } else if (this.player.capabilities.getFlySpeed() != 0.05F) {
            this.player.capabilities.setFlySpeed(0.05F);
        }


        if (this.player.capabilities.allowFlying && !isJumping && this.player.movementInput.jump) {
            if (this.playerAPI.getFlyToggleTimerField() == 0) {
                this.playerAPI.setFlyToggleTimerField(7);
            } else {
                this.player.capabilities.isFlying = !this.player.capabilities.isFlying;
                this.player.sendPlayerAbilities();
                this.playerAPI.setFlyToggleTimerField(0);
            }
        }

        if (this.player.capabilities.isFlying) {
            if (this.player.movementInput.sneak) {
                this.player.motionY -= 0.15D;
            }
            if (this.player.movementInput.jump) {
                this.player.motionY += 0.15D;
            }
        }

        if (this.player.isRidingHorse()) {
            if (this.playerAPI.getHorseJumpPowerCounterField() < 0) {
                this.playerAPI.setHorseJumpPowerCounterField(this.playerAPI.getHorseJumpPowerCounterField() + 1);
                if (this.playerAPI.getHorseJumpPowerCounterField() == 0) {
                    this.playerAPI.setHorseJumpPowerField(0.0F);
                }
            }

            if (isJumping && !this.player.movementInput.jump) {
                this.playerAPI.setHorseJumpPowerCounterField(this.playerAPI.getHorseJumpPowerCounterField() - 10);
                this.playerAPI.setHorseJumpPowerCounterField(-10);
                ((EntityPlayerSP) this.player).connection.sendPacket(new CPacketEntityAction(this.player, CPacketEntityAction.Action.START_RIDING_JUMP, (int) (this.player.getHorseJumpPower() * 100.0F)));
            } else if (!isJumping && this.player.movementInput.jump) {
                this.playerAPI.setHorseJumpPowerCounterField(0);
                this.playerAPI.setHorseJumpPowerField(0.0F);
            } else if (isJumping) {
                this.playerAPI.setHorseJumpPowerCounterField(this.playerAPI.getHorseJumpPowerCounterField() + 1);
                if (this.playerAPI.getHorseJumpPowerCounterField() < 10) {
                    this.playerAPI.setHorseJumpPowerField((float) this.playerAPI.getHorseJumpPowerCounterField() * 0.1F);
                } else {
                    this.playerAPI.setHorseJumpPowerField(0.8F + 2.0F / (float) (this.playerAPI.getHorseJumpPowerCounterField() - 9) * 0.1F);
                }
            }
        } else {
            this.playerAPI.setHorseJumpPowerField(0.0F);
        }

        this.playerAPI.superOnLivingUpdate();
        if (this.player.onGround && this.player.capabilities.isFlying) {
            this.player.capabilities.isFlying = false;
            this.player.sendPlayerAbilities();
        }
        //}
    }
}