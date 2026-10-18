package dev.herovisuals.module;

import dev.herovisuals.render.TrailRenderer;
import dev.herovisuals.util.Fonts;

import java.util.ArrayList;
import java.util.List;

public final class Modules {
    private Modules() {}

    // ---------------- RENDER ----------------
    public static class ChinaHatModule extends Module {
        public final ColorModes color = new ColorModes(this, 0xFF66CC);
        public final Setting.Num size = add(new Setting.Num("Size", 0.3, 1.0, 0.01, 0.62));
        public final Setting.Num height = add(new Setting.Num("Height", 0.1, 0.7, 0.01, 0.30));
        public final Setting.Num alpha = add(new Setting.Num("Alpha", 20, 255, 1, 120));
        public final Setting.Bool outline = add(new Setting.Bool("Outline", true));
        public final Setting.Bool firstPerson = add(new Setting.Bool("First person", false));
        public ChinaHatModule() { super("China Hat", "Китайская шляпа над головой", Category.RENDER, true); }
    }

    public static class TrailModule extends Module {
        public final Setting.Mode style = add(new Setting.Mode("Style", "Ribbon", "Cubes"));
        public final ColorModes color = new ColorModes(this, 0x8B5CF6);
        public final Setting.Num length = add(new Setting.Num("Length", 10, 120, 1, 50));
        public final Setting.Num width = add(new Setting.Num("Width", 0.04, 0.4, 0.01, 0.12));
        public final Setting.Num alpha = add(new Setting.Num("Alpha", 30, 255, 1, 190));
        public final Setting.Num offsetY = add(new Setting.Num("Offset Y", 0.0, 1.8, 0.05, 0.1));
        public TrailModule() { super("Trail", "Плавный след за игроком", Category.RENDER, true); }
        @Override public void onDisable() { TrailRenderer.clear(); }
    }

    public static class TargetEspModule extends Module {
        public final Setting.Mode style = add(new Setting.Mode("Style", "Ghost", "Ring"));
        public final ColorModes color = new ColorModes(this, 0x8B5CF6);
        public final Setting.Num count = add(new Setting.Num("Ghosts", 1, 6, 1, 3));
        public final Setting.Num radius = add(new Setting.Num("Radius", 0.4, 1.6, 0.05, 0.8));
        public final Setting.Num tail = add(new Setting.Num("Tail", 8, 60, 1, 28));
        public final Setting.Num speed = add(new Setting.Num("Speed", 0.5, 6, 0.1, 2.2));
        public final Setting.Num size = add(new Setting.Num("Size", 0.03, 0.25, 0.01, 0.09));
        public final Setting.Num alpha = add(new Setting.Num("Alpha", 30, 255, 1, 220));
        public final Setting.Bool self = add(new Setting.Bool("Around me", false));
        public final Setting.Bool crosshair = add(new Setting.Bool("Crosshair target", true));
        public TargetEspModule() { super("Target ESP", "Ghost / кольцо вокруг цели", Category.RENDER, true); }
    }

    // ---------------- WORLD ----------------
    public static class SnowModule extends Module {
        public final Setting.Num count = add(new Setting.Num("Count", 100, 2000, 50, 600));
        public final Setting.Num speed = add(new Setting.Num("Speed", 0.5, 6, 0.1, 1.6));
        public final Setting.Num size = add(new Setting.Num("Size", 0.02, 0.15, 0.01, 0.05));
        public final Setting.Num wind = add(new Setting.Num("Wind", -1, 1, 0.1, 0.2));
        public final Setting.Num range = add(new Setting.Num("Range", 6, 24, 1, 14));
        public final Setting.Bool outdoors = add(new Setting.Bool("Only outdoors", true));
        public SnowModule() { super("Snow", "Снег вокруг игрока", Category.WORLD, false); }
    }

    public static class SkyModule extends Module {
        public final ColorValue color;
        public final Setting.Num alpha = add(new Setting.Num("Opacity", 0, 255, 1, 140));
        public final Setting.Bool gradient = add(new Setting.Bool("Gradient", true));
        public final ColorValue horizon;
        public SkyModule() {
            super("Sky", "Свой цвет и прозрачность неба", Category.WORLD, false);
            color = new ColorValue(this, "Sky", 0x7B4DFF, () -> true);
            horizon = new ColorValue(this, "Horizon", 0xFF8AD8, () -> gradient.value);
        }
    }

    // ---------------- HUD ----------------
    public static class HudModule extends Module {
        public int x, y, w, h;
        public boolean placed;
        public HudModule(String n, String d) { super(n, d, Category.HUD, true); }
    }

    public static class WatermarkModule extends HudModule {
        public final Setting.Bool fps = add(new Setting.Bool("Show FPS", true));
        public WatermarkModule() { super("Watermark", "Название клиента и FPS"); }
    }

    public static class ModuleListModule extends HudModule {
        public final ColorModes color = new ColorModes(this, 0x8B5CF6);
        public final Setting.Bool background = add(new Setting.Bool("Background", true));
        public ModuleListModule() { super("Module List", "Список включённых модулей"); }
    }

    public static class InfoModule extends HudModule {
        public final Setting.Bool coords = add(new Setting.Bool("Coordinates", true));
        public final Setting.Bool speed = add(new Setting.Bool("Speed", true));
        public InfoModule() { super("Info", "Координаты и скорость"); }
    }

    // ---------------- CLIENT ----------------
    public static class InterfaceModule extends Module {
        public final Setting.Mode font = add(new Setting.Mode("Font", Fonts.NAMES));
        public final Setting.Bool shadow = add(new Setting.Bool("Text shadow", true));
        public final Setting.Bool menu = add(new Setting.Bool("Purple menu", true));
        public final Setting.Bool menuAll = add(new Setting.Bool("Style all menus", true));
        public final Setting.Num dots = add(new Setting.Num("GUI dots", 0, 300, 10, 140));
        public InterfaceModule() { super("Interface", "Шрифт, меню, GUI", Category.CLIENT, true); }
    }

    public static class FpsBoostModule extends Module {
        public final Setting.Bool particles = add(new Setting.Bool("Minimal particles", true));
        public final Setting.Bool clouds = add(new Setting.Bool("Clouds off", true));
        public final Setting.Bool shadows = add(new Setting.Bool("Entity shadows off", true));
        public final Setting.Bool fast = add(new Setting.Bool("Fast graphics", false));
        public final Setting.Bool noAo = add(new Setting.Bool("Smooth lighting off", false));
        public final Setting.Bool blend = add(new Setting.Bool("Biome blend off", false));
        public final Setting.Num entityDist = add(new Setting.Num("Entity distance", 0.5, 1.0, 0.05, 0.7));
        public FpsBoostModule() { super("FPS Boost", "Оптимизация настроек графики", Category.CLIENT, false); }

        public String key() {
            return enabled + "|" + particles.value + clouds.value + shadows.value + fast.value + noAo.value + blend.value + entityDist.value;
        }
    }

    public static final ChinaHatModule CHINA_HAT = new ChinaHatModule();
    public static final TrailModule TRAIL = new TrailModule();
    public static final TargetEspModule TARGET_ESP = new TargetEspModule();
    public static final SnowModule SNOW = new SnowModule();
    public static final SkyModule SKY = new SkyModule();
    public static final WatermarkModule WATERMARK = new WatermarkModule();
    public static final ModuleListModule MODULE_LIST = new ModuleListModule();
    public static final InfoModule INFO = new InfoModule();
    public static final InterfaceModule INTERFACE = new InterfaceModule();
    public static final FpsBoostModule FPS_BOOST = new FpsBoostModule();

    public static final List<Module> ALL = List.of(CHINA_HAT, TRAIL, TARGET_ESP, SNOW, SKY,
            WATERMARK, MODULE_LIST, INFO, INTERFACE, FPS_BOOST);

    public static List<Module> byCategory(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : ALL) if (m.category == c) out.add(m);
        return out;
    }
}
