package com.example.demo.repository;

import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface EventParentRepository extends CrudRepository<EventParent, Long> {
    List<EventParent> findByPtrParentAndPtrEvent(long parentId, long eventId);
}

