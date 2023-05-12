package com.fpt.edu.repository;

import com.fpt.edu.models.Food;
import com.fpt.edu.models.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    @Query(value = "select i from Invoice i where  " +
            "concat(i.table_name, i.date, i.total) like %?1%")
    Page<Invoice> findByKeyword(String keyword, Pageable pageable);
}
