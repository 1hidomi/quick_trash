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

public final class UndoManager {
    private static final int MAX_HISTORY = 64;
    private static final Map<UUID, Deque<List<ItemStack>>> HISTORY = new ConcurrentHashMap<>();

    public static void recordDeletion(ServerPlayer player, ItemStack stack) {
        if (stack != null && !stack.isEmpty()) recordDeletionBatch(player, List.of(stack.copy()));
    }

    public static void recordDeletionBatch(ServerPlayer player, List<ItemStack> stacks) {
        List<ItemStack> copies = stacks.stream().filter(stack -> !stack.isEmpty()).map(ItemStack::copy).toList();
        if (copies.isEmpty()) return;
        Deque<List<ItemStack>> history = HISTORY.computeIfAbsent(player.getUUID(), ignored -> new ArrayDeque<>());
        if (history.size() >= MAX_HISTORY) history.removeLast();
        history.push(copies);
    }

    public static boolean undo(ServerPlayer player) {
        Deque<List<ItemStack>> history = HISTORY.get(player.getUUID());
        if (history == null || history.isEmpty()) {
            player.sendSystemMessage(Component.translatable("message.quicktrash.undo_empty"), true);
            return false;
        }
        List<ItemStack> batch = history.pop();
        int restoredCount = 0;
        Component sampleName = batch.getFirst().getHoverName();
        for (ItemStack stack : batch) {
            restoredCount += stack.getCount();
            player.getInventory().add(stack);
            if (!stack.isEmpty()) player.spawnAtLocation((ServerLevel) player.level(), stack);
        }
        player.containerMenu.broadcastChanges();
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP,
                SoundSource.PLAYERS, 0.6F, 1.2F);
        player.sendSystemMessage(Component.translatable("message.quicktrash.undo_success_count", sampleName, restoredCount), true);
        return true;
    }

    public static void clearPlayer(UUID playerId) { HISTORY.remove(playerId); }
    private UndoManager() {}
}
