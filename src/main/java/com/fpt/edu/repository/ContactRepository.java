package com.fpt.edu.repository;

import com.fpt.edu.models.BanDat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<BanDat,Long> {
}
