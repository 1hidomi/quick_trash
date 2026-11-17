package com.example.quicktrash.client;

import com.example.quicktrash.network.ClearSameItemsC2SPacket;
import com.example.quicktrash.network.ClearSlotC2SPacket;
import com.example.quicktrash.network.ModMessages;
import com.example.quicktrash.network.UndoDeleteC2SPacket;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.client.event.ScreenEvent;

import java.lang.reflect.Field;

/** Client-only inventory gestures. Server-side validation remains authoritative. */
public final class InventoryDeleteHandler {
    private static int lastDeletedSlotId = -1;
    private static final Field LEFT_POS = findContainerField("leftPos");
    private static final Field TOP_POS = findContainerField("topPos");

    public static boolean onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen) || isTyping(screen)) return false;

        if (KeyBindings.UNDO_KEY.matches(event.getInfo())) {
            ModMessages.sendToServer(new UndoDeleteC2SPacket());
            return true;
        }
        if (!KeyBindings.DELETE_KEY.matches(event.getInfo())) return false;

        Slot slot = getSlotUnderMouse(screen, scaledMouseX(), scaledMouseY());
        if (slot == null || !slot.hasItem()) return false;

        if (Minecraft.getInstance().hasShiftDown()) {
            ModMessages.sendToServer(new ClearSameItemsC2SPacket(screen.getMenu().containerId, slot.index));
            playDestroySound(0.45F, 1.4F);
        } else {
            deleteSlot(screen, slot);
        }
        return true;
    }

    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof AbstractContainerScreen<?> screen) || isTyping(screen) || Minecraft.getInstance().hasShiftDown()) {
            lastDeletedSlotId = -1;
            return;
        }
        int key = KeyBindings.DELETE_KEY.getKey().getValue();
        if (!InputConstants.isKeyDown(key)) {
            lastDeletedSlotId = -1;
            return;
        }
        Slot slot = getSlotUnderMouse(screen, event.getMouseX(), event.getMouseY());
        if (slot != null && slot.hasItem() && slot.index != lastDeletedSlotId) deleteSlot(screen, slot);
        else if (slot == null) lastDeletedSlotId = -1;
    }

    private static void deleteSlot(AbstractContainerScreen<?> screen, Slot slot) {
        lastDeletedSlotId = slot.index;
        ModMessages.sendToServer(new ClearSlotC2SPacket(screen.getMenu().containerId, slot.index));
        playDestroySound(0.35F, 1.9F);
    }

    private static void playDestroySound(float volume, float pitch) {
        if (Minecraft.getInstance().player != null)
            Minecraft.getInstance().player.playSound(SoundEvents.LAVA_EXTINGUISH, volume, pitch);
    }

    private static boolean isTyping(Screen screen) {
        return screen.getFocused() instanceof EditBox editBox && editBox.isFocused();
    }

    private static double scaledMouseX() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth() / minecraft.getWindow().getScreenWidth();
    }

    private static double scaledMouseY() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight() / minecraft.getWindow().getScreenHeight();
    }

    private static Slot getSlotUnderMouse(AbstractContainerScreen<?> screen, double mouseX, double mouseY) {
        int leftPos = getContainerPosition(LEFT_POS, screen);
        int topPos = getContainerPosition(TOP_POS, screen);
        for (Slot slot : screen.getMenu().slots) {
            if (slot.isActive() && mouseX >= leftPos + slot.x && mouseX < leftPos + slot.x + 16
                    && mouseY >= topPos + slot.y && mouseY < topPos + slot.y + 16) return slot;
        }
        return null;
    }

    private static Field findContainerField(String name) {
        try {
            Field field = AbstractContainerScreen.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Quick Trash could not access container screen geometry", exception);
        }
    }

    private static int getContainerPosition(Field field, AbstractContainerScreen<?> screen) {
        try {
            return field.getInt(screen);
        } catch (IllegalAccessException exception) {
            throw new IllegalStateException("Quick Trash could not read container screen geometry", exception);
        }
    }

    private InventoryDeleteHandler() {}
}
