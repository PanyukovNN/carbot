package com.zylex.carbot.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "model")
public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "link_part")
    private String linkPart;

    private String name;

    @OneToMany(mappedBy = "model", fetch = FetchType.EAGER)
    private List<Equipment> equipments = new ArrayList<>();

//    @OneToMany(fetch = FetchType.EAGER)
//    private List<Color> colors = new ArrayList<>();

    public Model() {
    }

    public Model(String linkPart, String name) {
        this.linkPart = linkPart;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLinkPart() {
        return linkPart;
    }

    public void setLinkPart(String linkPart) {
        this.linkPart = linkPart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Equipment> getEquipments() {
        return equipments;
    }

    public void setEquipments(List<Equipment> equipments) {
        this.equipments = equipments;
    }

//    public List<Color> getColors() {
//        return colors;
//    }
//
//    public void setColors(List<Color> colors) {
//        this.colors = colors;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return Objects.equals(linkPart, model.linkPart) &&
                Objects.equals(name, model.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(linkPart, name);
    }

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", linkPart='" + linkPart + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
