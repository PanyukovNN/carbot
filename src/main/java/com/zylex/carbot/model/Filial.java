package com.zylex.carbot.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "filial")
public class Filial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Dealer dealer;

    private String address;

    private String code;

    @OneToMany(mappedBy="filial", fetch = FetchType.EAGER)
    private List<Car> cars = new ArrayList<>();

    public Filial() {
    }

    public Filial(Dealer dealer, String address, String code) {
        this.dealer = dealer;
        this.address = address;
        this.code = code;
        dealer.getFilials().add(this);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Filial filial = (Filial) o;
        return Objects.equals(address, filial.address) &&
                Objects.equals(code, filial.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, code);
    }

    @Override
    public String toString() {
        return "Filial{" +
                "id=" + id +
                ", dealer=" + dealer +
                ", address='" + address + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
