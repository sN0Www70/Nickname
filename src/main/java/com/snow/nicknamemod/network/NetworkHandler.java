package com.snow.nicknamemod.network;

import com.snow.nicknamemod.NicknameMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.fml.network.PacketDistributor;


import java.util.UUID;
import java.util.function.Supplier;

public class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(NicknameMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(
                packetId++,
                SyncNicknamePacket.class,
                SyncNicknamePacket::encode,
                SyncNicknamePacket::decode,
                SyncNicknamePacket::handle
        );
    }

    /** Envoie une mise à jour de surnom du joueur au serveur */
    public static void sendToServer(UUID playerUUID, String nickname) {
        CHANNEL.sendToServer(new SyncNicknamePacket(playerUUID, nickname));
    }

    /** Envoie une mise à jour de surnom à tous les clients */
    public static void sendToAll(UUID playerUUID, String nickname) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncNicknamePacket(playerUUID, nickname));
    }
}
