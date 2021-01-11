package com.johnymuffin.tracker.atlisping;

import org.bukkit.util.config.Configuration;

import java.io.File;

public class AtlisPingConfig extends Configuration {

    private AtlisPing plugin;
    private static AtlisPingConfig singleton;


    private AtlisPingConfig(AtlisPing plugin) {
        super(new File(plugin.getDataFolder(), "config.yml"));
        this.plugin = plugin;
        this.reload();
    }

    private void setPluginInstance(AtlisPing plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        this.load();
        this.write();
        this.save();
    }

    private void write() {
        this.getAPIHost();
        this.getServerName();
        this.getServerPassword();
        this.getDebugMode();

    }

    public String getAPIHost() {
        String key = "ping.host";
        if (this.getString(key) == null) {
            this.setProperty(key, "url");
        }
        final String apiHost = this.getString(key);
        this.removeProperty(key);
        this.setProperty(key, apiHost);
        return apiHost;
    }

    public String getServerName() {
        String key = "ping.serverName";
        if (this.getString(key) == null) {
            this.setProperty(key, "serverName");
        }
        final String serverName = this.getString(key);
        this.removeProperty(key);
        this.setProperty(key, serverName);
        return serverName;
    }

    public String getServerPassword() {
        String key = "ping.serverPassword";
        if (this.getString(key) == null) {
            this.setProperty(key, "serverPassword");
        }
        final String serverName = this.getString(key);
        this.removeProperty(key);
        this.setProperty(key, serverName);
        return serverName;
    }

    public Boolean getDebugMode() {
        String key = "ping.debugMode";
        if (this.getString(key) == null) {
            this.setProperty(key, (Object)true);
        }
        return this.getBoolean(key, true);
    }


    public static AtlisPingConfig getInstance(AtlisPing plugin) {
        if (AtlisPingConfig.singleton == null) {
            AtlisPingConfig.singleton = new AtlisPingConfig(plugin);
        }
        return AtlisPingConfig.singleton;
    }

    public static AtlisPingConfig getInstance() {
        if(AtlisPingConfig.singleton != null) {
            return AtlisPingConfig.singleton;
        }
        return null;
    }


}
