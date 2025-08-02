package com.snow.nicknamemod;

import com.snow.nicknamemod.client.NicknameRenderHandler;
import com.snow.nicknamemod.commands.NicknameCommand;
import com.snow.nicknamemod.events.NicknameEvents;
import com.snow.nicknamemod.network.NetworkHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NicknameMod.MODID)
public class NicknameMod {
    public static final String MODID = "nicknamemod";
    private static final Logger LOGGER = LogManager.getLogger();

    public NicknameMod() {
        // Enregistre les listeners généraux (événements, network)
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new NicknameEvents());

        // Enregistrement du réseau (paquets)
        NetworkHandler.register();

        // Enregistre le setup client (pour le rendu etc)
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        LOGGER.info("Nickname Mod initialized!");
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // Inscrit le render handler sur l’event bus client
        MinecraftForge.EVENT_BUS.register(NicknameRenderHandler.class);
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // Enregistre les commandes dès que le serveur démarre
        NicknameCommand.register(event.getServer().getCommands().getDispatcher());
        LOGGER.info("Nickname commands registered!");
    }
}
