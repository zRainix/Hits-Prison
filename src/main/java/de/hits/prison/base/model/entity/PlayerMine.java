package de.hits.prison.base.model.entity;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "player_mine")
public class PlayerMine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String templateName;
    boolean privateMine;
    int blockLevel;
    int areaLevel;
    int sellLevel;
    int rebirthLevel;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, unique = true)
    PrisonPlayer refPrisonPlayer;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "refPlayerMine")
    List<MineTrustedPlayer> trustedPlayers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public boolean isPrivateMine() {
        return privateMine;
    }

    public void setPrivateMine(boolean privateMine) {
        this.privateMine = privateMine;
    }

    public int getBlockLevel() {
        return blockLevel;
    }

    public void setBlockLevel(int blockLevel) {
        this.blockLevel = blockLevel;
    }

    public int getAreaLevel() {
        return areaLevel;
    }

    public void setAreaLevel(int areaLevel) {
        this.areaLevel = areaLevel;
    }

    public int getSellLevel() {
        return sellLevel;
    }

    public void setSellLevel(int sellLevel) {
        this.sellLevel = sellLevel;
    }

    public int getRebirthLevel() {
        return rebirthLevel;
    }

    public void setRebirthLevel(int rebirthLevel) {
        this.rebirthLevel = rebirthLevel;
    }

    public PrisonPlayer getRefPrisonPlayer() {
        return refPrisonPlayer;
    }

    public void setRefPrisonPlayer(PrisonPlayer refPrisonPlayer) {
        this.refPrisonPlayer = refPrisonPlayer;
    }

    public List<MineTrustedPlayer> getTrustedPlayers() {
        return trustedPlayers;
    }
}
