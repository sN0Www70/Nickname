package com.snow.nicknamemod.events;

import com.snow.nicknamemod.network.NetworkHandler;
import com.snow.nicknamemod.network.SyncNicknamePacket;
import com.snow.nicknamemod.storage.NicknameStorage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.network.PacketDistributor;


import java.io.File;

public class NicknameEvents {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            String nickname = NicknameStorage.getNickname(player.getUUID());

            if (nickname != null && !nickname.isEmpty()) {
                // Appliquer le surnom sauvegardé
                player.setCustomName(new StringTextComponent(nickname));
                player.setCustomNameVisible(true);
                LOGGER.info("Applied saved nickname '{}' to player {}", nickname, player.getGameProfile().getName());

                // Envoyer le surnom aux clients (sync)
                NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncNicknamePacket(player.getUUID(), nickname));
            }
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isClientSide() && event.getWorld() instanceof ServerWorld) {
            ServerWorld world = (ServerWorld) event.getWorld();
            if (world.dimension() == World.OVERWORLD) {
                // Récupération du dossier monde côté serveur
                File worldDir = world.getServer().getServerDirectory();
                NicknameStorage.init(worldDir);
                LOGGER.info("Nickname storage initialized for world: {}", worldDir.getAbsolutePath());
            }
        }
    }

    @SubscribeEvent
    public void onChatMessage(ServerChatEvent event) {
        ServerPlayerEntity player = event.getPlayer();
        String nickname = NicknameStorage.getNickname(player.getUUID());

        if (nickname != null && !nickname.isEmpty()) {
            // Remplacer le message avec le surnom dans le chat
            String originalMessage = event.getMessage();
            ITextComponent newComponent = new StringTextComponent("<" + nickname + "> " + originalMessage);
            event.setComponent(newComponent);
        }
    }
}
