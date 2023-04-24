package com.fpt.edu.repository;

import com.fpt.edu.models.Chef;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChefRepository extends JpaRepository<Chef, Long> {
}
