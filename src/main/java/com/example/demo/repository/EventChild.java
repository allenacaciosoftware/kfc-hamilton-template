package com.example.demo.repository;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "event_child")
public class EventChild implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    @Column
    private long ptrChild;
    @Column
    private long ptrEvent;
    @Column
    private String participation;
    @Column
    private Date created_date;
}
