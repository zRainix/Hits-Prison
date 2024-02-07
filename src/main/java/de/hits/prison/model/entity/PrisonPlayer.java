package de.hits.prison.model.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prison_player")
public class PrisonPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String playerName;
    String playerUuid;
    LocalDateTime lastPlayed;
    @OneToOne(mappedBy = "refPrisonPlayer")
    PlayerCurrency playerCurrency;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(String playerUuid) {
        this.playerUuid = playerUuid;
    }

    public LocalDateTime getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(LocalDateTime lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public PlayerCurrency getPlayerCurrency() {
        return playerCurrency;
    }
}
