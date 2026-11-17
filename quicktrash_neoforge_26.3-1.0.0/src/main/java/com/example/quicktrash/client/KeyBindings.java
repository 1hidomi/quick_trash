package com.example.quicktrash.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class KeyBindings {
    public static final String KEY_DELETE = "key.quicktrash.delete";
    public static final String KEY_UNDO = "key.quicktrash.undo";

    public static final KeyMapping DELETE_KEY = new KeyMapping(
            KEY_DELETE,
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_D,
            KeyMapping.Category.INVENTORY
    );

    public static final KeyMapping UNDO_KEY = new KeyMapping(
            KEY_UNDO,
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_Z,
            KeyMapping.Category.INVENTORY
    );
}
