package dev.akamevoid.voidvisuals.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/** Einfache JSON-Config: Modul-Schalter und Einstellungen. */
public class VoidConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE =
            FabricLoader.getInstance().getConfigDir().resolve("voidvisuals.json");

    public Map<String, Boolean> modules = new LinkedHashMap<>();

    // Crosshair
    public int crosshairColor = 0;   // Index in Colors.NAMES
    public int crosshairSize = 5;    // Laenge eines Arms in Pixeln
    public int crosshairGap = 2;

    // HUD
    public boolean hudFps = true;
    public boolean hudCoords = true;
    public boolean hudKeys = true;

    // Weitere Module
    public int hitColor = 0;
    public int outlineColor = 0;
    public int killEffect = 0;
    public int skyTime = 3;
    public String mutedSounds = "";

    private static VoidConfig instance = new VoidConfig();

    public static VoidConfig get() {
        return instance;
    }

    public static void load() {
        if (Files.exists(FILE)) {
            try (Reader r = Files.newBufferedReader(FILE)) {
                VoidConfig loaded = GSON.fromJson(r, VoidConfig.class);
                if (loaded != null) {
                    if (loaded.modules == null) loaded.modules = new LinkedHashMap<>();
                    instance = loaded;
                }
            } catch (IOException | RuntimeException e) {
                System.err.println("[Void Visuals] Config konnte nicht gelesen werden: " + e);
            }
        }
        for (String id : Modules.IDS) instance.modules.putIfAbsent(id, false);
        save();
    }

    public static void save() {
        try (Writer w = Files.newBufferedWriter(FILE)) {
            GSON.toJson(instance, w);
        } catch (IOException e) {
            System.err.println("[Void Visuals] Config konnte nicht gespeichert werden: " + e);
        }
    }

    /** Preset als Share-Code (Base64 des JSON). Mit "VV1:" Praefix und Versionskennung. */
    public static String exportCode() {
        String json = new GsonBuilder().create().toJson(instance);
        return "VV1:" + Base64.getUrlEncoder().withoutPadding()
                .encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    /** Liest einen Share-Code. Gibt false zurueck, wenn der Code ungueltig ist. */
    public static boolean importCode(String code) {
        if (code == null) return false;
        code = code.trim();
        if (!code.startsWith("VV1:")) return false;
        try {
            byte[] raw = Base64.getUrlDecoder().decode(code.substring(4));
            VoidConfig loaded = GSON.fromJson(new String(raw, StandardCharsets.UTF_8), VoidConfig.class);
            if (loaded == null) return false;
            if (loaded.modules == null) loaded.modules = new LinkedHashMap<>();
            for (String id : Modules.IDS) loaded.modules.putIfAbsent(id, false);
            loaded.crosshairColor = Math.floorMod(loaded.crosshairColor, Colors.ARGB.length);
            loaded.crosshairSize = Math.max(1, Math.min(32, loaded.crosshairSize));
            loaded.crosshairGap = Math.max(0, Math.min(16, loaded.crosshairGap));
            loaded.hitColor = Math.floorMod(loaded.hitColor, Colors.ARGB.length);
            loaded.outlineColor = Math.floorMod(loaded.outlineColor, Colors.ARGB.length);
            loaded.killEffect = Math.floorMod(loaded.killEffect, KillEffectNames.NAMES.length);
            loaded.skyTime = Math.floorMod(loaded.skyTime, SkyTimes.NAMES.length);
            if (loaded.mutedSounds == null) loaded.mutedSounds = "";
            instance = loaded;
            save();
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isEnabled(String id) {
        return modules.getOrDefault(id, false);
    }

    public void toggle(String id) {
        modules.put(id, !isEnabled(id));
        save();
    }

    public static final class Modules {
        public static final String[] IDS = {
                "hit_color", "block_outline", "crosshair", "glint", "hide_armor", "hud",
                "kill_effect", "sky_time", "mute_sounds"
        };
        private Modules() {}
    }

    public static final class KillEffectNames {
        public static final String[] NAMES = {"Totem", "Flame", "Hearts", "Cloud", "Soul"};
        private KillEffectNames() {}
    }

    public static final class SkyTimes {
        public static final String[] NAMES = {"Day", "Noon", "Sunset", "Night"};
        public static final long[] TICKS = {1000L, 6000L, 12500L, 18000L};
        private SkyTimes() {}
    }

    /** Auswaehlbare Farben (ARGB). */
    public static final class Colors {
        public static final String[] NAMES = {"Red", "White", "Green", "Cyan", "Yellow", "Magenta"};
        public static final int[] ARGB = {
                0xFFE5232E, 0xFFFFFFFF, 0xFF3CD13C, 0xFF29D3E0, 0xFFF2D93A, 0xFFD43BD4
        };
        private Colors() {}
    }
}
