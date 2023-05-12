package com.fpt.edu.repository;

import com.fpt.edu.models.Ban;
import com.fpt.edu.models.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query(value = "select c from Contact c where  " +
            "concat(c.name, c.email, c.phone, c.comment) like %?1%")
    Page<Contact> findByKeyword(String keyword, Pageable pageable);
}
