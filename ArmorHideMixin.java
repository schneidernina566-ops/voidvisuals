package dev.akamevoid.voidvisuals.mixin;

import dev.akamevoid.voidvisuals.config.VoidConfig;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorFeatureRenderer.class)
public class ArmorHideMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true, require = 0)
    private void voidvisuals$hideArmor(CallbackInfo ci) {
        if (VoidConfig.get().isEnabled("hide_armor")) ci.cancel();
    }
}
