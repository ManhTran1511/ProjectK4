package com.fpt.edu.repository;

import com.fpt.edu.models.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository  extends JpaRepository<Blog, Long> {
}
