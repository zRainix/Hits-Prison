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

    boolean privateMine;
    int blockLevel;
    int areaLevel;
    int sellLevel;
    int rebirthLevel;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    PrisonPlayer refPrisonPlayer;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "refPlayerMine")
    List<MineTrustedPlayer> trustedPlayers;

}
