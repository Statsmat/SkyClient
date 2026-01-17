package name.skyclient;

import net.fabricmc.api.ClientModInitializer;
import name.skyclient.input.KeyInputHandler;

public class SkyClientClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

		KeyInputHandler.register();
	}
}