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
@Table(name = "child")
public class Child implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    @Column
    private String full_name;
    @Column
    private String preferred_name;
    @Column
    private Date birth_date;
    @Column
    private Date baptism_date;
    @Column
    private Date first_communion_date;
    @Column
    private Date confirmation_date;
    @Column
    private int age;
    @Column
    private String gender;
    @Column
    private String allergies;
    @Column
    private String medical_needs;
    @Column
    private String hobbies;
    @Column
    private long ptrParent;
    @Column
    private Date createdDate;

    private String getDateHumanReadable(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return date == null ? "" : simpleDateFormat.format(date);
    }
    public String getBirthDateHumanReadable() {
        return getDateHumanReadable(birth_date);
    }
    public String getBaptismDateHumanReadable() {
        return getDateHumanReadable(baptism_date);
    }
    public String getFirstCommunionDateHumanReadable() {
        return getDateHumanReadable(first_communion_date);
    }
    public String getConfirmationDateHumanReadable() {
        return getDateHumanReadable(confirmation_date);
    }
}
