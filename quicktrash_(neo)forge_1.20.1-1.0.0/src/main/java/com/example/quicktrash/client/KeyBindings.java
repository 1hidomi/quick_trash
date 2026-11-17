package com.example.quicktrash.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String KEY_CATEGORY = "key.categories.quicktrash";
    public static final String KEY_DELETE = "key.quicktrash.delete";
    public static final String KEY_UNDO = "key.quicktrash.undo";

    // 默认绑定 D 键作为滑动删除键
    public static final KeyMapping DELETE_KEY = new KeyMapping(
            KEY_DELETE,
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_D,
            KEY_CATEGORY
    );

    // 默认绑定 Z 键作为撤回键
    public static final KeyMapping UNDO_KEY = new KeyMapping(
            KEY_UNDO,
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            KEY_CATEGORY
    );
}
