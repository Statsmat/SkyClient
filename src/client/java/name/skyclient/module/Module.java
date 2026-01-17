package name.skyclient.module;

import name.skyclient.module.settings.Setting;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private final String name;
    private boolean enabled;

    private final List<Setting> settings = new ArrayList<>();

    public Module(String name) {
        this.name = name;
        this.enabled = false;
    }

    public String getName() { return name; }
    public boolean isEnabled() { return enabled; }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    protected void onEnable() {}
    protected void onDisable() {}

    // ✅ settings
    protected void addSetting(Setting setting) {
        settings.add(setting);
    }

    public List<Setting> getSettings() {
        return settings;
    }
}
