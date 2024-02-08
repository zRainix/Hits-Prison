package de.hits.prison.model.entity;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "player_currency")
public class PlayerCurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    BigInteger vulcanicAsh;
    BigInteger obsidianShards;
    BigInteger exp;

    @OneToOne
    @JoinColumn(nullable = false, unique = true)
    PrisonPlayer refPrisonPlayer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getVulcanicAsh() {
        return vulcanicAsh;
    }

    public void setVulcanicAsh(BigInteger vulcanicAsh) {
        this.vulcanicAsh = vulcanicAsh;
    }

    public BigInteger getObsidianShards() {
        return obsidianShards;
    }

    public void setObsidianShards(BigInteger obsidianShards) {
        this.obsidianShards = obsidianShards;
    }

    public BigInteger getExp() {
        return exp;
    }

    public void setExp(BigInteger exp) {
        this.exp = exp;
    }

    public PrisonPlayer getRefPrisonPlayer() {
        return refPrisonPlayer;
    }

    public void setRefPrisonPlayer(PrisonPlayer refPrisonPlayer) {
        this.refPrisonPlayer = refPrisonPlayer;
    }
}
