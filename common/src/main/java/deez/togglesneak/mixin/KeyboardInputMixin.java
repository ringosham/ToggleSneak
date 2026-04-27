package deez.togglesneak.mixin;

import deez.togglesneak.hud.Status;
import net.minecraft.client.Options;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.world.entity.player.Input;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
    @Shadow
    public Options options;

    @Redirect(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/player/KeyboardInput;keyPresses:Lnet/minecraft/world/entity/player/Input;", opcode = Opcodes.PUTFIELD))
    public void newInput(KeyboardInput input, Input keyPresses) {
        input.keyPresses = new Input(
                this.options.keyUp.isDown(),
                this.options.keyDown.isDown(),
                this.options.keyLeft.isDown(),
                this.options.keyRight.isDown(),
                this.options.keyJump.isDown(),
                Status.INSTANCE.isSneakToggled() | this.options.keyShift.isDown(),
                this.options.keySprint.isDown()
        );
    }
}
