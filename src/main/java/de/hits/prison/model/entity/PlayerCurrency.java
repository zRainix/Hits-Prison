package de.hits.prison.model.entity;

import javax.persistence.*;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

@Entity
@Table(name = "player_currency")
public class PlayerCurrency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(columnDefinition = "DECIMAL(65, 0)")
    BigInteger vulcanicAsh;
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

    public String formatVulcanicAsh() {
        return formatValue(getVulcanicAsh());
    }

    public String formatObsidianShards() {
        return formatValue(getObsidianShards());
    }

    public String formatExp() {
        return formatValue(getExp());
    }

    private String formatValue(BigInteger value) {
        if (value == null) {
            return "-";
        }

        if (value.compareTo(BigInteger.valueOf(1_000)) < 0) {
            return value.toString();
        } else if (value.compareTo(BigInteger.valueOf(1_000_000)) < 0) {
            return format(value, 3, "k");
        } else if (value.compareTo(BigInteger.valueOf(1_000_000_000)) < 0) {
            return format(value, 6, "m");
        } else if (value.compareTo(BigInteger.valueOf(1_000_000_000_000L)) < 0) {
            return format(value, 9, "b");
        } else {
            return format(value);
        }
    }

    private String format(BigInteger value, int exp, String suffix) {
        NumberFormat format = new DecimalFormat("#.##");
        format.setRoundingMode(RoundingMode.FLOOR);
        double scaledValue = value.doubleValue() / Math.pow(10, exp);
        return format.format(scaledValue) + suffix;
    }

    private String format(BigInteger value) {
        NumberFormat format = new DecimalFormat("#.####E0");
        format.setRoundingMode(RoundingMode.FLOOR);
        return format.format(value).replace("E", "x10^");
    }
}
