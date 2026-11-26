package dev.akamevoid.voidvisuals;

import dev.akamevoid.voidvisuals.config.VoidConfig;
import dev.akamevoid.voidvisuals.fx.KillEffects;
import dev.akamevoid.voidvisuals.hud.VoidHud;
import dev.akamevoid.voidvisuals.ui.VoidScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class VoidVisualsClient implements ClientModInitializer {
    public static final String MOD_ID = "voidvisuals";

    private static final KeyBinding.Category CATEGORY =
            KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));

    private static KeyBinding openMenu;

    @Override
    public void onInitializeClient() {
        VoidConfig.load();
        VoidHud.register();
        KillEffects.register();

        // Vorinstalliertes Pack: erscheint unter Optionen -> Ressourcenpakete
        FabricLoader.getInstance().getModContainer(MOD_ID).ifPresent(container ->
                ResourceManagerHelper.registerBuiltinResourcePack(
                        Identifier.of(MOD_ID, "void_red_glint"),
                        container,
                        Text.literal("Void Red Glint"),
                        ResourcePackActivationType.NORMAL));

        openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.voidvisuals.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                CATEGORY));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new VoidScreen());
                }
            }
        });
    }
}
