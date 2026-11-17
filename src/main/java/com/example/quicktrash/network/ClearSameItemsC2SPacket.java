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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ClearSameItemsC2SPacket {
    private final int containerId;
    private final int slotId;

    public ClearSameItemsC2SPacket(int containerId, int slotId) {
        this.containerId = containerId;
        this.slotId = slotId;
    }

    public ClearSameItemsC2SPacket(FriendlyByteBuf buf) {
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

            Slot targetSlot = menu.getSlot(this.slotId);
            if (!targetSlot.mayPickup(player)) {
                return;
            }

            ItemStack targetItem = targetSlot.getItem();
            if (targetItem.isEmpty()) {
                return;
            }

            List<ItemStack> deletedItems = new ArrayList<>();

            // 遍历所有槽位，清除所有同类物品（相同物品类型且相同NBT标签）
            for (Slot slot : menu.slots) {
                if (slot.mayPickup(player) && slot.hasItem()) {
                    ItemStack slotItem = slot.getItem();
                    if (ItemStack.isSameItemSameTags(slotItem, targetItem)) {
                        deletedItems.add(slotItem.copy());
                        slot.set(ItemStack.EMPTY);
                    }
                }
            }

            if (!deletedItems.isEmpty()) {
                // 批量记录到撤回历史（按一次Z即可全部恢复）
                UndoManager.recordDeletionBatch(player, deletedItems);
                menu.broadcastChanges();

                // 播放销毁音效
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
        context.setPacketHandled(true);
    }
}
