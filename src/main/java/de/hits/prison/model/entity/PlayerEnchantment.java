package de.hits.prison.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "player_enchantment")
public class PlayerEnchantment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String enchantmentName;
    int enchantmentLevel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    PrisonPlayer refPrisonPlayer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnchantmentName() {
        return enchantmentName;
    }

    public void setEnchantmentName(String enchantmentName) {
        this.enchantmentName = enchantmentName;
    }

    public int getEnchantmentLevel() {
        return enchantmentLevel;
    }

    public void setEnchantmentLevel(int enchantmentLevel) {
        this.enchantmentLevel = enchantmentLevel;
    }

    public PrisonPlayer getRefPrisonPlayer() {
        return refPrisonPlayer;
    }

    public void setRefPrisonPlayer(PrisonPlayer refPrisonPlayer) {
        this.refPrisonPlayer = refPrisonPlayer;
    }
}
