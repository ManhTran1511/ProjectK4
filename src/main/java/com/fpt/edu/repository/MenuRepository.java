package com.fpt.edu.repository;

import com.fpt.edu.models.Food;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Food, Long> {
}
