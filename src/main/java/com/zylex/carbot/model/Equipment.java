package com.zylex.carbot.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Model model;

    private String name;

    private String code;

    public Equipment() {
    }

    public Equipment(Model model, String name, String code) {
        this.model = model;
        this.name = name;
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipment equipment = (Equipment) o;
        return Objects.equals(model, equipment.model) &&
                Objects.equals(name, equipment.name) &&
                Objects.equals(code, equipment.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(model, name, code);
    }

    @Override
    public String toString() {
        return "Equipment{" +
                "id=" + id +
                ", model=" + model +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
