package com.example.demo.repository;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "event_parent")
public class EventParent implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    @Column
    private long ptrParent;
    @Column
    private long ptrEvent;
    @Column
    private String attendee;
    @Column
    private Date created_date;
}
