package dev.herovisuals.render;

import dev.herovisuals.module.Module;
import dev.herovisuals.module.Modules;
import dev.herovisuals.module.Category;
import dev.herovisuals.util.ColorUtil;
import dev.herovisuals.util.Fonts;
import dev.herovisuals.util.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class HudRenderer {
    private static Modules.HudModule dragging;
    private static double offX, offY;
    private static boolean prevDown;

    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) return;

        boolean editing = mc.currentScreen instanceof ChatScreen;
        int sw = ctx.getScaledWindowWidth(), sh = ctx.getScaledWindowHeight();

        // ---------- содержимое и размеры ----------
        Modules.WatermarkModule wm = Modules.WATERMARK;
        String wm1 = "HeroVisuals";
        String wm2 = wm.fps.value ? " | " + mc.getCurrentFps() + " fps" : "";
        wm.w = Fonts.width(wm1) + Fonts.width(wm2) + 14;
        wm.h = 18;

        Modules.ModuleListModule ml = Modules.MODULE_LIST;
        List<Module> active = new ArrayList<>();
        for (Module m : Modules.ALL) {
            if (m.enabled && m.category != Category.HUD && m != Modules.INTERFACE) active.add(m);
        }
        active.sort(Comparator.comparingInt((Module m) -> Fonts.width(m.name)).reversed());
        List<String> names = new ArrayList<>();
        for (Module m : active) names.add(m.name);
        if (names.isEmpty() && editing) names.add("Module List");
        int maxW = 0;
        for (String s : names) maxW = Math.max(maxW, Fonts.width(s));
        ml.w = maxW + 12;
        ml.h = Math.max(1, names.size()) * 13;

        Modules.InfoModule info = Modules.INFO;
        List<String> lines = new ArrayList<>();
        if (info.coords.value)
            lines.add(String.format(Locale.ROOT, "XYZ: %.1f %.1f %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ()));
        if (info.speed.value) {
            double dx = mc.player.getX() - mc.player.prevX, dz = mc.player.getZ() - mc.player.prevZ;
            lines.add(String.format(Locale.ROOT, "Speed: %.2f b/s", Math.sqrt(dx * dx + dz * dz) * 20.0));
        }
        int iw = 0;
        for (String s : lines) iw = Math.max(iw, Fonts.width(s));
        info.w = iw + 12;
        info.h = Math.max(1, lines.size()) * 11 + 6;

        // ---------- дефолтные позиции ----------
        place(wm, 6, 6);
        place(ml, sw - ml.w - 6, 6);
        place(info, 6, sh - info.h - 6);

        Modules.HudModule[] els = {wm, ml, info};

        // ---------- перетаскивание (когда открыт чат) ----------
        double mx = mc.mouse.getX() * sw / (double) mc.getWindow().getWidth();
        double my = mc.mouse.getY() * sh / (double) mc.getWindow().getHeight();
        boolean down = GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        if (editing) {
            if (down && !prevDown) {
                for (int i = els.length - 1; i >= 0; i--) {
                    Modules.HudModule e = els[i];
                    if (!visible(e, info, lines)) continue;
                    if (mx >= e.x && mx <= e.x + e.w && my >= e.y && my <= e.y + e.h) {
                        dragging = e;
                        offX = mx - e.x;
                        offY = my - e.y;
                        break;
                    }
                }
            }
            if (down && dragging != null) {
                dragging.x = (int) (mx - offX);
                dragging.y = (int) (my - offY);
            }
            if (!down) dragging = null;
        } else {
            dragging = null;
        }
        prevDown = down;

        for (Modules.HudModule e : els) {
            e.x = Math.max(0, Math.min(sw - e.w, e.x));
            e.y = Math.max(0, Math.min(sh - e.h, e.y));
        }

        // ---------- отрисовка ----------
        if (wm.enabled) {
            RenderUtil.rounded(ctx, wm.x, wm.y, wm.w, wm.h, 5, 0xB0141024);
            RenderUtil.rounded(ctx, wm.x + 6, wm.y + wm.h - 3, wm.w - 12, 2, 1, ColorUtil.ACCENT);
            Fonts.draw(ctx, wm1, wm.x + 7, wm.y + 5, ColorUtil.ACCENT);
            Fonts.draw(ctx, wm2, wm.x + 7 + Fonts.width(wm1), wm.y + 5, 0xFFFFFFFF);
        }

        if (ml.enabled) {
            for (int i = 0; i < names.size(); i++) {
                String s = names.get(i);
                int lw = Fonts.width(s);
                int y = ml.y + i * 13;
                int x = ml.x + ml.w - lw - 10;
                int col = ml.color.get(i * 0.08f, 255);
                if (ml.background.value) RenderUtil.rounded(ctx, x, y, lw + 10, 12, 4, 0x96141024);
                RenderUtil.rounded(ctx, ml.x + ml.w - 3, y + 2, 2, 8, 1, col);
                Fonts.draw(ctx, s, x + 4, y + 2, col);
            }
        }

        if (info.enabled && !lines.isEmpty()) {
            RenderUtil.rounded(ctx, info.x, info.y, info.w, info.h, 5, 0xB0141024);
            for (int i = 0; i < lines.size(); i++) {
                Fonts.draw(ctx, lines.get(i), info.x + 6, info.y + 5 + i * 11, i == 0 ? 0xFFFFFFFF : ColorUtil.ACCENT);
            }
        }

        if (editing) {
            for (Modules.HudModule e : els) {
                if (!visible(e, info, lines)) continue;
                ctx.drawBorder(e.x - 1, e.y - 1, e.w + 2, e.h + 2, ColorUtil.withAlpha(0x8B5CF6, e == dragging ? 255 : 150));
            }
            Fonts.drawCentered(ctx, "HeroVisuals: drag HUD elements with the mouse", sw / 2, 24, 0xFFD9CCFF);
        }
    }

    private static boolean visible(Modules.HudModule e, Modules.InfoModule info, List<String> lines) {
        if (!e.enabled) return false;
        return e != info || !lines.isEmpty();
    }

    private static void place(Modules.HudModule e, int defX, int defY) {
        if (!e.placed) {
            e.x = defX;
            e.y = defY;
            e.placed = true;
        }
    }
}
