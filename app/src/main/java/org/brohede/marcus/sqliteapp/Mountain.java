package org.brohede.marcus.sqliteapp;

/**
 * Created by marcus on 2018-04-25.
 */

public class Mountain {

    private String name;
    private String location;
    private int height;
    private String bild;
    private String url;

    public Mountain(String n, String l, int h, String b, String u){
        name = n;
        location = l;
        height = h;
        bild = b;
        url = u;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
    public int getHeight(){
        return height;
    }

    public String getBild() {
        return bild;
    }

    public String getUrl() {
        return url;
    }
}
