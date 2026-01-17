package name.skyclient.gui.hud;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class HudScreen extends Screen {

    private final List<Panel> panels = new ArrayList<>();

    public HudScreen() {
        super(Text.literal("SkyClient"));
    }

    @Override
    protected void init() {
        panels.clear();
        String[] categories = {"Dungeons", "Foraging", "Mining", "Events", "Farming", "Slayers", "Misc"};

        int startX = 8;
        int startY = 8;

        int panelW = 120;
        int gap = 3;

        for (int i = 0; i < categories.length; i++) {
            int x = startX + i * (panelW + gap);
            panels.add(new Panel(x, startY, panelW, categories[i]));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        for (Panel panel : panels) {
            panel.render(context, mouseX, mouseY);
        }
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        for (Panel panel : panels) {
            if (panel.mouseClicked(click.x(), click.y(), click.button())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(Click click) {
        for (Panel panel : panels) {
            panel.mouseReleased();
        }
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(Click click, double offsetX, double offsetY) {
        for (Panel panel : panels) {
            if (panel.mouseDragged(click.x(), click.y(), click.button())) {
                return true;
            }
        }
        return super.mouseDragged(click, offsetX, offsetY);
    }
}