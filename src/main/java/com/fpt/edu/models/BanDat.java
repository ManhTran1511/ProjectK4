package com.fpt.edu.models;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Null;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table ( name = "bandat")
@SQLDelete(sql = "UPDATE bandat SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class BanDat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String tenKhachHang;
    private Long ban_id;
    private int taiKhoan_id;
    private String sdt;
    private String gioDat;
    private String gioRa;
    private String ngayDat;
    private int soNguoi;
    private String status;
    private boolean deleted = Boolean.FALSE;

    private String[] tablesBooking;

    public BanDat() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBan_id() {
        return ban_id;
    }

    public void setBan_id(Long ban_id) {
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

    public String getGioRa() {
        return gioRa;
    }

    public void setGioRa(String gioRa) {
        this.gioRa = gioRa;
    }

    public int getSoNguoi() {
        return soNguoi;
    }

    public void setSoNguoi(int soNguoi) {
        this.soNguoi = soNguoi;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    public String[] getTablesBooking() {
        return tablesBooking;
    }

    public void setTablesBooking(String[] tablesBooking) {
        this.tablesBooking = tablesBooking;
    }

}
