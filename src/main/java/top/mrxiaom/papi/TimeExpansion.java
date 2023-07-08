package top.mrxiaom.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TimeExpansion extends PlaceholderExpansion implements Configurable {
    Pattern ROUND_BRACKET_PLACEHOLDER_PATTERN = Pattern.compile("[(]([^()]+)[)]");
    int hourOffset = 8;
    @Override
    public String getIdentifier() {
        return "timeoperate";
    }

    @Override
    public String getAuthor() {
        return "MrXiaoM";
    }

    @Override
    public String getVersion() {
        return "1.2.0";
    }
    @Override
    public Map<String, Object> getDefaults() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("hour-offset", 8);
        return defaults;
    }

    @Override
    public boolean register() {
        hourOffset = getInt("hour-offset", 8);
        return super.register();
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        params = PlaceholderAPI.setPlaceholders(p, params, ROUND_BRACKET_PLACEHOLDER_PATTERN);
        params = PlaceholderAPI.setBracketPlaceholders(p, params);
        String[] args = params.split("_");
        if (args.length >= 2) {
            try {
                LocalDateTime time = parse(args[0], hourOffset);
                if (time == null) return null;
                time = override(time, args, 2);
                return format(time, args[1], hourOffset);
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    public static LocalDateTime parse(String param, int hourOffset) {
        if (param.equalsIgnoreCase("now")) return LocalDateTime.now();
        if (param.contains("|")) {
            String[] args = param.split("\\|", 2);
            DateTimeFormatter format = DateTimeFormatter.ofPattern(args[0]);
            return LocalDateTime.parse(args[1], format);
        }
        long timestamp = Long.parseLong(param);
        return Instant.ofEpochSecond(timestamp)
                .atZone(ZoneOffset.ofHours(hourOffset))
                .toLocalDateTime();
    }

    public static String format(LocalDateTime time, String param, int hourOffset) {
        if (param.equalsIgnoreCase("unix")) return String.valueOf(
                time.toInstant(ZoneOffset.ofHours(hourOffset)).getEpochSecond()
        );
        DateTimeFormatter format = DateTimeFormatter.ofPattern(param);
        return time.format(format);
    }

    public static LocalDateTime override(LocalDateTime time, String[] args, int startIndex) {
        for (int i = startIndex; i < args.length; i++) {
            String override = args[i];
            if (override.startsWith("+")) {
                Duration d = parseDuration(override.substring(1));
                if (d != null) time = time.plusSeconds(d.getSeconds());
            }
            else if (override.startsWith("-")) {
                Duration d = parseDuration(override.substring(1));
                if (d != null) time = time.minusSeconds(d.getSeconds());
            }
            else if (override.contains("=")) {
                String[] specific = override.split("=", 2);
                String unit = specific[0];
                try {
                    int value = Integer.parseInt(specific[1]);
                    if (e(unit, "s", "second")) time = time.withSecond(value);
                    if (e(unit, "m", "minute")) time = time.withMinute(value);
                    if (e(unit, "h", "hour")) time = time.withHour(value);
                    if (e(unit, "d", "day")) time = time.withDayOfMonth(value);
                    if (e(unit, "M", "month")) time = time.withMonth(value);
                    if (e(unit, "y", "year")) time = time.withYear(value);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return time;
    }

    public static Duration parseDuration(String s) {
        try {
            s = s.toUpperCase().replace("D","DT");
            if (!s.contains("DT")) s = "T" + s;
            if (s.endsWith("T")) s = s.substring(0, s.length() - 1);
            return Duration.parse("P" + s);
        } catch (DateTimeParseException ignored){
            return null;
        }
    }

    /**
     * is equals one of the strings
     * @param s string
     * @param a another strings
     */
    static boolean e(String s, String... a) {
        return Arrays.asList(a).contains(s);
    }
}
