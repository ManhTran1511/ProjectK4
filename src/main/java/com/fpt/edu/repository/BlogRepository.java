package com.fpt.edu.repository;

import com.fpt.edu.models.Ban;
import com.fpt.edu.models.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BlogRepository  extends JpaRepository<Blog, Long> {
    @Query(value = "select bl from Blog bl where  " +
            "concat(bl.name, bl.content) like %?1%")
    Page<Blog> findByKeyword(String keyword, Pageable pageable);
}
