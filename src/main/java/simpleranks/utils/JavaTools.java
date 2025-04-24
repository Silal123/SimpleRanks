package simpleranks.utils;

import org.bukkit.Bukkit;

public class JavaTools {

    public static boolean isLower(String compareVersion, String serverVersion) {
        String[] compareParts = compareVersion.split("\\.");
        String[] serverParts = serverVersion.split("\\.");

        int length = Math.max(compareParts.length, serverParts.length);

        for (int i = 0; i < length; i++) {
            int comparePart = i < compareParts.length ? Integer.parseInt(compareParts[i]) : 0;
            int serverPart = i < serverParts.length ? Integer.parseInt(serverParts[i]) : 0;

            if (serverPart < comparePart) {
                return true;
            } else if (serverPart > comparePart) {
                return false;
            }
        }

        return false;
    }

    public static String shortenWithDots(String input, int maxLength) {
        if (input.length() > maxLength) {
            if (maxLength <= 3) return "...".substring(0, maxLength);
            return input.substring(0, maxLength - 3) + "...";
        } else {
            return input;
        }
    }

    public static String getMcVersion() {
        String mcVersion = Bukkit.getVersion().split("\\(MC: ")[1].replace(")", "");
        return mcVersion;
    }

    public static boolean isLong(String s) {
        try {
            Long.valueOf(s);
            return true;
        } catch (Exception e) {}
        return false;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.valueOf(s);
            return true;
        } catch (Exception e) {}
        return false;
    }

    public static String convertMinutesToDaysHoursMinutes(int minutes) {
        int days = minutes / (24 * 60);
        int hours = (minutes % (24 * 60)) / 60;
        int remainingMinutes = minutes % 60;

        String result = "";
        if (days > 0) {
            result += days + " Day" + (days > 1 ? "s " : " ");
        }
        if (hours > 0) {
            result += hours + " Hour" + (hours > 1 ? "s " : " ");
        }
        if (remainingMinutes > 0) {
            result += remainingMinutes + " Minutes" + (remainingMinutes > 1 ? "s" : "");
        }

        if (result.equals("")) {
            result = "0 Minutes";
        }

        return result.trim();
    }

    public static String convertMinutesToDaysHoursMinutesShort(int minutes) {
        int days = minutes / (24 * 60);
        int hours = (minutes % (24 * 60)) / 60;
        int remainingMinutes = minutes % 60;

        String result = "";
        if (days > 0) {
            result += days + "d ";
        }
        if (hours > 0) {
            result += hours + "h ";
        }
        if (remainingMinutes > 0) {
            result += remainingMinutes + "min ";
        }

        if (result.equals("")) {
            result = "0min";
        }

        return result.trim();
    }
}
