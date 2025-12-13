package me.yleoft.voicechatWorlguard;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import me.yleoft.voicechatWorlguard.worldguard.Flags;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public final class VoicechatWorldguard extends JavaPlugin {

    public static VoicechatWorldguard instance;
    public static VoicechatWorldguard getInstance() {
        return instance;
    }

    @Nullable
    private WorldguardVoicechatPlugin vcPlugin;

    @Override
    public void onLoad() {
        Flags.registerFlags();
    }

    @Override
    public void onEnable() {
        instance = this;
        BukkitVoicechatService vcservice = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (vcservice != null) {
            vcPlugin = new WorldguardVoicechatPlugin();
            vcservice.registerPlugin(vcPlugin);
            getLogger().info("Successfully registered voicechat worlguard extension");
        } else {
            getLogger().info("Failed to register voicechat worlguard extension: voicechat service not found");
        }
        new Metrics(this, 27929);
    }

    @Override
    public void onDisable() {
        if (vcPlugin != null) {
            getServer().getServicesManager().unregister(vcPlugin);
            getLogger().info("Successfully unregistered voicechat worlguard extension");
        }
        instance = null;
        vcPlugin = null;
    }
}
