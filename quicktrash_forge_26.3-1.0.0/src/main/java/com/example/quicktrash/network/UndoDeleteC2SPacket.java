package com.example.quicktrash.network;

import com.example.quicktrash.server.UndoManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public final class UndoDeleteC2SPacket {
    public static UndoDeleteC2SPacket decode(FriendlyByteBuf buffer) { return new UndoDeleteC2SPacket(); }
    public void encode(FriendlyByteBuf buffer) {}
    public static void handle(UndoDeleteC2SPacket packet, CustomPayloadEvent.Context context) {
        if (context.getSender() instanceof ServerPlayer player) UndoManager.undo(player);
    }
}
