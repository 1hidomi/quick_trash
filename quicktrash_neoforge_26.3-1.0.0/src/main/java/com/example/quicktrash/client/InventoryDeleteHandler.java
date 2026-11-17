package com.example.quicktrash.client;

import com.example.quicktrash.network.payloads.ClearSameItemsPayload;
import com.example.quicktrash.network.payloads.ClearSlotPayload;
import com.example.quicktrash.network.payloads.UndoDeletePayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.Slot;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class InventoryDeleteHandler {
    private static int lastDeletedSlotId = -1;

    @SubscribeEvent
    public static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        Screen screen = event.getScreen();
        if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) {
            return;
        }

        if (isTyping(containerScreen)) {
            return;
        }

        // 检查撤回按键 (Z)
        if (KeyBindings.UNDO_KEY.matches(event.getKeyEvent())) {
            ClientPacketDistributor.sendToServer(new UndoDeletePayload());
            event.setCanceled(true);
            return;
        }

        // 检查删除按键 (D)
        if (KeyBindings.DELETE_KEY.matches(event.getKeyEvent())) {
            Slot slot = containerScreen.getHoveredSlot();

            if (slot != null && slot.hasItem()) {
                if (event.getKeyEvent().hasShiftDown()) {
                    // Shift + D: 批量删除同类物品
                    ClientPacketDistributor.sendToServer(new ClearSameItemsPayload(containerScreen.getMenu().containerId, slot.index));
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.playSound(SoundEvents.LAVA_EXTINGUISH, 0.45F, 1.4F);
                    }
                } else {
                    // 普通 D: 单格删除
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

        if (isTyping(containerScreen)) {
            lastDeletedSlotId = -1;
            return;
        }

        int keyCode = KeyBindings.DELETE_KEY.getKey().getValue();
        boolean isDeleteDown = keyCode > 0 && InputConstants.isKeyDown(keyCode);

        if (isDeleteDown) {
            Slot hoveredSlot = containerScreen.getHoveredSlot();
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
        ClientPacketDistributor.sendToServer(new ClearSlotPayload(screen.getMenu().containerId, slot.index));

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
}
