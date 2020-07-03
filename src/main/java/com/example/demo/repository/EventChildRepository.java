package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventChildRepository extends CrudRepository<EventChild, Long> {
    EventChild findByPtrChildAndPtrEvent(long childId, long eventId);
    List<EventChild> findByPtrEvent(long eventId);
}

