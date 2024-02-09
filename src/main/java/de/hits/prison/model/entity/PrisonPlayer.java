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
    @Column(unique = true)
    String playerUuid;
    LocalDateTime lastLogin;
    LocalDateTime lastLogout;
    @Column(nullable = false)
    Long playtimeInMinutes;

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

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDateTime getLastLogout() {
        return lastLogout;
    }

    public void setLastLogout(LocalDateTime lastLogout) {
        this.lastLogout = lastLogout;
    }

    public Long getPlaytimeInMinutes() {
        return playtimeInMinutes;
    }

    public void setPlaytimeInMinutes(Long playtimeInMinutes) {
        this.playtimeInMinutes = playtimeInMinutes;
    }

    public PlayerCurrency getPlayerCurrency() {
        return playerCurrency;
    }



}
