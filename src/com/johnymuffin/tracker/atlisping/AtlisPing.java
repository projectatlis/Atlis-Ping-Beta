package com.johnymuffin.tracker.atlisping;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.logging.Logger;

public class AtlisPing extends JavaPlugin implements Listener {

    private String world = "world";

    private int taskID;
    private HashMap<String, AtlisPlayer> playerLocations = new HashMap<String, AtlisPlayer>();
    private int playerCount;


    private Logger log;
    private PluginDescriptionFile pdf;


    private AtlisPing plugin;
    private AtlisPingConfig config;

    //User Settings
    private String host;
    private String servername;
    private String key;
    private Boolean debugMode;

    public void onEnable() {
        log = this.getServer().getLogger();
        pdf = this.getDescription();
        plugin = this;
        log.info("[" + pdf.getName() + "] Is loading, Version: " + pdf.getVersion());
        config = AtlisPingConfig.getInstance(plugin);

        host = config.getAPIHost();
        servername = config.getServerName();
        key = config.getServerPassword();
        debugMode = config.getDebugMode();

        world = Bukkit.getServer().getWorlds().get(0).getName();
        taskID = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                //Let cleanup some stuff for the garbage collector
                playerLocations.keySet().removeIf(entries -> {
                    Player player = Bukkit.getServer().getPlayer(entries);
                    if (player == null || !player.isOnline()) {
                        debugMessage("[AlitsPing] Deleting " + entries + " from AFK tracker");
                        return true;
                    }

                    return false;
                });
                //Lets Run Our Updates & Get Player Count
                playerCount = 0;
                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    //Create Atlis Profile if it doesn't Exist
                    if (!playerLocations.containsKey(p.getName())) {
                        debugMessage("[AtlisPing] Generating Atlis Profile For: " + p.getName());
                        playerLocations.put(p.getName(), new AtlisPlayer(p));
                    }
                    //Update Player
                    playerLocations.get(p.getName()).updatePlayer(p);
                    //Is Player AFK???
                    if (!playerLocations.get(p.getName()).isPlayerAFK()) {
                        debugMessage("[AtlisPing] Player: " + p.getName() + " is not AFK");
                        playerCount = playerCount + 1;
                    } else {
                        debugMessage("[AtlisPing] Player: " + p.getName() + " is AFK");
                    }
                }
                long worldTime = Bukkit.getServer().getWorld(world).getTime();
                final String requestURL = host + "/" + "listner" + "/" + servername + "/" + key + "/" + playerCount + "/" + worldTime + "/" + (System.currentTimeMillis() / 1000L) + "/" + pdf.getVersion();
                plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.setProperty("http.agent", "");
                            final URLConnection connection = new URL(requestURL).openConnection();
                            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
                            connection.setConnectTimeout(1000);
                            final InputStream jsonearly = connection.getInputStream();
                        } catch (IOException e) {
                            log.warning("[" + pdf.getName() + "] Ping Failed");
                            System.out.println(e);
                        }
                    }
                }, 0L);

            }
        }, 0L, 1200);


    }

    public void debugMessage(String s) {
        if (this.debugMode) {
            log.info("[" + pdf.getName() + "] " + s);
        }
    }

    public void onDisable() {
        Bukkit.getServer().getScheduler().cancelTask(taskID);
        log.info("[" + pdf.getName() + "] has been disabled");
    }

}
