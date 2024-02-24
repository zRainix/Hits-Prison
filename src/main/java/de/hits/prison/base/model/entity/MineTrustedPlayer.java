package de.hits.prison.base.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "player_mine")
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
}
