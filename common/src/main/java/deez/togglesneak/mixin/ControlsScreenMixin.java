package deez.togglesneak.mixin;

import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ControlsScreen.class)
public class ControlsScreenMixin {
    @Inject(method = "options", at = @At("RETURN"), cancellable = true)
    private static void options(Options options, CallbackInfoReturnable<OptionInstance<?>[]> cir) {
        // Do not let users alter with the vanilla toggle sprint and sneak
        cir.setReturnValue(new OptionInstance[]{
                options.toggleAttack(),
                options.toggleUse(),
                options.autoJump(),
                options.sprintWindow(),
                options.operatorItemsTab()
        });
    }
}
