package uk.jamieisgeek.ugclogging;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;

public class ConfigController {
    private final UGCLogging plugin;
    private Configuration config;
    private Configuration messages;

    public ConfigController(UGCLogging plugin) {
        this.plugin = plugin;
        this.initialize();
    }

    public void initialize() {
        try {
            plugin.saveDefaultConfig();
            plugin.saveResource("messages.yml.yml", false);

            File messages = Arrays.stream(plugin.getDataFolder().listFiles())
                    .filter(file -> file.getName().equals("messages.yml"))
                    .findFirst()
                    .orElse(null);

            if (messages == null) {
                return;
            }

            this.config = plugin.getConfig();
            this.messages = YamlConfiguration.loadConfiguration(messages);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getFromConfig(String path) { return config.get(path); }
    public String getFromMessages(String path) {
        return messages.getString(path);
    }
}