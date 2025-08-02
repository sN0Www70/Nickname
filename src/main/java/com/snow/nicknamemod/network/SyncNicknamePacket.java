package com.snow.nicknamemod.network;

import com.snow.nicknamemod.storage.ClientNicknameCache;
import com.snow.nicknamemod.storage.NicknameStorage;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;


import java.util.UUID;
import java.util.function.Supplier;

public class SyncNicknamePacket {
    private UUID playerUUID;
    private String nickname;

    public SyncNicknamePacket() {}

    public SyncNicknamePacket(UUID playerUUID, String nickname) {
        this.playerUUID = playerUUID;
        this.nickname = nickname;
    }

    public static void encode(SyncNicknamePacket pkt, PacketBuffer buf) {
        buf.writeUUID(pkt.playerUUID);
        buf.writeUtf(pkt.nickname);
    }

    public static SyncNicknamePacket decode(PacketBuffer buf) {
        UUID uuid = buf.readUUID();
        String name = buf.readUtf(32767);
        return new SyncNicknamePacket(uuid, name);
    }

    public static void handle(SyncNicknamePacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            NetworkEvent.Context context = ctx.get();

            if (context.getDirection().getReceptionSide().isServer()) {
                // Côté serveur
                ServerPlayerEntity sender = context.getSender();
                if (sender != null && sender.getUUID().equals(pkt.playerUUID)) {
                    // Mise à jour du surnom côté serveur
                    NicknameStorage.setNickname(pkt.playerUUID, pkt.nickname);

                    // Broadcast aux autres joueurs (à l'exception de l'envoyeur)
                    NetworkHandler.CHANNEL.send(PacketDistributor.ALL.noArg(), new SyncNicknamePacket(pkt.playerUUID, pkt.nickname));
                }
            } else {
                // Côté client
                ClientNicknameCache.setNickname(pkt.playerUUID, pkt.nickname);
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public String getNickname() {
        return nickname;
    }
}
