package com.zylex.carbot.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Filial filial;

    @ManyToOne
    private Equipment equipment;

    private String color;

    public Car() {
    }

    public Car(Filial filial, Equipment equipment, String color) {
        this.filial = filial;
        this.equipment = equipment;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Filial getFilial() {
        return filial;
    }

    public void setFilial(Filial filial) {
        this.filial = filial;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Car car = (Car) o;
        return Objects.equals(filial, car.filial) &&
                Objects.equals(equipment, car.equipment) &&
                Objects.equals(color, car.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filial, equipment, color);
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", filial=" + filial +
                ", equipment='" + equipment + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
