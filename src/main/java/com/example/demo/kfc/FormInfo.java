package com.example.demo.kfc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Data;
import lombok.SneakyThrows;

@Data
public class FormInfo {

    private long child_Id;
    private String child_fullname;
    private String child_preferred_name;
    private String child_birth_date;
    private String child_baptism_date;
    private String child_first_communion_date;
    private String child_confirmation_date;
    private String child_gender;
    private String child_allergies;
    private String child_medical_needs;
    private String child_hobbies;
    private String child_event;
    private String child_participation;

    private String parent_fullname;
    private String parent_email;
    private String parent_mobile_no;
    private String parent_facebook_account;
    private String parent_event;
    private String guardian_fullname;
    private String guardian_mobile;

    @SneakyThrows
    @Override
    public String toString(){
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(this);
    }
}