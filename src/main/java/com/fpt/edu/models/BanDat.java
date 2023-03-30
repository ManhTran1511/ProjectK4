package com.fpt.edu.models;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Null;
import java.util.Date;

@Entity
@Table ( name = "bandat")
public class BanDat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenKhachHang;
    private int ban_id;

    private int taiKhoan_id;
    private String sdt;
    private String gioDat;

    private String ngayDat;

    private int soNguoi;

    public BanDat() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getBan_id() {
        return ban_id;
    }

    public void setBan_id(int ban_id) {
        this.ban_id = ban_id;
    }

    public int getTaiKhoan_id() {
        return taiKhoan_id;
    }

    public void setTaiKhoan_id(int taiKhoan_id) {
        this.taiKhoan_id = taiKhoan_id;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getGioDat() {
        return gioDat;
    }

    public void setGioDat(String gioDat) {
        this.gioDat = gioDat;
    }

    public String getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(String ngayDat) {
        this.ngayDat = ngayDat;
    }

    public int getSoNguoi() {
        return soNguoi;
    }

    public void setSoNguoi(int soNguoi) {
        this.soNguoi = soNguoi;
    }

}
