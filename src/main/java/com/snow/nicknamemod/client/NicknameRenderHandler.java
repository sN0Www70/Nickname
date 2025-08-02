package com.snow.nicknamemod.client;

import com.snow.nicknamemod.storage.ClientNicknameCache;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "nicknamemod", value = Dist.CLIENT)
public class NicknameRenderHandler {

    @SubscribeEvent
    public static void onRenderName(RenderNameplateEvent event) {
        if (!(event.getEntity() instanceof AbstractClientPlayerEntity)) return;

        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) event.getEntity();
        String nickname = ClientNicknameCache.getNickname(player.getUUID());

        if (nickname != null && !nickname.trim().isEmpty()) {
            ITextComponent nicknameComponent = new StringTextComponent(nickname);
            // TODO: parse nickname to colored component if needed
            event.setContent(nicknameComponent);
        }
    }
}
