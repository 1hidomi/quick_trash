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
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InventoryDeleteHandler {
    private static int lastDeletedSlotId = -1;

    @SubscribeEvent
    public static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }

        // 如果用户正在输入框（如JEI/搜索框）打字，不响应快捷键
        if (isTyping(containerScreen)) {
            return;
        }

        // 检查撤回按键 (默认 Z)
        if (KeyBindings.UNDO_KEY.matches(event.getKeyCode(), event.getScanCode())) {
            ModMessages.sendToServer(new UndoDeleteC2SPacket());
            event.setCanceled(true);
            return;
        }

        // 检查删除按键 (默认 D)
        if (KeyBindings.DELETE_KEY.matches(event.getKeyCode(), event.getScanCode())) {
            double mouseX = Minecraft.getInstance().mouseHandler.xpos() * (double) Minecraft.getInstance().getWindow().getGuiScaledWidth() / (double) Minecraft.getInstance().getWindow().getScreenWidth();
            double mouseY = Minecraft.getInstance().mouseHandler.ypos() * (double) Minecraft.getInstance().getWindow().getGuiScaledHeight() / (double) Minecraft.getInstance().getWindow().getScreenHeight();
            Slot slot = getSlotUnderMouse(containerScreen, mouseX, mouseY);

            if (slot != null && slot.hasItem()) {
                if (Screen.hasShiftDown()) {
                    // Shift + D: 一键删除当前容器中的所有同类物品
                    ModMessages.sendToServer(new ClearSameItemsC2SPacket(containerScreen.getMenu().containerId, slot.index));
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.playSound(SoundEvents.LAVA_EXTINGUISH, 0.45F, 1.4F);
                    }
                } else {
                    // 普通 D 键: 单格删除
                    deleteSlot(containerScreen, slot);
                }
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            lastDeletedSlotId = -1;
            return;
        }

        if (isTyping(containerScreen) || Screen.hasShiftDown()) {
            lastDeletedSlotId = -1;
            return;
        }

        long windowHandle = Minecraft.getInstance().getWindow().getWindow();
        int keyCode = KeyBindings.DELETE_KEY.getKey().getValue();

        // 检测删除按键是否处于长按状态（未按 Shift 时的滑动删除）
        boolean isDeleteDown = keyCode > 0 && InputConstants.isKeyDown(windowHandle, keyCode);

        if (isDeleteDown) {
            Slot hoveredSlot = getSlotUnderMouse(containerScreen, event.getMouseX(), event.getMouseY());
            if (hoveredSlot != null && hoveredSlot.hasItem()) {
                if (hoveredSlot.index != lastDeletedSlotId) {
                    deleteSlot(containerScreen, hoveredSlot);
                }
            } else if (hoveredSlot == null) {
                lastDeletedSlotId = -1;
            }
        } else {
            lastDeletedSlotId = -1;
        }
    }

    private static void deleteSlot(AbstractContainerScreen<?> screen, Slot slot) {
        lastDeletedSlotId = slot.index;

        // 发送删除数据包到服务端
        ModMessages.sendToServer(new ClearSlotC2SPacket(screen.getMenu().containerId, slot.index));

        // 客户端本地音效即时反馈
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.playSound(SoundEvents.LAVA_EXTINGUISH, 0.35F, 1.9F);
        }
    }

    private static boolean isTyping(Screen screen) {
        if (screen.getFocused() instanceof EditBox editBox) {
            return editBox.isFocused();
        }
        return false;
    }

    private static Slot getSlotUnderMouse(AbstractContainerScreen<?> screen, double mouseX, double mouseY) {
        int left = screen.getGuiLeft();
        int top = screen.getGuiTop();

        for (Slot slot : screen.getMenu().slots) {
            if (slot.isActive()) {
                int slotX = left + slot.x;
                int slotY = top + slot.y;
                if (mouseX >= slotX && mouseX < slotX + 16 && mouseY >= slotY && mouseY < slotY + 16) {
                    return slot;
                }
            }
        }
        return null;
    }
}
