package dev.herovisuals.module;

public enum Category {
    RENDER("Render"),
    WORLD("World"),
    HUD("HUD"),
    CLIENT("Client");

    public final String label;
    Category(String label) { this.label = label; }
}
