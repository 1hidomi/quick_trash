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

public record ClearSlotC2SPacket(int containerId, int slotId) {
    public static ClearSlotC2SPacket decode(FriendlyByteBuf buffer) { return new ClearSlotC2SPacket(buffer.readInt(), buffer.readInt()); }
    public void encode(FriendlyByteBuf buffer) { buffer.writeInt(containerId); buffer.writeInt(slotId); }

    public static void handle(ClearSlotC2SPacket packet, CustomPayloadEvent.Context context) {
        if (!(context.getSender() instanceof ServerPlayer player)) return;
        AbstractContainerMenu menu = player.containerMenu;
        if (menu.containerId != packet.containerId || packet.slotId < 0 || packet.slotId >= menu.slots.size()) return;
        Slot slot = menu.getSlot(packet.slotId);
        if (!slot.mayPickup(player) || !slot.hasItem()) return;
        ItemStack item = slot.getItem();
        UndoManager.recordDeletion(player, item);
        slot.set(ItemStack.EMPTY);
        menu.broadcastChanges();
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LAVA_EXTINGUISH,
                SoundSource.PLAYERS, 0.4F, 1.8F);
    }
}
