package com.snow.nicknamemod.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientNicknameStorage {
    private static final Map<UUID, String> nicknames = new HashMap<>();

    public static void setNicknames(Map<UUID, String> newMap) {
        nicknames.clear();
        nicknames.putAll(newMap);
    }

    public static void setNickname(UUID uuid, String nickname) {
        nicknames.put(uuid, nickname);
    }

    public static void removeNickname(UUID uuid) {
        nicknames.remove(uuid);
    }

    public static String getNickname(UUID uuid) {
        return nicknames.getOrDefault(uuid, "");
    }
}
