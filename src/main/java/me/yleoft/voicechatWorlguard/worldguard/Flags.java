package me.yleoft.voicechatWorlguard.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DoubleFlag;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.IntegerFlag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import me.yleoft.voicechatWorlguard.VoicechatWorldguard;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public abstract class Flags {

    public static StateFlag VOICECHAT_ENABLED = new StateFlag("voicechat-enabled", true);
    public static IntegerFlag VOICECHAT_DISTANCE = new IntegerFlag("voicechat-distance");
    public static StateFlag VOICECHAT_MUTED = new StateFlag("voicechat-muted", false);
    public static DoubleFlag VOICE_MUFFLE = new DoubleFlag("voice-muffle");
    public static DoubleFlag VOICE_OBSTRUCTED_VOLUME = new DoubleFlag("voice-obstructed-volume");

    public static void registerFlags() {
        try {
            if (Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") instanceof WorldGuardPlugin wg) {
                try {
                    FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
                    //<editor-fold desc="vcEnabledFlag">
                    try {
                        registry.register(VOICECHAT_ENABLED);
                    } catch (FlagConflictException | IllegalStateException e) {
                        Flag<?> existingFlag = registry.get("voicechat-enabled");
                        if (existingFlag instanceof StateFlag) {
                            VOICECHAT_ENABLED = (StateFlag) existingFlag;
                        }
                    }
                    //</editor-fold>
                    //<editor-fold desc="vcDistanceFlag">
                    try {
                        registry.register(VOICECHAT_DISTANCE);
                    } catch (FlagConflictException | IllegalStateException e) {
                        Flag<?> existingFlag = registry.get("voicechat-distance");
                        if (existingFlag instanceof IntegerFlag) {
                            VOICECHAT_DISTANCE = (IntegerFlag) existingFlag;
                        }
                    }
                    //</editor-fold>
                    //<editor-fold desc="vcMutedFlag">
                    try {
                        registry.register(VOICECHAT_MUTED);
                    } catch (FlagConflictException | IllegalStateException e) {
                        Flag<?> existingFlag = registry.get("voicechat-muted");
                        if (existingFlag instanceof StateFlag) {
                            VOICECHAT_MUTED = (StateFlag) existingFlag;
                        }
                    }
                    //</editor-fold>
                } catch (Exception e) {
                    VoicechatWorldguard.getInstance().getLogger().severe("Failed to hook into WorldGuard properly.");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            VoicechatWorldguard.getInstance().getLogger().log(Level.SEVERE, "Error hooking into WorldGuard");
        }
    }

}
