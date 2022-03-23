package com.infinityraider.infinitylib.utility;

import net.minecraft.Util;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

import java.text.MessageFormat;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility class for messaging players.
 */
public class MessageUtil {
    
    public static final char COLOR_CODE_ESCAPE = '`';
    public static final char COLOR_CODE_REPLACEMENT = '\u00a7';

    public static final void messagePlayer(@Nullable Player player, @Nonnull String format, Object... args) {
        messagePlayer(player, Util.NIL_UUID, format, args);
    }

    public static final void messagePlayer(@Nullable Player player, UUID id, @Nonnull String format, Object... args) {
        // If the player is null don't do anything.
        if (player == null) {
            return;
        }

        // Holder for formatted message.
        String message;

        // Format the message.
        try {
            message = MessageFormat.format(format, args);
        } catch (IllegalArgumentException ex) {
            message = "`4Message Formatting Error: \"" + format + "\"!`r";
        }
        
        // Colorize the message.
        message = colorize(message);

        // Send the message.
        player.sendMessage(new TextComponent(message), id);
    }
    
    public static final String colorize(@Nonnull String message) {
        return message.replace(COLOR_CODE_ESCAPE, COLOR_CODE_REPLACEMENT);
    }

}