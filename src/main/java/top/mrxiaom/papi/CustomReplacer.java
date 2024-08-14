package top.mrxiaom.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Function;

public class CustomReplacer{
    public static final CustomReplacer ROUND_BRACKET = new CustomReplacer('(', ')');

    private final char head;
    private final char tail;
    public CustomReplacer(char head, char tail) {
        this.head = head;
        this.tail = tail;
    }

    @NotNull
    public String apply(@NotNull final String text, @Nullable final OfflinePlayer player,
                        @NotNull final Function<String, @Nullable PlaceholderExpansion> lookup) {
        final char[] chars = text.toCharArray();
        final StringBuilder builder = new StringBuilder(text.length());

        final StringBuilder identifier = new StringBuilder();
        final StringBuilder parameters = new StringBuilder();

        for (int i = 0; i < chars.length; i++) {
            final char l = chars[i];

            if (l != head || i + 1 >= chars.length) {
                builder.append(l);
                continue;
            }

            boolean identified = false;
            boolean invalid = true;
            boolean hadSpace = false;

            while (++i < chars.length) {
                final char p = chars[i];

                if (p == ' ' && !identified) {
                    hadSpace = true;
                    break;
                }
                if (p == tail) {
                    invalid = false;
                    break;
                }

                if (p == '_' && !identified) {
                    identified = true;
                    continue;
                }

                if (identified) {
                    parameters.append(p);
                } else {
                    identifier.append(p);
                }
            }

            final String identifierString = identifier.toString();
            final String lowercaseIdentifierString = identifierString.toLowerCase(Locale.ROOT);
            final String parametersString = parameters.toString();

            identifier.setLength(0);
            parameters.setLength(0);

            if (invalid) {
                builder.append(head).append(identifierString);

                if (identified) {
                    builder.append('_').append(parametersString);
                }

                if (hadSpace) {
                    builder.append(' ');
                }
                continue;
            }

            final PlaceholderExpansion placeholder = lookup.apply(lowercaseIdentifierString);
            if (placeholder == null) {
                builder.append(head).append(identifierString);

                if (identified) {
                    builder.append('_');
                }

                builder.append(parametersString).append(tail);
                continue;
            }

            final String replacement = placeholder.onRequest(player, parametersString);
            if (replacement == null) {
                builder.append(head).append(identifierString);

                if (identified) {
                    builder.append('_');
                }

                builder.append(parametersString).append(tail);
                continue;
            }

            builder.append(replacement);
        }

        return builder.toString();
    }
}
