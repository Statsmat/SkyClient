package name.skyclient;

import name.skyclient.gui.hud.HudOverlay;
import name.skyclient.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import name.skyclient.input.KeyInputHandler;

public class SkyClientClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		KeyInputHandler.register();
		ModuleManager.init();
		HudOverlay.register();
	}
}