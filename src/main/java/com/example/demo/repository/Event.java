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
@Table(name = "event")
public class Event implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    @Column
    private String cfc_type;
    @Column
    private String name;
    @Column
    private Date created_date;
}
