package de.hits.prison.base.model.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "cell")
public class Cell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "cell")
    private List<CellPlayer> cellPlayers;

    public Long getCellId() {
        return id;
    }

    public void setCellId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CellPlayer> getCellPlayers() {
        return cellPlayers;
    }

    public void setCellPlayers(List<CellPlayer> cellPlayers) {
        this.cellPlayers = cellPlayers;
    }
}
