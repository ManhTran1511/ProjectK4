package com.fpt.edu.repository;

import com.fpt.edu.models.BanDat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BanDatRepository extends JpaRepository<BanDat,Long> {
}
