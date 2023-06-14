package uk.jamieisgeek.ugclogging.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import uk.jamieisgeek.ugclogging.ConfigController;
import uk.jamieisgeek.ugclogging.UGCLogging;

import java.util.List;

public class BookWriteListener implements Listener {
    /*
        @contributor AutismFather
    */
    private final ConfigController configController;
    public BookWriteListener(ConfigController configController) {
        this.configController = configController;
    }

    @EventHandler
    public void onBookWrite(PlayerEditBookEvent event) {
        if(!(boolean) configController.getFromConfig("settings.log-books")) return;
        Player editor = event.getPlayer();

        String bookTitle;
        boolean isSigned;
        StringBuilder contents = new StringBuilder();
        String content;

        if(event.isSigning() && event.getNewBookMeta().getTitle() != null) {
            bookTitle = event.getNewBookMeta().getTitle();
            isSigned = true;
        } else {
            isSigned = false;
            bookTitle = "Unsigned Book";
        }

        List<String> pages = event.getNewBookMeta().getPages();

        for (String page: pages) {
            contents.append(page.replaceAll("`", "'")).append("\n");
        }

        content = contents.toString();
        if(content.length() < 256 ){
            content = content.substring(0, content.length()-1);
        }

        String finalContent = content;

        if((boolean) configController.getFromConfig("settings.only-log-filtered") && UGCLogging.containsFilteredWord(finalContent)) return;

        Bukkit.getOnlinePlayers()
                .stream()
                .filter(player ->
                        player.hasPermission(configController.getFromConfig("alertPermission").toString())
                )
                .forEach(player -> player.sendMessage(configController.getFromMessages("bookAlert")
                        .replace("%player%", editor.getName())
                        .replace("%title%", bookTitle)
                        .replace("%signed%", String.valueOf(isSigned))
                        .replace("%content%", finalContent)
                ));
        String string = "Title: " + bookTitle + " Signed: " + isSigned + " Content: " + finalContent;
        if(!(boolean) configController.getFromConfig("settings.log-to-discord")) return;
        UGCLogging.LogToDiscord(string, editor, "Book Edit", null);
    }
}
