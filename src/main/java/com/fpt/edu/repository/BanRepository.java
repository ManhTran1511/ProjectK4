package com.fpt.edu.repository;

import com.fpt.edu.models.Ban;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BanRepository extends JpaRepository<Ban, Long> {

//    @Query(value = "select b from Ban b where  " + "b.name like %?1%")
//    List<Ban> findByKeyword(String keyword);

    @Query(value = "select b from Ban b where  " +
            "concat(b.loaiBan, b.name) like %?1%")
    Page<Ban> findByKeyword(String keyword, Pageable pageable);

}
