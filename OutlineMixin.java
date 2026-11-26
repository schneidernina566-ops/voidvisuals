package dev.akamevoid.voidvisuals.mixin;

import dev.akamevoid.voidvisuals.config.VoidConfig;
import net.minecraft.client.render.VertexRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(VertexRendering.class)
public class OutlineMixin {
    @ModifyVariable(method = "drawOutline", at = @At("HEAD"), argsOnly = true, ordinal = 0, require = 0)
    private static int voidvisuals$outlineColor(int original) {
        VoidConfig c = VoidConfig.get();
        if (!c.isEnabled("block_outline")) return original;
        int rgb = VoidConfig.Colors.ARGB[c.outlineColor % VoidConfig.Colors.ARGB.length] & 0xFFFFFF;
        return (0xCC << 24) | rgb;
    }
}
