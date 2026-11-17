package com.example.quicktrash.network;

import com.example.quicktrash.QuickTrash;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(QuickTrash.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        INSTANCE.messageBuilder(ClearSlotC2SPacket.class, packetId++)
                .encoder(ClearSlotC2SPacket::toBytes)
                .decoder(ClearSlotC2SPacket::new)
                .consumerMainThread(ClearSlotC2SPacket::handle)
                .add();

        INSTANCE.messageBuilder(UndoDeleteC2SPacket.class, packetId++)
                .encoder(UndoDeleteC2SPacket::toBytes)
                .decoder(UndoDeleteC2SPacket::new)
                .consumerMainThread(UndoDeleteC2SPacket::handle)
                .add();

        INSTANCE.messageBuilder(ClearSameItemsC2SPacket.class, packetId++)
                .encoder(ClearSameItemsC2SPacket::toBytes)
                .decoder(ClearSameItemsC2SPacket::new)
                .consumerMainThread(ClearSameItemsC2SPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
}
