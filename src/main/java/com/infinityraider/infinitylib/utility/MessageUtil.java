/*
 */
package com.infinityraider.infinitylib.utility;

import java.text.MessageFormat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

/**
 * Utility class for messaging players.
 */
public class MessageUtil {

    public static final void messagePlayer(EntityPlayer player, String format, Object... args) {
        String message;
        try {
            message = MessageFormat.format(format, args);
        } catch (IllegalArgumentException ex) {
            message = "Message Formatting Error: \"" + format + "\"!";
        }
        player.addChatComponentMessage(new TextComponentString(message));
    }

}
