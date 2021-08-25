package it.polimi.tiw.TiwProject.beans;

import it.polimi.tiw.TiwProject.dao.OfferDAO;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

public class DashboardAuction {

    private int id;
    private String description;
    private String name;
    private InputStream picture; // base64 encoded
    private Date end_date;
    private Date start_date;
    private int id_user;
    private int item_id;
    private Boolean open;
    private float initial_price;
    private float min_rise;

    private Long hoursRemaining;
    private Long daysRemaining;
    private float winningBet;

    public DashboardAuction(int id, String description, String name, InputStream picture, Date end_date, Date start_date, int id_user, int item_id, Boolean open, float initial_price, float min_rise) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.picture = picture;
        this.end_date = end_date;
        this.start_date = start_date;
        this.id_user = id_user;
        this.item_id = item_id;
        this.open = open;
        this.initial_price = initial_price;
        this.min_rise = min_rise;

        calculateDaysRemaining();
        calculateHoursRemaining();

    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public InputStream getPicture() {
        return picture;
    }

    public String getBase64Picture() throws IOException {

        if (this.picture == null) return null;

        byte[] imageInBytes = IOUtils.toByteArray(this.picture);
        return Base64.getEncoder().encodeToString(imageInBytes);
    }

    public Date getEnd_date() {
        return end_date;
    }

    public Date getStart_date() {
        return start_date;
    }

    public int getId_user() {
        return id_user;
    }

    public int getItem_id() {
        return item_id;
    }

    public Long getHoursRemaining() {
        return hoursRemaining;
    }

    public Long getDaysRemaining() {
        return daysRemaining;
    }

    public float getWinningBet() {
        return winningBet;
    }

    public void setWinningBet(float winningBet) {
        this.winningBet = winningBet;
    }

    public float getInitial_price() {
        return initial_price;
    }

    public float getMinOffer(){

        return (this.winningBet > initial_price) ? ( winningBet + min_rise ) : (initial_price + min_rise);
    }

    public Boolean isOpen(){

        return open;
    }

    public Boolean isClosed(){

        return !open;
    }

    public float getMin_rise() {
        return min_rise;
    }
    // custom methods

    private void calculateHoursRemaining() {

        ZoneId defaultZoneId = ZoneId.systemDefault();
        Instant instant = end_date.toInstant();

        this.hoursRemaining = ( ChronoUnit.HOURS.between(LocalDateTime.now(), instant.atZone(defaultZoneId).toLocalDateTime()) > 0 ) ? ChronoUnit.HOURS.between(LocalDateTime.now(), instant.atZone(defaultZoneId).toLocalDateTime()) % 24 : 0;
    }

    private void calculateDaysRemaining() {

        ZoneId defaultZoneId = ZoneId.systemDefault();
        Instant instant = end_date.toInstant();

        this.daysRemaining = ( ChronoUnit.DAYS.between(LocalDate.now(), instant.atZone(defaultZoneId).toLocalDate()) > 0 ) ? ChronoUnit.DAYS.between(LocalDate.now(), instant.atZone(defaultZoneId).toLocalDate()) : 0;
    }

    public Boolean isOutDated(){

        return end_date.before(new Date());
    }
}
