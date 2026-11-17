package com.example.quicktrash.network.payloads;

import com.example.quicktrash.QuickTrash;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClearSlotPayload(int containerId, int slotId) implements CustomPacketPayload {
    public static final Type<ClearSlotPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(QuickTrash.MOD_ID, "clear_slot"));

    public static final StreamCodec<ByteBuf, ClearSlotPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClearSlotPayload::containerId,
            ByteBufCodecs.INT, ClearSlotPayload::slotId,
            ClearSlotPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
