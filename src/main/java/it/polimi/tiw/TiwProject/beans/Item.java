package it.polimi.tiw.TiwProject.beans;

import java.io.InputStream;

public class Item {
    private int id;
    private String name;
    private String description;
    private InputStream picture;
    private int id_auction;

    public Item(int id, String name, String description, InputStream picture, int id_auction) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.id_auction = id_auction;
    }

    public Item(String name, String description, InputStream picture, int id_auction) {
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.id_auction = id_auction;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public InputStream getPicture() {
        return picture;
    }

    public int getId_auction() {
        return id_auction;
    }

    public void setId(int id) {
        this.id = id;
    }
}
