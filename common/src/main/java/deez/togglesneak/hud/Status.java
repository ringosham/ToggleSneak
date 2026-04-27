package deez.togglesneak.hud;

import deez.togglesneak.config.TSConfig;

public class Status {
    public static final Status INSTANCE = new Status();

    private Status() {}

    private boolean fly;
    private boolean flyBoost;
    private boolean sprintHeld;
    private boolean sneakHeld;
    private boolean sprintToggled;
    private boolean sneakToggled;
    private boolean sprintVanilla;
    private boolean elytra;
    private boolean riding;
    private boolean ridingDismount;
    private boolean swimming;
    private boolean crouching;

    public boolean isFly() { return fly; }
    public void setFly(boolean fly) { this.fly = fly; }
    public boolean isFlyBoost() { return flyBoost; }
    public void setFlyBoost(boolean flyBoost) { this.flyBoost = flyBoost; }
    public boolean isSprintHeld() { return sprintHeld; }
    public void setSprintHeld(boolean sprintHeld) { this.sprintHeld = sprintHeld; }
    public boolean isSneakHeld() { return sneakHeld; }
    public void setSneakHeld(boolean sneakHeld) { this.sneakHeld = sneakHeld; }
    public boolean isSprintToggled() { return sprintToggled; }
    public void setSprintToggled(boolean sprintToggled) { this.sprintToggled = sprintToggled; }
    public boolean isSneakToggled() { return sneakToggled; }
    public void setSneakToggled(boolean sneakToggled) { this.sneakToggled = sneakToggled; }
    public boolean isSprintVanilla() { return sprintVanilla; }
    public void setSprintVanilla(boolean sprintVanilla) { this.sprintVanilla = sprintVanilla; }
    public boolean isElytra() { return elytra; }
    public void setElytra(boolean elytra) { this.elytra = elytra; }
    public boolean isRiding() { return riding; }
    public void setRiding(boolean riding) { this.riding = riding; }
    public boolean isRidingDismount() { return ridingDismount; }
    public void setRidingDismount(boolean ridingDismount) { this.ridingDismount = ridingDismount; }
    public void setSwimming(boolean swimming) { this.swimming = swimming; }
    public void setCrouching(boolean crouching) { this.crouching = crouching; }
    public boolean isCrouching() { return crouching; }

    public String getStatusString() {
        if (!TSConfig.getInstance().optionShowHUDText)
            return "";
        StringBuilder builder = new StringBuilder();
        if (flyBoost)
            builder.append(String.format(StatusText.FLY_BOOST.toString(), TSConfig.getInstance().optionFlyBoostAmount));
        else if (fly)
            builder.append(StatusText.FLY);
        
        if ((sneakHeld || sneakToggled) && fly)
            builder.append(" ").append(StatusText.FLY_DESCEND);
        if (elytra)
            builder.append(StatusText.ELYTRA);
        if (swimming)
            builder.append(StatusText.SWIMMING);
        if (crouching)
            builder.append(StatusText.CROUCHING);
        
        if (!fly) {
            if (sneakHeld)
                builder.append(StatusText.SNEAK);
            if (sneakToggled && !sneakHeld)
                builder.append(StatusText.SNEAK_TOGGLED);
            
            if (!sneakToggled && !sneakHeld) {
                if (sprintHeld)
                    builder.append(StatusText.SPRINT);
                if (sprintToggled)
                    builder.append(StatusText.SPRINT_TOGGLED);
                if (sprintVanilla && !sprintHeld)
                    builder.append(StatusText.SPRINT_VANILLA);
            }
        }
        
        if (riding)
            builder.append(StatusText.RIDING);
        if (riding && sneakHeld)
            builder.append(" ").append(StatusText.RIDING_DISMOUNT);
            
        return builder.toString();
    }

    enum StatusText {
        FLY("[Flying]"),
        FLY_BOOST("[Flying (%.2f x boost)]"),
        FLY_DESCEND("[Descending]"),
        SPRINT("[Sprinting (Key held)]"),
        SNEAK("[Sneaking (Key held)] "),
        SPRINT_TOGGLED("[Sprinting (Toggled)] "),
        SNEAK_TOGGLED("[Sneaking (Toggled)]"),
        SPRINT_VANILLA("[Sprinting (Vanilla)] "),
        ELYTRA("[Gliding] "),
        RIDING("[Riding]"),
        RIDING_DISMOUNT("[Dismounting]"),
        CROUCHING("[Crouching] "),
        SWIMMING("[Swimming] ");

        private final String text;
        StatusText(String text) { this.text = text; }
        public String toString() { return text; }
    }
}
