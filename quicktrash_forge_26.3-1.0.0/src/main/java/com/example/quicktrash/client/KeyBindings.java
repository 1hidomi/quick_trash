package com.example.quicktrash.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;

public final class KeyBindings {
    public static final String KEY_CATEGORY = "key.categories.quicktrash";
    public static final String KEY_DELETE = "key.quicktrash.delete";
    public static final String KEY_UNDO = "key.quicktrash.undo";

    public static final KeyMapping DELETE_KEY = new KeyMapping(KEY_DELETE, KeyConflictContext.GUI,
            InputConstants.Type.KEYBOARD.getOrCreate(InputConstants.KEY_D), KeyMapping.Category.INVENTORY, 0);
    public static final KeyMapping UNDO_KEY = new KeyMapping(KEY_UNDO, KeyConflictContext.GUI,
            InputConstants.Type.KEYBOARD.getOrCreate(InputConstants.KEY_Z), KeyMapping.Category.INVENTORY, 0);

    private KeyBindings() {}
}
