package com.example.quicktrash.server;

import net.minecraft.network.chat.Component;
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
    // 每一个历史步骤记录一个列表（支持单个物品与 Shift+D 批量同类物品）
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
            player.displayClientMessage(Component.translatable("message.quicktrash.undo_empty"), true);
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
            // 尝试添加回玩家背包，若有剩余未放下的则掉落在玩家脚下
            player.getInventory().add(restored);
            if (!restored.isEmpty()) {
                player.drop(restored, false);
            }
        }

        // 同步界面容器
        player.containerMenu.broadcastChanges();

        // 播放拾取音效
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

        player.displayClientMessage(
                Component.translatable("message.quicktrash.undo_success_count", sampleName, totalCount),
                true
        );
        return true;
    }

    public static void clearPlayer(UUID uuid) {
        UNDO_HISTORY.remove(uuid);
    }
}
