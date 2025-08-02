package com.snow.nicknamemod.storage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientNicknameCache {
    private static final HashMap<UUID, String> nicknames = new HashMap<>();

    /**
     * Ajoute ou modifie le surnom dans le cache client.
     */
    public static void setNickname(UUID uuid, String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            nicknames.remove(uuid);
        } else {
            nicknames.put(uuid, nickname);
        }
    }

    /**
     * Récupère le surnom dans le cache, ou une chaîne vide si aucun surnom.
     */
    public static String getNickname(UUID uuid) {
        return nicknames.getOrDefault(uuid, "");
    }

    /**
     * Supprime un surnom du cache.
     */
    public static void removeNickname(UUID uuid) {
        nicknames.remove(uuid);
    }

    /**
     * Récupère une copie non modifiable de la map complète des surnoms.
     */
    public static Map<UUID, String> getAllNicknames() {
        return Collections.unmodifiableMap(nicknames);
    }

    /**
     * Vide entièrement le cache client (utile si besoin).
     */
    public static void clear() {
        nicknames.clear();
    }
}
