package com.fpt.edu.models;

import javax.persistence.*;

@Entity
@Table( name = "ban")
public class Ban {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loaiBan;

    private int trangThai;

    public Ban() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoaiBan() {
        return loaiBan;
    }

    public void setLoaiBan(String loaiBan) {
        this.loaiBan = loaiBan;
    }

    public int getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(int trangThai) {
        this.trangThai = trangThai;
    }


}
