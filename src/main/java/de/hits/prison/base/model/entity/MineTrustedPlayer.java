package de.hits.prison.base.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "mine_trusted_player")
public class MineTrustedPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    PlayerMine refPlayerMine;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    PrisonPlayer refTrustedPrisonPlayer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlayerMine getRefPlayerMine() {
        return refPlayerMine;
    }

    public void setRefPlayerMine(PlayerMine refPlayerMine) {
        this.refPlayerMine = refPlayerMine;
    }

    public PrisonPlayer getRefTrustedPrisonPlayer() {
        return refTrustedPrisonPlayer;
    }

    public void setRefTrustedPrisonPlayer(PrisonPlayer refTrustedPrisonPlayer) {
        this.refTrustedPrisonPlayer = refTrustedPrisonPlayer;
    }
}
