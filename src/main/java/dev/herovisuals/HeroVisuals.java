package dev.herovisuals;

import dev.herovisuals.gui.ClickGuiScreen;
import dev.herovisuals.gui.MenuStyle;
import dev.herovisuals.module.FpsBoost;
import dev.herovisuals.render.ChinaHatRenderer;
import dev.herovisuals.render.HudRenderer;
import dev.herovisuals.render.SkyRenderer;
import dev.herovisuals.render.SnowRenderer;
import dev.herovisuals.render.TargetEspRenderer;
import dev.herovisuals.render.TrailRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import org.lwjgl.glfw.GLFW;

public class HeroVisuals implements ClientModInitializer {
    public static final String NAME = "HeroVisuals";
    private static KeyBinding openKey;

    @Override
    public void onInitializeClient() {
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.herovisuals.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "HeroVisuals"));

        ClientLifecycleEvents.CLIENT_STARTED.register(mc -> mc.getWindow().setTitle(NAME + " 1.1 | Minecraft 1.21.4"));
        ClientLifecycleEvents.CLIENT_STOPPING.register(FpsBoost::restore);

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openKey.wasPressed()) {
                if (mc.currentScreen == null) mc.setScreen(new ClickGuiScreen());
            }
            TrailRenderer.tick(mc);
            FpsBoost.tick(mc);
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) -> {
            if (world.isClient && entity instanceof LivingEntity le) TargetEspRenderer.onAttack(le);
            return ActionResult.PASS;
        });

        HudRenderCallback.EVENT.register((ctx, tickCounter) -> HudRenderer.render(ctx));

        MenuStyle.init();

        WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> {
            VertexConsumerProvider consumers = context.consumers();
            if (consumers == null) return;
            var cam = context.camera().getPos();
            float td = context.tickCounter().getTickDelta(false);
            var ms = context.matrixStack();
            if (ms == null) return;

            SkyRenderer.render(ms, consumers);
            SnowRenderer.render(ms, consumers, cam);
            TrailRenderer.render(ms, consumers, cam, td);
            ChinaHatRenderer.render(ms, consumers, cam, td);
            TargetEspRenderer.render(ms, consumers, cam, td);

            if (consumers instanceof VertexConsumerProvider.Immediate imm) {
                imm.draw(RenderLayer.getDebugQuads());
            }
        });
    }
}
