package dev.akamevoid.voidvisuals.hud;

import dev.akamevoid.voidvisuals.VoidVisualsClient;
import dev.akamevoid.voidvisuals.config.VoidConfig;
import dev.akamevoid.voidvisuals.ui.VoidTheme;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/** Eigenes Crosshair (ersetzt das normale) und Info-HUD (FPS, Koordinaten). */
public final class VoidHud {
    private VoidHud() {}

    public static void register() {
        // Vanilla-Crosshair ersetzen: wenn Modul aus, wird das Original gezeichnet.
        HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, original ->
                (ctx, tickCounter) -> {
                    if (VoidConfig.get().isEnabled("crosshair")) {
                        drawCrosshair(ctx);
                    } else {
                        original.render(ctx, tickCounter);
                    }
                });

        HudElementRegistry.addLast(
                Identifier.of(VoidVisualsClient.MOD_ID, "info"),
                (HudElement) (ctx, tickCounter) -> drawInfo(ctx));
    }

    private static void drawCrosshair(DrawContext ctx) {
        VoidConfig cfg = VoidConfig.get();
        int cx = ctx.getScaledWindowWidth() / 2;
        int cy = ctx.getScaledWindowHeight() / 2;
        int gap = cfg.crosshairGap;
        int len = cfg.crosshairSize;
        int color = VoidConfig.Colors.ARGB[cfg.crosshairColor % VoidConfig.Colors.ARGB.length];

        // oben, unten, links, rechts (1 Pixel dick), Mitte bleibt frei
        ctx.fill(cx, cy - gap - len, cx + 1, cy - gap, color);
        ctx.fill(cx, cy + gap + 1, cx + 1, cy + gap + 1 + len, color);
        ctx.fill(cx - gap - len, cy, cx - gap, cy + 1, color);
        ctx.fill(cx + gap + 1, cy, cx + gap + 1 + len, cy + 1, color);
    }

    private static void drawInfo(DrawContext ctx) {
        VoidConfig cfg = VoidConfig.get();
        if (!cfg.isEnabled("hud")) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;

        int x = 6;
        int y = 6;
        if (cfg.hudFps) {
            ctx.drawText(mc.textRenderer, "FPS " + mc.getCurrentFps(), x, y, VoidTheme.ACCENT, true);
            y += 11;
        }
        if (cfg.hudCoords) {
            String pos = String.format("XYZ %.1f / %.1f / %.1f",
                    mc.player.getX(), mc.player.getY(), mc.player.getZ());
            ctx.drawText(mc.textRenderer, pos, x, y, VoidTheme.TEXT, true);
        }
        if (cfg.hudKeys) {
            drawKeys(ctx, mc, 6, ctx.getScaledWindowHeight() - 56);
        }
    }

    private static void drawKeys(DrawContext ctx, MinecraftClient mc, int x, int y) {
        drawKey(ctx, mc, "W", mc.options.forwardKey.isPressed(), x + 22, y);
        drawKey(ctx, mc, "A", mc.options.leftKey.isPressed(), x, y + 22);
        drawKey(ctx, mc, "S", mc.options.backKey.isPressed(), x + 22, y + 22);
        drawKey(ctx, mc, "D", mc.options.rightKey.isPressed(), x + 44, y + 22);
    }

    private static void drawKey(DrawContext ctx, MinecraftClient mc, String label, boolean down, int x, int y) {
        int bg = down ? VoidTheme.ACCENT : 0x90000000;
        ctx.fill(x, y, x + 20, y + 20, bg);
        int tw = mc.textRenderer.getWidth(label);
        ctx.drawText(mc.textRenderer, label, x + (20 - tw) / 2, y + 6, VoidTheme.TEXT, true);
    }
}
