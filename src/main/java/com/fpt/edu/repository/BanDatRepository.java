package com.fpt.edu.repository;

import com.fpt.edu.models.Ban;
import com.fpt.edu.models.BanDat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BanDatRepository extends JpaRepository<BanDat,Long> {
    @Query(value = "select bd from BanDat bd where  " +
            "concat(bd.tenKhachHang, bd.taiKhoan_id, bd.sdt, bd.gioDat, bd.gioRa, bd.ngayDat, bd.soNguoi, bd.status) like %?1%")
    Page<BanDat> findByKeyword(String keyword, Pageable pageable);
}
