package com.example.quicktrash;

import com.example.quicktrash.client.InventoryDeleteHandler;
import com.example.quicktrash.client.KeyBindings;
import com.example.quicktrash.network.ServerPayloadHandler;
import com.example.quicktrash.network.payloads.ClearSameItemsPayload;
import com.example.quicktrash.network.payloads.ClearSlotPayload;
import com.example.quicktrash.network.payloads.UndoDeletePayload;
import com.example.quicktrash.server.UndoManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(QuickTrash.MOD_ID)
public class QuickTrash {
    public static final String MOD_ID = "quicktrash";

    public QuickTrash(IEventBus modEventBus, Dist dist) {
        modEventBus.addListener(this::registerPayloads);

        if (dist.isClient()) {
            modEventBus.addListener(this::onClientSetup);
            modEventBus.addListener(this::registerKeys);
        }

        NeoForge.EVENT_BUS.register(this);
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                ClearSlotPayload.TYPE,
                ClearSlotPayload.STREAM_CODEC,
                ServerPayloadHandler::handleClearSlot
        );

        registrar.playToServer(
                ClearSameItemsPayload.TYPE,
                ClearSameItemsPayload.STREAM_CODEC,
                ServerPayloadHandler::handleClearSameItems
        );

        registrar.playToServer(
                UndoDeletePayload.TYPE,
                UndoDeletePayload.STREAM_CODEC,
                ServerPayloadHandler::handleUndo
        );
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(InventoryDeleteHandler.class);
    }

    private void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.DELETE_KEY);
        event.register(KeyBindings.UNDO_KEY);
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() != null) {
            UndoManager.clearPlayer(event.getEntity().getUUID());
        }
    }
}
