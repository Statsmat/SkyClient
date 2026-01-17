package name.skyclient.skyblock;

import net.minecraft.client.MinecraftClient;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;

import java.util.ArrayList;
import java.util.List;

public class SkyblockUtils {

    /**
     * Returns the sidebar objective, or null if none.
     */
    public static ScoreboardObjective getSidebarObjective(MinecraftClient client) {
        if (client.world == null) return null;
        Scoreboard sb = client.world.getScoreboard();
        return sb.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
    }

    /**
     * Returns raw sidebar lines (top->bottom).
     *
     * In 1.21.10, getting the actual sidebar lines depends on how you parse teams/scores.
     * Keep this method as the one place you implement that logic.
     */
    public static List<String> getSidebarLines(MinecraftClient client) {
        // TODO: implement real sidebar parsing here
        // (no debug/fake logic requested)
        return new ArrayList<>();
    }

    /**
     * True if sidebar looks like SkyBlock.
     */
    public static boolean isOnSkyblock(MinecraftClient client) {
        ScoreboardObjective obj = getSidebarObjective(client);
        if (obj == null) return false;

        String title = clean(obj.getDisplayName().getString()).toUpperCase();
        if (title.contains("SKYBLOCK")) return true;

        for (String line : getSidebarLines(client)) {
            String upper = line.toUpperCase();
            if (upper.contains("HYPIXEL") || upper.contains("WWW.HYPIXEL.NET")) {
                return true;
            }
        }

        return false;
    }

    /**
     * Tries to extract SkyBlock area using "⏣ Area Name" line.
     */
    public static String getArea(MinecraftClient client) {
        for (String line : getSidebarLines(client)) {
            if (line.contains("⏣")) {
                int idx = line.indexOf("⏣");
                String after = line.substring(idx + 1).trim();
                if (!after.isEmpty()) return after;
            }
        }
        return "Unknown";
    }

    public static String getPurse(MinecraftClient client) {
        for (String line : getSidebarLines(client)) {
            // Example: "Purse: 12,345"
            if (line.toLowerCase().contains("purse")) {
                return line.replaceAll("(?i)purse\\s*[:]", "").trim();
            }
        }
        return "N/A";
    }

    public static String getBits(MinecraftClient client) {
        for (String line : getSidebarLines(client)) {
            // Example: "Bits: 1,234"
            if (line.toLowerCase().contains("bits")) {
                return line.replaceAll("(?i)bits\\s*[:]", "").trim();
            }
        }
        return "N/A";
    }

    public static String getDateLine(MinecraftClient client) {
        for (String line : getSidebarLines(client)) {
            // Hypixel usually has something like: "Early Spring 1st"
            // or "Late Winter 23rd"
            String lower = line.toLowerCase();

            boolean hasSeason = lower.contains("spring") || lower.contains("summer")
                    || lower.contains("autumn") || lower.contains("fall")
                    || lower.contains("winter");

            if (hasSeason) {
                return line.trim();
            }
        }
        return "N/A";
    }

    /**
     * Removes formatting weirdness.
     */
    private static String clean(String s) {
        return s.replace("\u00A0", " ").trim();
    }
}
