package name.skyclient.gui.hud;

import name.skyclient.module.Module;
import name.skyclient.module.ModuleManager;
import name.skyclient.module.SkyblockHudModule;
import name.skyclient.skyblock.SkyblockUtils;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class HudOverlay {

    public static void register() {
        HudRenderCallback.EVENT.register(HudOverlay::render);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        // Only show on SkyBlock
        if (!SkyblockHudModule.isOnHypixelSkyblock(client)) return;

        // Get module
        Module module = ModuleManager.getByName("SkyBlock HUD");
        if (!(module instanceof SkyblockHudModule hud)) return;
        if (!hud.isEnabled()) return;

        int x = 6;
        int y = 6;
        int line = 0;

        context.drawText(client.textRenderer, "SkyBlock HUD", x, y, 0xFF00C8C8, false);
        line++;

        if (hud.showArea.get()) {
            context.drawText(client.textRenderer,
                    "Area: " + SkyblockUtils.getArea(client),
                    x, y + line * 12,
                    0xFFFFFFFF, false
            );
            line++;
        }

        if (hud.showCoords.get()) {
            int px = client.player.getBlockPos().getX();
            int py = client.player.getBlockPos().getY();
            int pz = client.player.getBlockPos().getZ();

            context.drawText(client.textRenderer,
                    "XYZ: " + px + " " + py + " " + pz,
                    x, y + line * 12,
                    0xFFFFFFFF, false
            );
            line++;
        }

        if (hud.showPurse.get()) {
            context.drawText(client.textRenderer,
                    "Purse: " + SkyblockUtils.getPurse(client),
                    x, y + line * 12,
                    0xFFFFFFFF, false
            );
            line++;
        }

        if (hud.showBits.get()) {
            context.drawText(client.textRenderer,
                    "Bits: " + SkyblockUtils.getBits(client),
                    x, y + line * 12,
                    0xFFFFFFFF, false
            );
            line++;
        }

        if (hud.showDate.get()) {
            context.drawText(client.textRenderer,
                    SkyblockUtils.getDateLine(client),
                    x, y + line * 12,
                    0xFFFFFFFF, false
            );
        }
    }
}
