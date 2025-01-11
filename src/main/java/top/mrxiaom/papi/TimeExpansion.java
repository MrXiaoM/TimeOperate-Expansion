package top.mrxiaom.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.Configurable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class TimeExpansion extends PlaceholderExpansion implements Configurable {
    private final Pattern ROUND_BRACKET_PLACEHOLDER_PATTERN = Pattern.compile("[(]([^()]+)[)]");
    private final boolean supportReplacer = isReplacerPresent();
    private int hourOffset = 8;
    @NotNull
    @Override
    public String getIdentifier() {
        return "timeoperate";
    }

    @NotNull
    @Override
    public String getAuthor() {
        return "MrXiaoM";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "1.2.1";
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

    private String setRoundBracketPlaceholders(OfflinePlayer p, String params) {
        if (supportReplacer) {
            return CustomReplacer.ROUND_BRACKET.apply(params, p,
                    PlaceholderAPIPlugin.getInstance().getLocalExpansionManager()::getExpansion);
        } else {
            return PlaceholderAPI.setPlaceholders(p, params, ROUND_BRACKET_PLACEHOLDER_PATTERN);
        }
    }

    @Override
    public String onRequest(OfflinePlayer p, @NotNull String params) {
        params = setRoundBracketPlaceholders(p, params);
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
            } else if (override.contains(":")) {
                String[] split = override.split(":", 3);
                if (split.length == 2) {
                    try {
                        if (!split[0].equals("~")) time = time.withHour(Integer.parseInt(split[0]));
                        if (!split[1].equals("~")) time = time.withMinute(Integer.parseInt(split[1]));
                        time = time.withSecond(0);
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (split.length == 3) {
                    try {
                        if (!split[0].equals("~")) time = time.withHour(Integer.parseInt(split[0]));
                        if (!split[1].equals("~")) time = time.withMinute(Integer.parseInt(split[1]));
                        if (!split[2].equals("~")) time = time.withSecond(Integer.parseInt(split[2]));
                    } catch (NumberFormatException ignored) {
                    }
                }
            } else if (override.startsWith("w+")) {
                try {
                    int weeks = Integer.parseInt(override.substring(2));
                    if (!time.getDayOfWeek().equals(DayOfWeek.MONDAY)) { // 重置回周一
                        time = time.minusDays(time.getDayOfWeek().getValue() - 1);
                    }
                    if (weeks > 0) time = time.plusWeeks(weeks);
                } catch (NumberFormatException ignored) {
                }
            } else if (override.startsWith("w-")) {
                try {
                    int weeks = Integer.parseInt(override.substring(2));
                    if (!time.getDayOfWeek().equals(DayOfWeek.MONDAY)) { // 重置回周一
                        time = time.minusDays(time.getDayOfWeek().getValue() - 1);
                    }
                    if (weeks > 0) time = time.minusWeeks(weeks);
                } catch (NumberFormatException ignored) {
                }
            } else if (override.startsWith("M+")) {
                try {
                    int months = Integer.parseInt(override.substring(2));
                    time = time.plusMonths(months);
                } catch (NumberFormatException ignored) {
                }
            } else if (override.startsWith("M-")) {
                try {
                    int months = Integer.parseInt(override.substring(2));
                    time = time.minusMonths(months);
                } catch (NumberFormatException ignored) {
                }
            } else if (override.toLowerCase().startsWith("y+")) {
                try {
                    int years = Integer.parseInt(override.substring(2));
                    time = time.plusYears(years);
                } catch (NumberFormatException ignored) {
                }
            } else if (override.toLowerCase().startsWith("y-")) {
                try {
                    int years = Integer.parseInt(override.substring(2));
                    time = time.minusYears(years);
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

    static boolean isReplacerPresent() {
        try {
            Class.forName("me.clip.placeholderapi.replacer.Replacer");
            return true;
        } catch (ClassNotFoundException ignored) {
            return false;
        }
    }
}
