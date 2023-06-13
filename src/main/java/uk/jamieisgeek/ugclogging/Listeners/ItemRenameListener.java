package uk.jamieisgeek.ugclogging.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import uk.jamieisgeek.ugclogging.ConfigController;
import uk.jamieisgeek.ugclogging.UGCLogging;

import java.util.List;

public class ItemRenameListener implements Listener {
    private final ConfigController configController;

    public ItemRenameListener(ConfigController configController) {
        this.configController = configController;
    }

    @EventHandler
    public void onItemRename(InventoryClickEvent event) {
        if(!(boolean) configController.getFromConfig("settings.log-rename")) return;

        if(!(event.getInventory().getType().equals(InventoryType.ANVIL))) return;
        Player editor = (Player) event.getWhoClicked();

        ItemStack item = event.getCurrentItem();
        if(item == null) return;

        if(!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
        String itemName = item.getItemMeta().getDisplayName();

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player ->
                        player.hasPermission(configController.getFromConfig("alertPermission").toString())
                )
                .forEach(player -> player.sendMessage(configController.getFromMessages("itemAlert")
                        .replace("%player%", editor.getName())
                        .replace("%item%", itemName)
                ));

        if(!(boolean) configController.getFromConfig("settings.log-to-discord")) return;
        UGCLogging.LogToDiscord(List.of(itemName).toArray(new String[0]), editor, "Item Rename", null);
    }
}
