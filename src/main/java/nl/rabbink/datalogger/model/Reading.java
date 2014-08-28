package nl.rabbink.datalogger.model;

import java.util.Date;

public class Reading {

    private Long id;

    private Double value;

    private Date timestamp;

    public Reading() {
    }

    public Reading(Date timestamp, Double value) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
