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
@Table(name = "parent")
public class Parent implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    @Column
    private String fullName;
    @Column(name = "email")
    private String email;
    @Column
    private String mobileNo;
    @Column
    private String facebookAccount;
    @Column
    private String guardianFullName;
    @Column
    private String guardianMobile;
    @Column
    private Date createdDate;
}
