package com.example.quicktrash.network.payloads;

import com.example.quicktrash.QuickTrash;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ClearSameItemsPayload(int containerId, int slotId) implements CustomPacketPayload {
    public static final Type<ClearSameItemsPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(QuickTrash.MOD_ID, "clear_same_items"));

    public static final StreamCodec<ByteBuf, ClearSameItemsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClearSameItemsPayload::containerId,
            ByteBufCodecs.INT, ClearSameItemsPayload::slotId,
            ClearSameItemsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
