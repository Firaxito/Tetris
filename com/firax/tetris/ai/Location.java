package com.firax.tetris.ai;

public class Location {

    public int value;
    public int rotation;
    public int position;

    public Location(int rotation, int position, int value){
        this.value = value;
        this.rotation= rotation;
        this.position = position;
    }

}

