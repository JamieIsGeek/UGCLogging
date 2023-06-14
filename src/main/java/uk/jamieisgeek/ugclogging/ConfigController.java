package uk.jamieisgeek.ugclogging;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import uk.jamieisgeek.sootlib.Misc.TextManager;

import java.io.File;
import java.util.Arrays;

public class ConfigController {
    private final UGCLogging plugin;
    private Configuration config;
    private Configuration messages;
    private Configuration filter;

    public ConfigController(UGCLogging plugin) {
        this.plugin = plugin;
        this.initialize();
    }

    public void initialize() {
        try {
            plugin.saveDefaultConfig();

            // check if messages.yml and filter.yml exist, if not, create them
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            File messages = new File(plugin.getDataFolder(), "messages.yml");
            if (!messages.exists()) {
                plugin.saveResource("messages.yml", false);
            }

            File filter = new File(plugin.getDataFolder(), "filter.yml");
            if (!filter.exists()) {
                plugin.saveResource("filter.yml", false);
            }

            this.config = plugin.getConfig();
            this.messages = YamlConfiguration.loadConfiguration(messages);
            this.filter = YamlConfiguration.loadConfiguration(filter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getFromConfig(String path) { return config.get(path); }
    public String[] getFilterList() {
        return filter.getStringList("words").toArray(new String[0]);
    }
    public String getFromMessages(String path) {
        TextManager textManager = new TextManager();
        return textManager.translateHex(messages.getString(path));
    }
}