package deez.togglesneak;

import static deez.togglesneak.Status.StatusText.*;

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

    public boolean isFly() {
        return fly;
    }

    public void setFly(boolean fly) {
        this.fly = fly;
    }

    public boolean isFlyBoost() {
        return flyBoost;
    }

    public void setFlyBoost(boolean flyBoost) {
        this.flyBoost = flyBoost;
    }

    public boolean isSprintHeld() {
        return sprintHeld;
    }

    public void setSprintHeld(boolean sprintHeld) {
        this.sprintHeld = sprintHeld;
    }

    public boolean isSneakHeld() {
        return sneakHeld;
    }

    public void setSneakHeld(boolean sneakHeld) {
        this.sneakHeld = sneakHeld;
    }

    public boolean isSprintToggled() {
        return sprintToggled;
    }

    public void setSprintToggled(boolean sprintToggled) {
        this.sprintToggled = sprintToggled;
    }

    public boolean isSneakToggled() {
        return sneakToggled;
    }

    public void setSneakToggled(boolean sneakToggled) {
        this.sneakToggled = sneakToggled;
    }

    public boolean isSprintVanilla() {
        return sprintVanilla;
    }

    public void setSprintVanilla(boolean sprintVanilla) {
        this.sprintVanilla = sprintVanilla;
    }

    public boolean isElytra() {
        return elytra;
    }

    public void setElytra(boolean elytra) {
        this.elytra = elytra;
    }

    public boolean isRiding() {
        return riding;
    }

    public void setRiding(boolean riding) {
        this.riding = riding;
    }

    public boolean isRidingDismount() {
        return ridingDismount;
    }

    public void setRidingDismount(boolean ridingDismount) {
        this.ridingDismount = ridingDismount;
    }

    public String getStatusString() {
        if (!ToggleSneakMod.optionShowHUDText)
            return "";
        StringBuilder builder = new StringBuilder();
        //On flight
        if (flyBoost)
            builder.append(String.format(FLY_BOOST.toString(), ToggleSneakMod.optionFlyBoostAmount));
        else if (fly)
            builder.append(FLY);
        if ((sneakHeld || sneakToggled) && fly)
            builder.append(" ").append(FLY_DESCEND);
        if (elytra)
            builder.append(ELYTRA);
        //On ground
        if (!fly) {
            if (sneakHeld)
                builder.append(SNEAK);
            if (sneakToggled && !sneakHeld)
                builder.append(SNEAK_TOGGLED);
            //Sneaking takes precedence over sprinting
            if (!sneakToggled && !sneakHeld) {
                if (sprintHeld)
                    builder.append(SPRINT);
                if (sprintToggled)
                    builder.append(SPRINT_TOGGLED);
                if (sprintVanilla && !sprintHeld)
                    builder.append(SPRINT_VANILLA);
            }
        }
        //Riding
        if (riding)
            builder.append(RIDING);
        //Cannot ride while sneak toggled. So this is fine.
        if (riding && sneakHeld)
            builder.append(" ").append(RIDING_DISMOUNT);
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
        ELYTRA("[Elytra flying] "),
        RIDING("[Riding]"),
        RIDING_DISMOUNT("[Dismounting]");

        private final String text;

        StatusText(String text) {
            this.text = text;
        }

        public String toString() {
            return text;
        }
    }
}
