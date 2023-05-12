package com.fpt.edu.models;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table( name = "ban")
@SQLDelete(sql = "UPDATE ban SET deleted = true WHERE id=?")
@Where(clause = "deleted=false")
public class Ban {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int loaiBan;

    private int trangThai;
    private boolean deleted = Boolean.FALSE;

    public Ban() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setname(String name) {
        this.name = name;
    }

    public int getLoaiBan() {
        return loaiBan;
    }

    public void setLoaiBan(int loaiBan) {
        this.loaiBan = loaiBan;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
