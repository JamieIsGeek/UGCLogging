package uk.jamieisgeek.ugclogging;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import uk.jamieisgeek.sootlib.Misc.DiscordWebhook;
import uk.jamieisgeek.ugclogging.Listeners.BookWriteListener;
import uk.jamieisgeek.ugclogging.Listeners.ItemRenameListener;
import uk.jamieisgeek.ugclogging.Listeners.SignPlaceListener;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;

public final class UGCLogging extends JavaPlugin {
    private static ConfigController configController;

    @Override
    public void onEnable() {
        configController = new ConfigController(this);
        getServer().getPluginManager().registerEvents(new BookWriteListener(configController), this);
        getServer().getPluginManager().registerEvents(new ItemRenameListener(configController), this);
        getServer().getPluginManager().registerEvents(new SignPlaceListener(configController), this);

        getLogger().info("UGCLogging has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("UGCLogging has been disabled!");
    }

    public static boolean containsFilteredWord(@NonNull String content) {
        String[] split = content.split(" ");
        for (String word : configController.getFilterList()) {
            for (String s : split) {
                if (s.toLowerCase().contains(word.toLowerCase())) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void LogToDiscord(@NonNull String content, @NonNull Player player, @NonNull String type, @Nullable String location) {
        DiscordWebhook webhook = new DiscordWebhook(configController.getFromConfig("discordWebhook").toString());
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle(type)
                .addField("Player", player.getName(), false)
                .addField("Location", location == null ? "Unknown" : location, false)
                .addField("Content", String.join("\n", content), false)
                .setFooter("UGCLogging", "")
                .setThumbnail("https://api.tydiumcraft.net/skin?uuid=" + player.getUniqueId() + "&type=body&direction=right")
                .setColor(Color.ORANGE)
        );

        try {
            webhook.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
