package com.snow.nicknamemod.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NicknameStorage {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<UUID, String> nicknames = new HashMap<>();
    private static final String FILE_NAME = "nicknames.json";
    private static File dataFile;

    /**
     * Initialise le stockage en chargeant les surnoms depuis le dossier du monde.
     * @param worldDir Le dossier du monde (serveur ou client singleplayer)
     */
    public static void init(File worldDir) {
        dataFile = new File(worldDir, FILE_NAME);
        loadNicknames();
        LOGGER.info("Nickname storage initialized with {} nicknames", nicknames.size());
    }

    /**
     * Ajoute ou modifie le surnom d'un joueur, puis sauvegarde.
     */
    public static void setNickname(UUID playerUUID, String nickname) {
        nicknames.put(playerUUID, nickname);
        saveNicknames();
    }

    /**
     * Récupère le surnom d'un joueur, ou null si inexistant.
     */
    public static String getNickname(UUID playerUUID) {
        return nicknames.get(playerUUID);
    }

    /**
     * Supprime le surnom d'un joueur, puis sauvegarde.
     */
    public static void clearNickname(UUID playerUUID) {
        nicknames.remove(playerUUID);
        saveNicknames();
    }

    /**
     * Vérifie si un surnom est déjà utilisé par un autre joueur que excludeUUID.
     */
    public static boolean isNicknameUsed(String nickname, UUID excludeUUID) {
        return nicknames.entrySet().stream()
                .anyMatch(entry -> !entry.getKey().equals(excludeUUID) &&
                        entry.getValue().equalsIgnoreCase(nickname));
    }

    /**
     * Retourne une copie de tous les surnoms stockés.
     */
    public static Map<UUID, String> getAllNicknames() {
        return new HashMap<>(nicknames);
    }

    /**
     * Sauvegarde les surnoms dans un fichier JSON.
     */
    private static void saveNicknames() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            // Convertit UUID en String pour sérialisation JSON
            Map<String, String> stringMap = new HashMap<>();
            nicknames.forEach((uuid, name) -> stringMap.put(uuid.toString(), name));

            gson.toJson(stringMap, writer);
            LOGGER.debug("Nicknames saved to file");
        } catch (IOException e) {
            LOGGER.error("Failed to save nicknames", e);
        }
    }

    /**
     * Charge les surnoms depuis le fichier JSON, ignore les UUID invalides.
     */
    private static void loadNicknames() {
        if (!dataFile.exists()) {
            LOGGER.info("No existing nicknames file found, starting fresh");
            return;
        }

        try (FileReader reader = new FileReader(dataFile)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> stringMap = gson.fromJson(reader, type);

            if (stringMap != null) {
                nicknames.clear();
                stringMap.forEach((uuidStr, name) -> {
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        nicknames.put(uuid, name);
                    } catch (IllegalArgumentException e) {
                        LOGGER.warn("Invalid UUID in nicknames file: {}", uuidStr);
                    }
                });
                LOGGER.info("Loaded {} nicknames from file", nicknames.size());
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load nicknames", e);
        }
    }
}
