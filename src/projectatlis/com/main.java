package projectatlis.com;

import java.io.File;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;

import io.utils.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.ChatColor;

public class main extends JavaPlugin implements Listener{
	

   Logger logger;
   String host = "https://node.johnymuffin.com";
   String servername = "SERVERNAME";
   String key = "SERVERKEY";
   


   public void onEnable() {
      this.logger = this.getServer().getLogger();
      this.logger.info("[AtlisPing] Enabling 1.4 - WorldEat");
      
      
      this.getServer().getScheduler().scheduleAsyncRepeatingTask(this, new Runnable() {
      public void run() {
    	try {
    		System.setProperty("http.agent", "");
    		final URLConnection time = new URL("http://worldtimeapi.org/api/timezone/Australia/Brisbane").openConnection();
            final String timejsonearly = IOUtils.toString(time.getInputStream());
            final JSONObject timejson = new JSONObject(timejsonearly);
            final Long unixtime = timejson.getLong("unixtime");
            //System.out.println(Long.toString(unixtime));
	  		try {
	        	System.setProperty("http.agent", "");
	        	int players = Bukkit.getServer().getOnlinePlayers().length;
	        	//long unixTime = System.currentTimeMillis() / 1000L;
	        	long worldTime = Bukkit.getServer().getWorld("world").getTime();
	            final URLConnection connection = new URL(host + "/" + "listner" + "/" + servername + "/" + key + "/" + players + "/" + worldTime + "/" + unixtime).openConnection();
	            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
	            connection.setConnectTimeout(1000);
	            final String jsonearly = IOUtils.toString(connection.getInputStream());
	            final JSONObject json = new JSONObject(jsonearly);
	            System.out.println("[AtlisPing] Ping At Time " + unixtime + " and " + players + " players.");
	            if (json.get("ping").equals("success")) {
	            	//Do Nothing, it was all gggg fam
	            } else {
	            	//this.logger.info("[AtlisPing] Ping Server Down");
	            }
			} catch (Exception var8) {
				//this.logger.info("[AtlisGuard] Plugin misconfigured, or API down. Letting player join");
	        }
    	} catch (Exception var8) {
    		
    	}
      }
      }, 0L, 1200);     
      

   }

   public void onDisable() {
      this.logger.warning("[AtlisPing] Successfully stopped!");
      //Send Shutdown Ping
      try {
	  System.setProperty("http.agent", "");
	  int players = this.getServer().getOnlinePlayers().length;
  	  long worldTime = Bukkit.getServer().getWorld("world").getTime();
      final URLConnection connection = new URL(host + "/" + "listner" + "/" + servername + "/" + key + "/" + players + "/" + worldTime + "/" + "0000").openConnection();
      connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
      connection.setConnectTimeout(1000);
      final String jsonearly = IOUtils.toString(connection.getInputStream());
      } catch (Exception var8) {
    	  
      }
   }




}
