package me.yleoft.voicechatWorlguard;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import de.maxhenkel.voicechat.api.events.EntitySoundPacketEvent;
import de.maxhenkel.voicechat.api.events.VoiceDistanceEvent;
import me.yleoft.voicechatWorlguard.worldguard.Flags;
import org.bukkit.entity.Player;

public class WorldguardVoicechatPlugin implements VoicechatPlugin {

    public static final String id = "voicechat_worldguard";
    public static final String vcEnabledBypassPermission = "voicechatworlguard.bypass.voicechat-enabled";
    public static final String vcMutedBypassPermission = "voicechatworlguard.bypass.voicechat-muted";

    @Override
    public String getPluginId() {
        return id;
    }

    @Override
    public void initialize(VoicechatApi api) {
        VoicechatWorldguard.getInstance().getLogger().info("Worldguard voicechat plugin initialized by voicechat API");
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophone);
        registration.registerEvent(EntitySoundPacketEvent.class, this::onListen);
        try {
            Class.forName("de.maxhenkel.voicechat.api.events.VoiceDistanceEvent");
            registration.registerEvent(VoiceDistanceEvent.class, this::onProcessDistance);
        } catch (ClassNotFoundException ignored) {
        }
    }

    private void onMicrophone(MicrophonePacketEvent event) {
        if(
                event.getSenderConnection() == null ||
                !(event.getSenderConnection().getPlayer().getPlayer() instanceof Player player) ||
                player.hasPermission(vcEnabledBypassPermission)
        ) {
            return;
        }

        try {
            LocalPlayer localPlayer = getLocalPlayer(player);
            boolean canBypass = WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld());
            if (canBypass) return;

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(localPlayer.getLocation());

            if(!set.testState(localPlayer, Flags.VOICECHAT_ENABLED)) {
                event.cancel();
            };
        }catch (Exception e) {
            VoicechatWorldguard.getInstance().getLogger().severe("Error checking WorldGuard regions for voicechat:");
        }
    }
    private void onProcessDistance(VoiceDistanceEvent event) {
        if(
                event.getSenderConnection() == null ||
                !(event.getSenderConnection().getPlayer().getPlayer() instanceof Player player)
        ) {
            return;
        }

        try {
            LocalPlayer localPlayer = getLocalPlayer(player);
            boolean canBypass = WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld());
            if (canBypass) return;

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(localPlayer.getLocation());

            try {
                event.setDistance(set.queryValue(localPlayer, Flags.VOICECHAT_DISTANCE));
            }catch (Exception ignored) {
            }
        }
        catch (Exception e) {
            VoicechatWorldguard.getInstance().getLogger().severe("Error checking WorldGuard regions for voicechat.");
        }
    }
    private void onListen(EntitySoundPacketEvent event) {
        if(
                event.getReceiverConnection() == null ||
                !(event.getReceiverConnection().getPlayer().getPlayer() instanceof Player player) ||
                player.hasPermission(vcMutedBypassPermission)
        ) {
            return;
        }

        try {
            LocalPlayer localPlayer = getLocalPlayer(player);
            boolean canBypass = WorldGuard.getInstance().getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld());
            if (canBypass) return;

            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionQuery query = container.createQuery();
            ApplicableRegionSet set = query.getApplicableRegions(localPlayer.getLocation());

            if(set.testState(localPlayer, Flags.VOICECHAT_MUTED)) {
                event.cancel();
            };
        }catch (Exception e) {
            VoicechatWorldguard.getInstance().getLogger().severe("Error checking WorldGuard regions for voicechat:");
        }
    }

    public LocalPlayer getLocalPlayer(Player player) {
        return WorldGuardPlugin.inst().wrapPlayer(player);
    }

}
