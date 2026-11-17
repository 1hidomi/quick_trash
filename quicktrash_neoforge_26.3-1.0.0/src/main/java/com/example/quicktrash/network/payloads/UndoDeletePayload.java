package com.example.quicktrash.network.payloads;

import com.example.quicktrash.QuickTrash;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record UndoDeletePayload() implements CustomPacketPayload {
    public static final Type<UndoDeletePayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(QuickTrash.MOD_ID, "undo_delete"));

    public static final StreamCodec<ByteBuf, UndoDeletePayload> STREAM_CODEC = StreamCodec.unit(new UndoDeletePayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
