package dev.akamevoid.voidvisuals.mixin;

import dev.akamevoid.voidvisuals.config.VoidConfig;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientWorld.Properties.class)
public class SkyTimeMixin {
    @Inject(method = "getTimeOfDay", at = @At("HEAD"), cancellable = true, require = 0)
    private void voidvisuals$fixedTime(CallbackInfoReturnable<Long> cir) {
        VoidConfig c = VoidConfig.get();
        if (c.isEnabled("sky_time")) {
            cir.setReturnValue(VoidConfig.SkyTimes.TICKS[c.skyTime % VoidConfig.SkyTimes.TICKS.length]);
        }
    }
}
