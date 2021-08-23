package it.polimi.tiw.TiwProject.beans;

import java.util.Date;

public class Offer {

    private int id;
    private float amount;
    private String sh_address;
    private int id_user;
    private int id_auction;
    private Date date;
    private String userUserName;

    public Offer(int id, float amount, String sh_address, int id_user, int id_auction, Date date, String userUserName) {
        this.id = id;
        this.amount = amount;
        this.sh_address = sh_address;
        this.id_user = id_user;
        this.id_auction = id_auction;
        this.date = date;
        this.userUserName = userUserName;
    }

    public Offer(float amount, String sh_address, int id_user, int id_auction) {
        this.id = id;
        this.amount = amount;
        this.sh_address = sh_address;
        this.id_user = id_user;
        this.id_auction = id_auction;
    }

    public int getId() {
        return id;
    }

    public float getAmount() {
        return amount;
    }

    public String getSh_address() {
        return sh_address;
    }

    public int getId_user() {
        return id_user;
    }

    public int getId_auction() {
        return id_auction;
    }

    public Date getDate() {
        return date;
    }

    public String getUserUserName() {
        return userUserName;
    }
}
