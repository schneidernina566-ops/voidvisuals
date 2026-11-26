package dev.akamevoid.voidvisuals.mixin;

import dev.akamevoid.voidvisuals.config.VoidConfig;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSoundInstance.class)
public class SoundMuteMixin {
    @Inject(method = "getVolume", at = @At("HEAD"), cancellable = true, require = 0)
    private void voidvisuals$mute(CallbackInfoReturnable<Float> cir) {
        VoidConfig c = VoidConfig.get();
        if (!c.isEnabled("mute_sounds") || c.mutedSounds.isBlank()) return;
        String path = ((SoundInstance) (Object) this).getId().getPath();
        for (String part : c.mutedSounds.split(",")) {
            String p = part.trim();
            if (!p.isEmpty() && path.contains(p)) {
                cir.setReturnValue(0.0F);
                return;
            }
        }
    }
}
