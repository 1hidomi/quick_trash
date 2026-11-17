package com.example.quicktrash.server;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UndoManager {
    private static final int MAX_HISTORY = 64;
    private static final Map<UUID, Deque<List<ItemStack>>> UNDO_HISTORY = new ConcurrentHashMap<>();

    public static void recordDeletion(ServerPlayer player, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        List<ItemStack> list = new ArrayList<>();
        list.add(stack.copy());
        recordDeletionBatch(player, list);
    }

    public static void recordDeletionBatch(ServerPlayer player, List<ItemStack> stacks) {
        if (stacks == null || stacks.isEmpty()) {
            return;
        }

        Deque<List<ItemStack>> history = UNDO_HISTORY.computeIfAbsent(player.getUUID(), k -> new ArrayDeque<>());
        if (history.size() >= MAX_HISTORY) {
            history.removeLast();
        }

        List<ItemStack> copies = new ArrayList<>();
        for (ItemStack s : stacks) {
            if (!s.isEmpty()) {
                copies.add(s.copy());
            }
        }

        if (!copies.isEmpty()) {
            history.push(copies);
        }
    }

    public static boolean undo(ServerPlayer player) {
        Deque<List<ItemStack>> history = UNDO_HISTORY.get(player.getUUID());
        if (history == null || history.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.quicktrash.undo_empty"), true);
            return false;
        }

        List<ItemStack> batch = history.pop();
        if (batch.isEmpty()) {
            return false;
        }

        int totalCount = 0;
        Component sampleName = batch.get(0).getHoverName();

        for (ItemStack restored : batch) {
            totalCount += restored.getCount();
            player.getInventory().add(restored);
            if (!restored.isEmpty()) {
                player.spawnAtLocation((ServerLevel) player.level(), restored);
            }
        }

        player.containerMenu.broadcastChanges();

        player.level().playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.ITEM_PICKUP,
                SoundSource.PLAYERS,
                0.6F,
                1.2F
        );

        player.sendSystemMessage(
                Component.translatable("message.quicktrash.undo_success_count", sampleName, totalCount),
                true
        );
        return true;
    }

    public static void clearPlayer(UUID uuid) {
        UNDO_HISTORY.remove(uuid);
    }
}
