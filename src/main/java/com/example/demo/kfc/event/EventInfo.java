package com.example.demo.kfc.event;

import lombok.Data;

import java.util.List;

@Data
public class EventInfo {
    private List<Long> childIds;
    private long eventId;
}