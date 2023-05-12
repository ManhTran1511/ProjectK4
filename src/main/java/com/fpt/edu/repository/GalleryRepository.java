package com.fpt.edu.repository;

import com.fpt.edu.models.Ban;
import com.fpt.edu.models.Gallery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface GalleryRepository extends JpaRepository<Gallery, Long> {
    @Query(value = "select g from Gallery g where  " +
            "concat(g.id, g.name) like %?1%")
    Page<Gallery> findByKeyword(String keyword, Pageable pageable);
}
