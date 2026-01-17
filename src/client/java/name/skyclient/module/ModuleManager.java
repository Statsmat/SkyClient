package name.skyclient.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModuleManager {

    private static final List<Module> modules = new ArrayList<>();

    /**
     * Call this once during client init to register your modules.
     */
    public static void init() {
        modules.clear();

        // register modules here:
        modules.add(new SkyblockHudModule());
    }

    public static List<Module> getModules() {
        return Collections.unmodifiableList(modules);
    }

    public static Module getByName(String name) {
        for (Module m : modules) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }
}
