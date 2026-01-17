package name.skyclient.gui.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class Panel {

    private int x, y;
    private final int width;
    private final int headerHeight = 16;
    private final String title;

    private final List<Module> modules = new ArrayList<>();

    // expand/collapse
    private boolean expanded = true;

    // drag
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    // fonts (these IDs must match your font json filenames)
    private static final StyleSpriteSource FONT_TITLE =
            new StyleSpriteSource.Font(Identifier.of("skyclient", "roboto_bold"));
    private static final StyleSpriteSource FONT_TEXT =
            new StyleSpriteSource.Font(Identifier.of("skyclient", "roboto_regular"));

    public Panel(int x, int y, int width, String title) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.title = title;

        // demo modules (you'll replace these later with real ones per category)
        modules.add(new Module("ExampleModule1"));
        modules.add(new Module("ExampleModule2"));
        modules.add(new Module("ExampleModule3"));
    }

    public void render(DrawContext context, int mouseX, int mouseY) {
        MinecraftClient mc = MinecraftClient.getInstance();

        // SalHack-ish colors
        int shadow = 0x22000000;
        int panelBg = 0x3A000000;
        int headerBg = 0x55000000;
        int accent = 0xFF00C8C8;

        int disabledText = 0xFFBFBFBF;
        int enabledText = 0xFF00C8C8;

        // row colors
        int hoverBg = 0x3300C8C8; // faint cyan overlay

        // Determine panel height dynamically
        int rowHeight = mc.textRenderer.fontHeight + 3;
        int bodyHeight = expanded ? (modules.size() * rowHeight + 4) : 0;
        int height = headerHeight + bodyHeight;

        // shadow
        context.fill(x + 2, y + 2, x + width + 2, y + height + 2, shadow);

        // background
        context.fill(x, y, x + width, y + height, panelBg);

        // header
        context.fill(x, y, x + width, y + headerHeight, headerBg);

        // accent bar
        context.fill(x, y, x + 2, y + headerHeight, accent);

        // title
        Text titleText = Text.literal(title).setStyle(Style.EMPTY.withFont(FONT_TITLE));
        context.drawText(mc.textRenderer, titleText, x + 6, y + 5, accent, false);

        // expand indicator (tiny)
        context.drawText(
                mc.textRenderer,
                expanded ? "-" : "+",
                x + width - 10,
                y + 5,
                0xFFFFFFFF,
                false
        );

        if (!expanded) return;

        // modules list area
        int listTop = y + headerHeight + 2;
        int textX = x + 6;

        for (int i = 0; i < modules.size(); i++) {
            Module m = modules.get(i);
            int rowY = listTop + i * rowHeight;

            // hover highlight
            if (isHoveringModule(mouseX, mouseY, i, rowHeight)) {
                context.fill(x, rowY - 1, x + width, rowY + rowHeight - 1, hoverBg);
            }

            Text moduleText = Text.literal(m.getName()).setStyle(Style.EMPTY.withFont(FONT_TEXT));
            context.drawText(
                    mc.textRenderer,
                    moduleText,
                    textX,
                    rowY,
                    m.isEnabled() ? enabledText : disabledText,
                    false
            );
        }
    }

    // --- input handling ---

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHoveringHeader(mouseX, mouseY)) {
            // Start drag on left click header
            dragging = true;
            dragOffsetX = (int) mouseX - x;
            dragOffsetY = (int) mouseY - y;
            return true;
        }

        // Right click header toggles expand/collapse (SalHack-ish)
        if (button == 1 && isHoveringHeader(mouseX, mouseY)) {
            expanded = !expanded;
            return true;
        }

        // Left click module toggles it
        if (expanded && button == 0) {
            MinecraftClient mc = MinecraftClient.getInstance();
            int rowHeight = mc.textRenderer.fontHeight + 3;
            int idx = getModuleIndexAt(mouseX, mouseY, rowHeight);
            if (idx != -1) {
                modules.get(idx).toggle();
                return true;
            }
        }

        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        if (dragging && button == 0) {
            x = (int) mouseX - dragOffsetX;
            y = (int) mouseY - dragOffsetY;
            return true;
        }
        return false;
    }

    public void mouseReleased() {
        dragging = false;
    }

    // --- helpers ---

    private boolean isHoveringHeader(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width
                && mouseY >= y && mouseY <= y + headerHeight;
    }

    private boolean isHoveringModule(double mouseX, double mouseY, int index, int rowHeight) {
        int listTop = y + headerHeight + 2;
        int rowY = listTop + index * rowHeight;
        return mouseX >= x && mouseX <= x + width
                && mouseY >= rowY && mouseY <= rowY + rowHeight;
    }

    private int getModuleIndexAt(double mouseX, double mouseY, int rowHeight) {
        if (mouseY < y + headerHeight) return -1;

        int listTop = y + headerHeight + 2;
        int idx = (int) ((mouseY - listTop) / rowHeight);

        if (idx < 0 || idx >= modules.size()) return -1;
        if (!isHoveringModule(mouseX, mouseY, idx, rowHeight)) return -1;

        return idx;
    }
}
