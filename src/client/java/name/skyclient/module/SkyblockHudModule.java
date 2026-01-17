package name.skyclient.module;

import name.skyclient.module.settings.BooleanSetting;
import name.skyclient.skyblock.SkyblockUtils;
import net.minecraft.client.MinecraftClient;

public class SkyblockHudModule extends Module {

    // ✅ settings
    public final BooleanSetting showArea   = new BooleanSetting("Show Area", true);
    public final BooleanSetting showCoords = new BooleanSetting("Show Coords", true);
    public final BooleanSetting showPurse  = new BooleanSetting("Show Purse", true);
    public final BooleanSetting showBits   = new BooleanSetting("Show Bits", true);
    public final BooleanSetting showDate   = new BooleanSetting("Show Date", true);

    public SkyblockHudModule() {
        super("SkyBlock HUD");

        addSetting(showArea);
        addSetting(showCoords);
        addSetting(showPurse);
        addSetting(showBits);
        addSetting(showDate);
    }

    public static boolean isOnHypixelSkyblock(MinecraftClient client) {
        return SkyblockUtils.isOnSkyblock(client);
    }
}
