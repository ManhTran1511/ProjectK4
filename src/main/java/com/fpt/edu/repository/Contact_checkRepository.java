package com.fpt.edu.repository;

import com.fpt.edu.models.Ban;
import com.fpt.edu.models.Contact_check;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface Contact_checkRepository extends JpaRepository<Contact_check, Long> {
    @Query(value = "select ctc from Contact_check ctc where  " +
            "concat(ctc.name, ctc.email, ctc.phone, ctc.comment) like %?1%")
    Page<Contact_check> findByKeyword(String keyword, Pageable pageable);
}
