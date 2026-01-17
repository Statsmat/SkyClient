package name.skyclient.gui.hud;

import name.skyclient.module.Module;
import name.skyclient.module.ModuleManager;
import name.skyclient.module.settings.BooleanSetting;
import name.skyclient.module.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Panel {

    private int x, y;
    private final int width;
    private final int headerHeight = 16;
    private final String title;

    private final List<Module> modules = new ArrayList<>();
    private final Map<Module, Boolean> moduleOpen = new HashMap<>();

    // expand/collapse whole panel
    private boolean expanded = true;

    // drag
    private boolean dragging = false;
    private int dragOffsetX, dragOffsetY;

    // fonts
    private static final StyleSpriteSource FONT_TITLE =
            new StyleSpriteSource.Font(Identifier.of("skyclient", "roboto_bold"));
    private static final StyleSpriteSource FONT_TEXT =
            new StyleSpriteSource.Font(Identifier.of("skyclient", "roboto_regular"));

    public Panel(int x, int y, int width, String title) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.title = title;

        // TEMP category logic: modules only in Misc for now
        if (title.equalsIgnoreCase("Misc")) {
            modules.addAll(ModuleManager.getModules());
        }

        for (Module m : modules) {
            moduleOpen.put(m, false);
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY) {
        MinecraftClient mc = MinecraftClient.getInstance();

        // SalHack-ish colors
        int shadow = 0x22000000;
        int panelBg = 0x3A000000;
        int headerBg = 0x55000000;
        int accent = 0xFF00C8C8;

        // brighter text
        int disabledText = 0xFFE6E6E6;         // brighter than before
        int enabledText = 0xFF00C8C8;

        int settingLabelColor = 0xFFF0F0F0;    // near pure white
        int offTextColor = 0xFFB0B0B0;         // readable OFF

        int hoverBg = 0x3300C8C8;

        // row sizing + centering
        int rowHeight = mc.textRenderer.fontHeight + 6; // taller rows = better centering
        int fontH = mc.textRenderer.fontHeight;
        int textYOffset = (rowHeight - fontH) / 2;

        int height = getPanelHeight(mc, rowHeight);

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

        // expand indicator
        context.drawText(mc.textRenderer, expanded ? "-" : "+", x + width - 10, y + 5, 0xFFFFFFFF, false);

        if (!expanded) return;

        int listTop = y + headerHeight + 2;
        int currentY = listTop;

        for (Module m : modules) {

            // module hover
            if (isHoveringRect(mouseX, mouseY, x, currentY - 1, width, rowHeight)) {
                context.fill(x + 2, currentY - 1, x + width, currentY + rowHeight - 1, hoverBg);
            }

            // module name (centered vertically)
            Text moduleText = Text.literal(m.getName()).setStyle(Style.EMPTY.withFont(FONT_TEXT));
            context.drawText(
                    mc.textRenderer,
                    moduleText,
                    x + 6,
                    currentY + textYOffset,
                    m.isEnabled() ? enabledText : disabledText,
                    false
            );

            // arrow if module has settings (centered vertically)
            if (!m.getSettings().isEmpty()) {
                boolean open = moduleOpen.getOrDefault(m, false);
                context.drawText(
                        mc.textRenderer,
                        open ? "v" : ">",
                        x + width - 10,
                        currentY + textYOffset,
                        0xFFFFFFFF,
                        false
                );
            }

            currentY += rowHeight;

            // settings
            boolean open = moduleOpen.getOrDefault(m, false);
            if (open) {
                for (Setting s : m.getSettings()) {

                    // settings hover
                    if (isHoveringRect(mouseX, mouseY, x, currentY - 1, width, rowHeight)) {
                        context.fill(x + 2, currentY - 1, x + width, currentY + rowHeight - 1, hoverBg);
                    }

                    // guide line
                    context.fill(x + 5, currentY - 1, x + 6, currentY + rowHeight - 1, 0x5500C8C8);

                    if (s instanceof BooleanSetting b) {

                        // fixed ON/OFF column
                        int rightPadding = 8;
                        int slotWidth = 22;
                        int stateColumnRight = x + width - rightPadding;
                        int stateColumnLeft = stateColumnRight - slotWidth;

                        // label trimmed so it can't enter ON/OFF column
                        int labelX = x + 8;
                        int labelMaxWidth = (stateColumnLeft - 6) - labelX;

                        String labelStr = b.getName();
                        if (labelMaxWidth > 0) {
                            labelStr = mc.textRenderer.trimToWidth(labelStr, labelMaxWidth);
                        }

                        Text label = Text.literal(labelStr).setStyle(Style.EMPTY.withFont(FONT_TEXT));
                        context.drawText(
                                mc.textRenderer,
                                label,
                                labelX,
                                currentY + textYOffset,
                                settingLabelColor,
                                false
                        );

                        // ON/OFF aligned to same X
                        String state = b.get() ? "ON" : "OFF";
                        int stateColor = b.get() ? enabledText : offTextColor;

                        int stateX = stateColumnLeft;
                        context.drawText(
                                mc.textRenderer,
                                Text.literal(state).setStyle(Style.EMPTY.withFont(FONT_TEXT)),
                                stateX,
                                currentY + textYOffset,
                                stateColor,
                                false
                        );

                    } else {
                        // other setting types later
                        Text label = Text.literal(s.getName()).setStyle(Style.EMPTY.withFont(FONT_TEXT));
                        context.drawText(
                                mc.textRenderer,
                                label,
                                x + 8,
                                currentY + textYOffset,
                                settingLabelColor,
                                false
                        );
                    }

                    currentY += rowHeight;
                }
            }
        }
    }

    // --- input handling ---

    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        // drag header
        if (button == 0 && isHoveringHeader(mouseX, mouseY)) {
            dragging = true;
            dragOffsetX = (int) mouseX - x;
            dragOffsetY = (int) mouseY - y;
            return true;
        }

        // collapse panel
        if (button == 1 && isHoveringHeader(mouseX, mouseY)) {
            expanded = !expanded;
            return true;
        }

        if (!expanded) return false;

        MinecraftClient mc = MinecraftClient.getInstance();
        int rowHeight = mc.textRenderer.fontHeight + 6;

        int listTop = y + headerHeight + 2;
        int currentY = listTop;

        for (Module m : modules) {

            // clicked module row
            if (isHoveringRect(mouseX, mouseY, x, currentY - 1, width, rowHeight)) {

                // left click toggles module
                if (button == 0) {
                    m.toggle();
                    return true;
                }

                // right click opens settings
                if (button == 1 && !m.getSettings().isEmpty()) {
                    moduleOpen.put(m, !moduleOpen.getOrDefault(m, false));
                    return true;
                }
            }

            currentY += rowHeight;

            // clicked settings rows
            boolean open = moduleOpen.getOrDefault(m, false);
            if (open) {
                for (Setting s : m.getSettings()) {

                    if (isHoveringRect(mouseX, mouseY, x, currentY - 1, width, rowHeight)) {
                        if (button == 0 && s instanceof BooleanSetting b) {
                            b.toggle();
                            return true;
                        }
                        return true;
                    }

                    currentY += rowHeight;
                }
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

    private int getPanelHeight(MinecraftClient mc, int rowHeight) {
        int height = headerHeight + 4;

        if (!expanded) return headerHeight;

        for (Module m : modules) {
            height += rowHeight;

            boolean open = moduleOpen.getOrDefault(m, false);
            if (open) {
                height += m.getSettings().size() * rowHeight;
            }
        }

        return Math.max(height, headerHeight + 4);
    }

    private boolean isHoveringHeader(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width
                && mouseY >= y && mouseY <= y + headerHeight;
    }

    private boolean isHoveringRect(double mouseX, double mouseY, int rx, int ry, int rw, int rh) {
        return mouseX >= rx && mouseX <= rx + rw
                && mouseY >= ry && mouseY <= ry + rh;
    }
}
