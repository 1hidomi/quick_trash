package com.example.quicktrash.network;

import com.example.quicktrash.network.payloads.ClearSameItemsPayload;
import com.example.quicktrash.network.payloads.ClearSlotPayload;
import com.example.quicktrash.network.payloads.UndoDeletePayload;
import com.example.quicktrash.server.UndoManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public class ServerPayloadHandler {

    public static void handleClearSlot(ClearSlotPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            AbstractContainerMenu menu = player.containerMenu;
            if (menu == null || menu.containerId != payload.containerId()) {
                return;
            }

            if (payload.slotId() < 0 || payload.slotId() >= menu.slots.size()) {
                return;
            }

            Slot slot = menu.getSlot(payload.slotId());
            if (!slot.mayPickup(player)) {
                return;
            }

            ItemStack item = slot.getItem();
            if (!item.isEmpty()) {
                UndoManager.recordDeletion(player, item);
                slot.set(ItemStack.EMPTY);
                menu.broadcastChanges();

                player.level().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.LAVA_EXTINGUISH,
                        SoundSource.PLAYERS,
                        0.4F,
                        1.8F
                );
            }
        });
    }

    public static void handleClearSameItems(ClearSameItemsPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }

            AbstractContainerMenu menu = player.containerMenu;
            if (menu == null || menu.containerId != payload.containerId()) {
                return;
            }

            if (payload.slotId() < 0 || payload.slotId() >= menu.slots.size()) {
                return;
            }

            Slot targetSlot = menu.getSlot(payload.slotId());
            if (!targetSlot.mayPickup(player)) {
                return;
            }

            ItemStack targetItem = targetSlot.getItem();
            if (targetItem.isEmpty()) {
                return;
            }

            List<ItemStack> deletedItems = new ArrayList<>();

            for (Slot slot : menu.slots) {
                if (slot.mayPickup(player) && slot.hasItem()) {
                    ItemStack slotItem = slot.getItem();
                    // 现代 Minecraft 组件与类型完全匹配检查
                    if (ItemStack.matches(slotItem, targetItem)) {
                        deletedItems.add(slotItem.copy());
                        slot.set(ItemStack.EMPTY);
                    }
                }
            }

            if (!deletedItems.isEmpty()) {
                UndoManager.recordDeletionBatch(player, deletedItems);
                menu.broadcastChanges();

                player.level().playSound(
                        null,
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        SoundEvents.LAVA_EXTINGUISH,
                        SoundSource.PLAYERS,
                        0.6F,
                        1.4F
                );
            }
        });
    }

    public static void handleUndo(UndoDeletePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                UndoManager.undo(player);
            }
        });
    }
}
