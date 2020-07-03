package com.example.demo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventRepository extends CrudRepository<Event, Long> {
    Event findByName(String name);

    @Query("Select event from Event event where event.cfc_type = 'kfc' order by event.name")
    List<Event> findKfcEvents();
}

