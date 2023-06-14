package uk.jamieisgeek.ugclogging.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import uk.jamieisgeek.ugclogging.ConfigController;
import uk.jamieisgeek.ugclogging.UGCLogging;

public class SignPlaceListener implements Listener {
    private final ConfigController configController;
    public SignPlaceListener(ConfigController configController) {
        this.configController = configController;
    }
    @EventHandler
    public void onSignPlace(SignChangeEvent event) {
        if(!(boolean) configController.getFromConfig("settings.log-signs")) return;

        Player editor = event.getPlayer();
        String[] lines = event.getLines();

        if (lines.length == 0) {
            return;
        }

        String locationString = "X: " + event.getBlock().getX() + " Y: " + event.getBlock().getY() + " Z: " + event.getBlock().getZ() + " World: " + event.getBlock().getWorld().getName();

        if((boolean) configController.getFromConfig("settings.only-log-filtered") && UGCLogging.containsFilteredWord(formatLines(lines))) return;

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player ->
                        player.hasPermission(configController.getFromConfig("alertPermission").toString())
                )
                .forEach(player -> {
                    String message = configController.getFromMessages("signAlert");
                    message = message.replace("%player%", editor.getName());
                    message = message.replace("%location%", locationString);
                    message = message.replace("%lines%", String.join("", formatLines(lines)));

                    player.sendMessage(message);
                });

        if(!(boolean) configController.getFromConfig("settings.log-to-discord")) return;
        UGCLogging.LogToDiscord(formatLines(lines), editor, "Sign Place", locationString);
    }

    private String formatLines(String[] lines) {
        StringBuilder builder = new StringBuilder();
        for(String line : lines) {
            builder.append(line).append(", ");
        }
        return builder.toString();
    }
}
