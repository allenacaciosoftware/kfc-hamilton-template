package com.example.demo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChildRepository extends CrudRepository<Child, Long> {
    @Query("Select child from Child child where child.ptrParent = :parentId order by child.birth_date desc")
    List<Child> findChildrenByParentId(@Param("parentId") long parentId);
}

