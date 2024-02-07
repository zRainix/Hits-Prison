package de.hits.prison.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "player_currency")
public class PlayerCurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long vulcanicAsh;
    Long obsidianShards;
    @OneToOne
    @JoinColumn(nullable = false)
    PrisonPlayer refPrisonPlayer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVulcanicAsh() {
        return vulcanicAsh;
    }

    public void setVulcanicAsh(Long vulcanicAsh) {
        this.vulcanicAsh = vulcanicAsh;
    }

    public Long getObsidianShards() {
        return obsidianShards;
    }

    public void setObsidianShards(Long obsidianShards) {
        this.obsidianShards = obsidianShards;
    }

    public PrisonPlayer getRefPrisonPlayer() {
        return refPrisonPlayer;
    }

    public void setRefPrisonPlayer(PrisonPlayer refPrisonPlayer) {
        this.refPrisonPlayer = refPrisonPlayer;
    }
}
