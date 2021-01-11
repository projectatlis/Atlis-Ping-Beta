package com.johnymuffin.tracker.atlisping;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class AtlisPlayer {

    private Block location;
    private Long moveTime;

    public AtlisPlayer(Player p) {
        //Create Player
        location = p.getLocation().getBlock();
        moveTime = System.currentTimeMillis() / 1000L;

    }

    public void updatePlayer(Player p) {
        if(!(location == p.getLocation().getBlock())) {
            //If Players Has Moved
            location = p.getLocation().getBlock();
            moveTime = System.currentTimeMillis() / 1000L;
        }
    }
    public boolean isPlayerAFK(){
        int difference = (int) ((System.currentTimeMillis()/1000L) - moveTime);
        if(difference > 300) {
            return true;
        } else {
            return false;
        }
    }





}
