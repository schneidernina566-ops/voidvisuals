package dev.akamevoid.voidvisuals.fx;

import dev.akamevoid.voidvisuals.config.VoidConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ActionResult;

import java.util.concurrent.ThreadLocalRandom;

/** Partikel-Explosion, wenn das zuletzt angegriffene Ziel stirbt. */
public final class KillEffects {
    private static Entity target;
    private static int ticksLeft;

    private KillEffects() {}

    private static ParticleEffect[] effects() {
        return new ParticleEffect[] {
                ParticleTypes.TOTEM_OF_UNDYING,
                ParticleTypes.FLAME,
                ParticleTypes.HEART,
                ParticleTypes.CLOUD,
                ParticleTypes.SOUL_FIRE_FLAME
        };
    }

    public static void register() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (world.isClient() && entity instanceof LivingEntity) {
                target = entity;
                ticksLeft = 60;
            }
            return ActionResult.PASS;
        });
        ClientTickEvents.END_CLIENT_TICK.register(KillEffects::tick);
    }

    private static void tick(MinecraftClient mc) {
        if (target == null) return;
        if (--ticksLeft <= 0) {
            target = null;
            return;
        }
        if (target instanceof LivingEntity le && (le.getHealth() <= 0 || le.isRemoved())) {
            if (VoidConfig.get().isEnabled("kill_effect") && mc.world != null) {
                burst(mc, target);
            }
            target = null;
        }
    }

    private static void burst(MinecraftClient mc, Entity e) {
        ParticleEffect[] all = effects();
        ParticleEffect fx = all[VoidConfig.get().killEffect % all.length];
        ThreadLocalRandom r = ThreadLocalRandom.current();
        double x = e.getX(), y = e.getY() + e.getHeight() * 0.5, z = e.getZ();
        for (int i = 0; i < 40; i++) {
            mc.particleManager.addParticle(fx, x, y, z,
                    (r.nextDouble() - 0.5) * 0.5,
                    r.nextDouble() * 0.4,
                    (r.nextDouble() - 0.5) * 0.5);
        }
    }
}
