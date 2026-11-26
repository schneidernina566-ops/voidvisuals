package dev.akamevoid.voidvisuals.fx;

import dev.akamevoid.voidvisuals.config.VoidConfig;

/** Bruecke zwischen Menue und OverlayTexture-Mixin. */
public final class HitColorHook {
    private static Runnable refresher;

    private HitColorHook() {}

    public static void set(Runnable r) { refresher = r; }

    public static void refresh() {
        if (refresher != null) refresher.run();
    }

    /** ARGB-Farbe der Treffer-Faerbung (Vanilla: halbtransparentes Rot). */
    public static int argb() {
        VoidConfig c = VoidConfig.get();
        if (!c.isEnabled("hit_color")) return 0xB2FF0000;
        int rgb = VoidConfig.Colors.ARGB[c.hitColor % VoidConfig.Colors.ARGB.length] & 0xFFFFFF;
        return (178 << 24) | rgb;
    }
}
