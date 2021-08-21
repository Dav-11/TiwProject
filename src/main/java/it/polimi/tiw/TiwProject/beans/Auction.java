package it.polimi.tiw.TiwProject.beans;

import java.util.Date;

public class Auction {
    private int id;
    private int id_user;
    private Date start_date;
    private Date end_date;
    private float min_rise;
    private float initial_price;
    private Boolean open;

    public Auction(int id, int id_user, Date start_date, Date end_date, float min_rise) {
        this.id = id;
        this.id_user = id_user;
        this.start_date = start_date;
        this.end_date = end_date;
        this.min_rise = min_rise;
    }

    public Auction(int id_user, Date start_date, Date end_date, float min_rise, float initial_price, Boolean open) {
        this.id = -1;
        this.id_user = id_user;
        this.start_date = start_date;
        this.end_date = end_date;
        this.min_rise = min_rise;
        this.initial_price = initial_price;
        this.open = open;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public Date getStart_date() {
        return start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public float getMin_rise() {
        return min_rise;
    }

    public void setMin_rise(float min_rise) {
        this.min_rise = min_rise;
    }

    public float getInitial_price() {
        return initial_price;
    }

    public Boolean getOpen() {
        return open;
    }
}
