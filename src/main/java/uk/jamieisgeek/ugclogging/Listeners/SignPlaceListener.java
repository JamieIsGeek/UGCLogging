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

        String locationString = "X: " + event.getBlock().getX() + " Y: " + event.getBlock().getY() + " Z: " + event.getBlock().getZ() + " World: " + event.getBlock().getWorld();

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player ->
                        player.hasPermission(configController.getFromConfig("alertPermission").toString())
                )
                .forEach(player -> player.sendMessage(configController.getFromMessages("signAlert")
                        .replace("%player%", editor.getName())
                        .replace("%location%", locationString)
                        .replace("%line1%", lines[0])
                        .replace("%line2%", lines[1])
                        .replace("%line3%", lines[2])
                        .replace("%line4%", lines[3])
                ));

        if(!(boolean) configController.getFromConfig("settings.log-to-discord")) return;
        UGCLogging.LogToDiscord(lines, editor, "Sign Place", locationString);
    }
}
