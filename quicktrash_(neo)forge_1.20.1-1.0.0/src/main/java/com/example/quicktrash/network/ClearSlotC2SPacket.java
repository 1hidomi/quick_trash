package com.example.quicktrash.network;

import com.example.quicktrash.server.UndoManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClearSlotC2SPacket {
    private final int containerId;
    private final int slotId;

    public ClearSlotC2SPacket(int containerId, int slotId) {
        this.containerId = containerId;
        this.slotId = slotId;
    }

    public ClearSlotC2SPacket(FriendlyByteBuf buf) {
        this.containerId = buf.readInt();
        this.slotId = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.containerId);
        buf.writeInt(this.slotId);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }

            AbstractContainerMenu menu = player.containerMenu;
            if (menu == null || menu.containerId != this.containerId) {
                return;
            }

            if (this.slotId < 0 || this.slotId >= menu.slots.size()) {
                return;
            }

            Slot slot = menu.getSlot(this.slotId);
            if (!slot.mayPickup(player)) {
                return;
            }

            ItemStack item = slot.getItem();
            if (!item.isEmpty()) {
                // 记录进撤回历史
                UndoManager.recordDeletion(player, item);

                // 清空该格子
                slot.set(ItemStack.EMPTY);
                menu.broadcastChanges();

                // 播放销毁音效
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
        context.setPacketHandled(true);
    }
}
