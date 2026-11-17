package com.example.quicktrash.network;

import com.example.quicktrash.QuickTrash;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public final class ModMessages {
    private static final SimpleChannel CHANNEL = ChannelBuilder.named(Identifier.fromNamespaceAndPath(QuickTrash.MOD_ID, "main"))
            .networkProtocolVersion(1)
            .simpleChannel();
    private static boolean registered;

    public static void register() {
        if (registered) return;
        registered = true;
        CHANNEL.messageBuilder(ClearSlotC2SPacket.class, 0)
                .encoder(ClearSlotC2SPacket::encode).decoder(ClearSlotC2SPacket::decode)
                .consumerMainThread(ClearSlotC2SPacket::handle).add();
        CHANNEL.messageBuilder(ClearSameItemsC2SPacket.class, 1)
                .encoder(ClearSameItemsC2SPacket::encode).decoder(ClearSameItemsC2SPacket::decode)
                .consumerMainThread(ClearSameItemsC2SPacket::handle).add();
        CHANNEL.messageBuilder(UndoDeleteC2SPacket.class, 2)
                .encoder(UndoDeleteC2SPacket::encode).decoder(UndoDeleteC2SPacket::decode)
                .consumerMainThread(UndoDeleteC2SPacket::handle).add();
        CHANNEL.build();
    }

    public static void sendToServer(Object message) {
        CHANNEL.send(message, PacketDistributor.SERVER.noArg());
    }

    private ModMessages() {}
}
