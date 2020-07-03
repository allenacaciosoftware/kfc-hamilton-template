package com.example.demo.kfc;

import java.io.Serializable;
import lombok.Data;

@Data
public class KfcReport implements Serializable {
    private long childId;
    private String childFullname;
    private int childAge;
    private String childPreferredName;
    private String childBirthdate;
    private String parentsName;
    private String parentsEmail;

}
