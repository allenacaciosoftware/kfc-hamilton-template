package com.example.demo.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ParentRepository extends CrudRepository<Parent, Long> {
    Parent findByEmail(String email);

    @Query("Select p from Parent p where p.email like %:email%")
    Parent findByEmailContaining(@Param("email") String email);

}

