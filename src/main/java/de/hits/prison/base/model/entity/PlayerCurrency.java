package de.hits.prison.base.model.entity;

import de.hits.prison.server.util.NumberUtil;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "player_currency")
public class PlayerCurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(columnDefinition = "DECIMAL(65, 0)")
    BigInteger volcanicAsh;
    @Column(columnDefinition = "DECIMAL(65, 0)")
    BigInteger obsidianShards;
    @Column(columnDefinition = "DECIMAL(65, 0)")
    BigInteger exp;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, unique = true)
    PrisonPlayer refPrisonPlayer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigInteger getVolcanicAsh() {
        return volcanicAsh;
    }

    public void setVolcanicAsh(BigInteger volcanicAsh) {
        this.volcanicAsh = volcanicAsh;
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

    public String formatVolcanicAsh() {
        return NumberUtil.formatValue(getVolcanicAsh());
    }

    public String formatObsidianShards() {
        return NumberUtil.formatValue(getObsidianShards());
    }

    public String formatExp() {
        return NumberUtil.formatValue(getExp());
    }

}
