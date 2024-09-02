package me.neovitalism.neoclear.util;

public class TimeUtil {
    public static long tryParse(String input) {
        String[] split = input.split("-");
        if(split.length != 2) return -1;
        long time;
        try {
            time = Long.parseLong(split[0]);
        } catch (NumberFormatException e) {
            return -1;
        }
        String timeUnit = split[1];
        if(timeUnit.replaceFirst("seconds?", "").isEmpty()) return time;
        if(timeUnit.replaceFirst("minutes?", "").isEmpty()) return time*60;
        if(timeUnit.replaceFirst("hours?", "").isEmpty()) return time*3600;
        return -1;
    }

    public static String getFormattedTime(long seconds) {
        StringBuilder timeString = new StringBuilder();
        boolean usedComma = false;
        long newSeconds = seconds;
        if(newSeconds >= 86400) {
            long days = newSeconds / 86400;
            timeString.append(days).append(" day");
            if(days > 1) timeString.append("s");
            newSeconds-=days*86400;
            if(newSeconds == 0) return timeString.toString();
            boolean oneLeft = (newSeconds % 3600 == 0) || (newSeconds < 3600 && newSeconds % 60 == 0) || (newSeconds < 60);
            if(oneLeft) {
                timeString.append(" and ");
            } else {
                timeString.append(", ");
                usedComma = true;
            }
        }
        if(newSeconds >= 3600) {
            long hours = newSeconds / 3600;
            timeString.append(hours).append(" hour");
            if(hours > 1) timeString.append("s");
            newSeconds-=hours*3600;
            if(newSeconds == 0) return timeString.toString();
            boolean oneLeft = (newSeconds % 60 == 0) || (newSeconds < 60);
            if(oneLeft) {
                if(usedComma) timeString.append(",");
                timeString.append(" and ");
            } else {
                timeString.append(", ");
                usedComma = true;
            }
        }
        if(newSeconds >= 60) {
            long minutes = newSeconds / 60;
            timeString.append(minutes).append(" minute");
            if(minutes > 1) timeString.append("s");
            newSeconds-=minutes*60;
            if(newSeconds == 0) return timeString.toString();
            if(usedComma) timeString.append(",");
            timeString.append(" and ");
        }
        timeString.append(newSeconds).append(" second");
        if(newSeconds == 0 || newSeconds > 1) timeString.append("s");
        return timeString.toString();
    }
}
