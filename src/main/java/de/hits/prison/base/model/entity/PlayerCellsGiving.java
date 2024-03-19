package de.hits.prison.base.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "cells_giving")
public class PlayerCellsGiving {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String cellsGivingItem;
    Long amount;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    PrisonPlayer refPrisonPlayer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCellsGivingItem() {
        return cellsGivingItem;
    }

    public void setCellsGivingItem(String cellsGivingItem) {
        this.cellsGivingItem = cellsGivingItem;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public PrisonPlayer getRefPrisonPlayer() {
        return refPrisonPlayer;
    }

    public void setRefPrisonPlayer(PrisonPlayer refPrisonPlayer) {
        this.refPrisonPlayer = refPrisonPlayer;
    }
}
