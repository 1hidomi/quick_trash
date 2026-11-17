package com.example.quicktrash.network;

import com.example.quicktrash.server.UndoManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.ArrayList;
import java.util.List;

public record ClearSameItemsC2SPacket(int containerId, int slotId) {
    public static ClearSameItemsC2SPacket decode(FriendlyByteBuf buffer) { return new ClearSameItemsC2SPacket(buffer.readInt(), buffer.readInt()); }
    public void encode(FriendlyByteBuf buffer) { buffer.writeInt(containerId); buffer.writeInt(slotId); }

    public static void handle(ClearSameItemsC2SPacket packet, CustomPayloadEvent.Context context) {
        if (!(context.getSender() instanceof ServerPlayer player)) return;
        AbstractContainerMenu menu = player.containerMenu;
        if (menu.containerId != packet.containerId || packet.slotId < 0 || packet.slotId >= menu.slots.size()) return;
        Slot targetSlot = menu.getSlot(packet.slotId);
        if (!targetSlot.mayPickup(player) || !targetSlot.hasItem()) return;
        ItemStack target = targetSlot.getItem();
        List<ItemStack> deleted = new ArrayList<>();
        for (Slot slot : menu.slots) {
            if (slot.mayPickup(player) && slot.hasItem() && ItemStack.matches(slot.getItem(), target)) {
                deleted.add(slot.getItem().copy());
                slot.set(ItemStack.EMPTY);
            }
        }
        if (deleted.isEmpty()) return;
        UndoManager.recordDeletionBatch(player, deleted);
        menu.broadcastChanges();
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LAVA_EXTINGUISH,
                SoundSource.PLAYERS, 0.6F, 1.4F);
    }
}
