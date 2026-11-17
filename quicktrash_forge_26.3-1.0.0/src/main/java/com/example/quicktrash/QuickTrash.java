package com.example.quicktrash;

import com.example.quicktrash.client.InventoryDeleteHandler;
import com.example.quicktrash.client.KeyBindings;
import com.example.quicktrash.network.ModMessages;
import com.example.quicktrash.server.UndoManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(QuickTrash.MOD_ID)
public final class QuickTrash {
    public static final String MOD_ID = "quicktrash";

    public QuickTrash() {
        ModMessages.register();
        PlayerEvent.PlayerLoggedOutEvent.BUS.addListener(event -> UndoManager.clearPlayer(event.getEntity().getUUID()));
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientModEvents.register();
        }
    }

    public static final class ClientModEvents {
        private static void register() {
            net.minecraftforge.client.event.ScreenEvent.KeyPressed.Pre.BUS.addListener(
                    (java.util.function.Predicate<net.minecraftforge.client.event.ScreenEvent.KeyPressed.Pre>) InventoryDeleteHandler::onScreenKeyPressed);
            net.minecraftforge.client.event.ScreenEvent.Render.Post.BUS.addListener(InventoryDeleteHandler::onScreenRenderPost);
            RegisterKeyMappingsEvent.BUS.addListener(ClientModEvents::registerKeys);
        }

        private static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(KeyBindings.DELETE_KEY);
            event.register(KeyBindings.UNDO_KEY);
        }
    }
}
