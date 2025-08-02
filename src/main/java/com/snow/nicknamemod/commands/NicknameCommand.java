package com.snow.nicknamemod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.snow.nicknamemod.storage.NicknameStorage;
import com.snow.nicknamemod.network.NetworkHandler;
import com.snow.nicknamemod.network.SyncNicknamePacket;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.Map;

public class NicknameCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("nickname")
                .then(Commands.argument("name", StringArgumentType.greedyString())
                        .executes(ctx -> setNickname(ctx)))
                .executes(ctx -> clearNickname(ctx))
        );

        dispatcher.register(Commands.literal("nicknameadmin")
                .requires(source -> source.hasPermission(2)) // Niveau op requis
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.argument("name", StringArgumentType.greedyString())
                                .executes(ctx -> setNicknameAdmin(ctx)))
                        .executes(ctx -> clearNicknameAdmin(ctx)))
        );

        dispatcher.register(Commands.literal("nicknamelist")
                .requires(source -> source.hasPermission(2))
                .executes(ctx -> listNicknames(ctx))
        );
    }

    private static int setNickname(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrException();
        String nickname = StringArgumentType.getString(context, "name");

        return setPlayerNickname(source, player, nickname, false);
    }

    private static int clearNickname(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrException();

        return clearPlayerNickname(source, player, false);
    }

    private static int setNicknameAdmin(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        ServerPlayerEntity targetPlayer = EntityArgument.getPlayer(context, "player");
        String nickname = StringArgumentType.getString(context, "name");

        return setPlayerNickname(source, targetPlayer, nickname, true);
    }

    private static int clearNicknameAdmin(CommandContext<CommandSource> context) throws CommandSyntaxException {
        CommandSource source = context.getSource();
        ServerPlayerEntity targetPlayer = EntityArgument.getPlayer(context, "player");

        return clearPlayerNickname(source, targetPlayer, true);
    }

    private static int listNicknames(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();

        Map<java.util.UUID, String> nicknames = NicknameStorage.getAllNicknames();
        if (nicknames.isEmpty()) {
            source.sendSuccess(new StringTextComponent("§eAucun surnom enregistré."), false);
            return 1;
        }

        source.sendSuccess(new StringTextComponent("§6=== Liste des surnoms ==="), false);
        nicknames.forEach((uuid, nickname) -> {
            ServerPlayerEntity player = source.getServer().getPlayerList().getPlayer(uuid);
            String playerName = player != null ? player.getGameProfile().getName() : "Joueur déconnecté";
            source.sendSuccess(new StringTextComponent("§f" + playerName + " §7-> §a" + nickname), false);
        });

        return 1;
    }

    private static int setPlayerNickname(CommandSource source, ServerPlayerEntity player, String nickname, boolean isAdmin) {
        if (nickname.length() > 16) {
            source.sendFailure(new StringTextComponent("§cLe surnom ne peut pas dépasser 16 caractères!"));
            return 0;
        }

        if (!nickname.matches("^[a-zA-Z0-9_§]+$")) {
            source.sendFailure(new StringTextComponent("§cLe surnom ne peut contenir que des lettres, chiffres, underscores et codes couleur!"));
            return 0;
        }

        if (NicknameStorage.isNicknameUsed(nickname, player.getUUID())) {
            source.sendFailure(new StringTextComponent("§cCe surnom est déjà utilisé par un autre joueur!"));
            return 0;
        }

        NicknameStorage.setNickname(player.getUUID(), nickname);
        updatePlayerDisplayName(player, nickname);

        if (isAdmin) {
            source.sendSuccess(new StringTextComponent("§aSurnom de §f" + player.getGameProfile().getName() + " §adéfini sur: §f" + nickname), false);
            player.sendMessage(new StringTextComponent("§aVotre surnom a été défini sur: §f" + nickname + " §apar un administrateur"), player.getUUID());
        } else {
            source.sendSuccess(new StringTextComponent("§aSurnom défini sur: §f" + nickname), false);
        }

        return 1;
    }

    private static int clearPlayerNickname(CommandSource source, ServerPlayerEntity player, boolean isAdmin) {
        String currentNickname = NicknameStorage.getNickname(player.getUUID());

        if (currentNickname == null) {
            if (isAdmin) {
                source.sendFailure(new StringTextComponent("§c" + player.getGameProfile().getName() + " n'a pas de surnom!"));
            } else {
                source.sendFailure(new StringTextComponent("§cVous n'avez pas de surnom!"));
            }
            return 0;
        }

        NicknameStorage.clearNickname(player.getUUID());
        updatePlayerDisplayName(player, player.getGameProfile().getName());

        if (isAdmin) {
            source.sendSuccess(new StringTextComponent("§aSurnom de §f" + player.getGameProfile().getName() + " §asupprimé!"), false);
            player.sendMessage(new StringTextComponent("§aVotre surnom a été supprimé par un administrateur"), player.getUUID());
        } else {
            source.sendSuccess(new StringTextComponent("§aSurnom supprimé!"), false);
        }

        return 1;
    }

    private static void updatePlayerDisplayName(ServerPlayerEntity player, String displayName) {
        // Mettre à jour le nom affiché au-dessus de la tête
        player.setCustomName(new StringTextComponent(displayName));
        player.setCustomNameVisible(true);

        // Synchroniser avec tous les clients (onglet TAB)
        SPlayerListItemPacket packet = new SPlayerListItemPacket(SPlayerListItemPacket.Action.UPDATE_DISPLAY_NAME, player);
        player.server.getPlayerList().broadcastAll(packet);

        // Envoyer le surnom à tous les clients pour l'affichage au-dessus de la tête
        NetworkHandler.CHANNEL.send(
                PacketDistributor.ALL.noArg(),
                new SyncNicknamePacket(player.getUUID(), displayName)
        );
    }
}
