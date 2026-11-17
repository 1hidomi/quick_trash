package com.example.quicktrash;

import com.example.quicktrash.client.InventoryDeleteHandler;
import com.example.quicktrash.client.KeyBindings;
import com.example.quicktrash.network.ModMessages;
import com.example.quicktrash.server.UndoManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(QuickTrash.MOD_ID)
public class QuickTrash {
    public static final String MOD_ID = "quicktrash";

    public QuickTrash() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册模组通用启动事件
        modEventBus.addListener(this::commonSetup);

        // 注册到全局 Forge 事件总线
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() != null) {
            UndoManager.clearPlayer(event.getEntity().getUUID());
        }
    }

    // 客户端专用注册
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MinecraftForge.EVENT_BUS.register(InventoryDeleteHandler.class);
        }

        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            event.register(KeyBindings.DELETE_KEY);
            event.register(KeyBindings.UNDO_KEY);
        }
    }
}
