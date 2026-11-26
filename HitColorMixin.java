package dev.akamevoid.voidvisuals.mixin;

import dev.akamevoid.voidvisuals.fx.HitColorHook;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OverlayTexture.class)
public class HitColorMixin {
    @Shadow @Final private NativeImageBackedTexture texture;

    @Inject(method = "<init>", at = @At("TAIL"), require = 0)
    private void voidvisuals$init(CallbackInfo ci) {
        HitColorHook.set(() -> voidvisuals$recolor());
        voidvisuals$recolor();
    }

    private void voidvisuals$recolor() {
        NativeImage img = this.texture.getImage();
        if (img == null) return;
        int argb = HitColorHook.argb();
        // Obere Haelfte der Overlay-Textur = Treffer-Faerbung
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 16; x++) {
                img.setColorArgb(x, y, argb);
            }
        }
        this.texture.upload();
    }
}
