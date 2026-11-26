package dev.akamevoid.voidvisuals.ui;

import dev.akamevoid.voidvisuals.config.VoidConfig;
import dev.akamevoid.voidvisuals.fx.HitColorHook;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.function.Supplier;

public class VoidScreen extends Screen {
    private static final int COL_W = 190;
    private static final int GAP = 10;
    private static final int PAD = 12;
    private static final int PANEL_W = PAD * 2 + COL_W * 2 + GAP;
    private static final int ROW_H = 24;
    private static final int ROWS = 11;

    private Text status = Text.empty();

    public VoidScreen() {
        super(Text.literal("Void Visuals"));
    }

    private int panelHeight() {
        return 52 + ROWS * ROW_H + 22;
    }

    /** Button, der bei Klick eine Aktion ausfuehrt und seinen Text neu berechnet. */
    private ButtonWidget cycle(int x, int y, Supplier<Text> label, Runnable action) {
        return ButtonWidget.builder(label.get(), b -> {
                    action.run();
                    VoidConfig.save();
                    b.setMessage(label.get());
                })
                .dimensions(x, y, COL_W, 20).build();
    }

    @Override
    protected void init() {
        VoidConfig cfg = VoidConfig.get();
        int x0 = (this.width - PANEL_W) / 2;
        int y0 = (this.height - panelHeight()) / 2;
        int left = x0 + PAD;
        int right = left + COL_W + GAP;
        int y = y0 + 40;

        // ---- Linke Spalte: Module + Presets ----
        int ly = y;
        for (String id : VoidConfig.Modules.IDS) {
            this.addDrawableChild(cycle(left, ly, () -> label(id), () -> {
                cfg.toggle(id);
                if (id.equals("hit_color")) HitColorHook.refresh();
            }));
            ly += ROW_H;
        }

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Copy preset code"), b -> {
                    if (this.client != null) {
                        this.client.keyboard.setClipboard(VoidConfig.exportCode());
                        status = Text.literal("Preset code copied to clipboard");
                    }
                })
                .dimensions(left, ly, COL_W, 20).build());
        ly += ROW_H;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Paste preset code"), b -> {
                    if (this.client != null) {
                        if (VoidConfig.importCode(this.client.keyboard.getClipboard())) {
                            HitColorHook.refresh();
                            this.client.setScreen(new VoidScreen());
                        } else {
                            status = Text.literal("Clipboard has no valid preset code");
                        }
                    }
                })
                .dimensions(left, ly, COL_W, 20).build());

        // ---- Rechte Spalte: Einstellungen ----
        int ry = y;
        int nColors = VoidConfig.Colors.NAMES.length;

        this.addDrawableChild(cycle(right, ry,
                () -> Text.literal("Crosshair color: " + VoidConfig.Colors.NAMES[cfg.crosshairColor % nColors]),
                () -> cfg.crosshairColor = (cfg.crosshairColor + 1) % nColors));
        ry += ROW_H;

        this.addDrawableChild(cycle(right, ry,
                () -> Text.literal("Crosshair size: " + cfg.crosshairSize),
                () -> cfg.crosshairSize = cfg.crosshairSize >= 12 ? 2 : cfg.crosshairSize + 1));
        ry += ROW_H;

        this.addDrawableChild(cycle(right, ry, () -> flag("HUD FPS", cfg.hudFps),
                () -> cfg.hudFps = !cfg.hudFps));
        ry += ROW_H;

        this.addDrawableChild(cycle(right, ry, () -> flag("HUD coordinates", cfg.hudCoords),
                () -> cfg.hudCoords = !cfg.hudCoords));
        ry += ROW_H;

        this.addDrawableChild(cycle(right, ry, () -> flag("HUD keystrokes", cfg.hudKeys),
                () -> cfg.hudKeys = !cfg.hudKeys));
        ry += ROW_H;

        this.addDrawableChild(cycle(right, ry,
                () -> Text.literal("Hit color: " + VoidConfig.Colors.NAMES[cfg.hitColor % nColors]),
                () -> {
                    cfg.hitColor = (cfg.hitColor + 1) % nColors;
                    HitColorHook.refresh();
                }));
        ry += ROW_H;

        this.addDrawableChild(cycle(right, ry,
                () -> Text.literal("Outline color: " + VoidConfig.Colors.NAMES[cfg.outlineColor % nColors]),
                () -> cfg.outlineColor = (cfg.outlineColor + 1) % nColors));
        ry += ROW_H;

        int nKill = VoidConfig.KillEffectNames.NAMES.length;
        this.addDrawableChild(cycle(right, ry,
                () -> Text.literal("Kill effect: " + VoidConfig.KillEffectNames.NAMES[cfg.killEffect % nKill]),
                () -> cfg.killEffect = (cfg.killEffect + 1) % nKill));
        ry += ROW_H;

        int nSky = VoidConfig.SkyTimes.NAMES.length;
        this.addDrawableChild(cycle(right, ry,
                () -> Text.literal("Sky time: " + VoidConfig.SkyTimes.NAMES[cfg.skyTime % nSky]),
                () -> cfg.skyTime = (cfg.skyTime + 1) % nSky));
        ry += ROW_H;

        TextFieldWidget muted = new TextFieldWidget(this.textRenderer, right, ry, COL_W, 20,
                Text.literal("Muted sounds"));
        muted.setMaxLength(120);
        muted.setPlaceholder(Text.literal("muted sounds: step,rain"));
        muted.setText(cfg.mutedSounds);
        muted.setChangedListener(t -> {
            cfg.mutedSounds = t;
            VoidConfig.save();
        });
        this.addDrawableChild(muted);
    }

    private static Text label(String id) {
        return flag(id.replace('_', ' '), VoidConfig.get().isEnabled(id));
    }

    private static Text flag(String name, boolean on) {
        return Text.literal(name + ": " + (on ? "ON" : "OFF"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, VoidTheme.BACKDROP);

        int w = PANEL_W;
        int h = panelHeight();
        int x0 = (this.width - w) / 2;
        int y0 = (this.height - h) / 2;

        ctx.fill(x0 - 1, y0 - 1, x0 + w + 1, y0 + h + 1, VoidTheme.PANEL_EDGE);
        ctx.fill(x0, y0, x0 + w, y0 + h, VoidTheme.PANEL);
        ctx.fill(x0, y0, x0 + w, y0 + 3, VoidTheme.ACCENT);

        ctx.drawText(this.textRenderer, "VOID VISUALS", x0 + PAD, y0 + 12, VoidTheme.ACCENT, true);
        ctx.drawText(this.textRenderer, "made by akamevoid", x0 + PAD, y0 + 24, VoidTheme.TEXT_DIM, false);
        ctx.drawText(this.textRenderer, status, x0 + PAD, y0 + h - 14, VoidTheme.TEXT_DIM, false);

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
