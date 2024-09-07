package simpleranks.utils;

public class JavaTools {

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
