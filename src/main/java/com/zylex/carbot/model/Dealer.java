package com.zylex.carbot.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "dealer")
public class Dealer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String link;

    @OneToMany(mappedBy="dealer", fetch = FetchType.EAGER)
    private List<Filial> filials = new ArrayList<>();

    public Dealer() {
    }

    public Dealer(String link) {
        this.link = link;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<Filial> getFilials() {
        return filials;
    }

    public void setFilials(List<Filial> filials) {
        this.filials = filials;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dealer dealer = (Dealer) o;
        return Objects.equals(link, dealer.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(link);
    }

    @Override
    public String toString() {
        return "Dealer{" +
                "id=" + id +
                ", link='" + link + '\'' +
                '}';
    }
}
