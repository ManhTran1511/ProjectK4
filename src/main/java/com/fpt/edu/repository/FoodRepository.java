package com.fpt.edu.repository;

import com.fpt.edu.models.BanDat;
import com.fpt.edu.models.Food;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {
    @Query(value = "select f from Food f where  " +
            "concat(f.cate_id, f.name, f.price, f.des) like %?1%")

    Page<Food> findByKeyword(String keyword, Pageable pageable);
}
