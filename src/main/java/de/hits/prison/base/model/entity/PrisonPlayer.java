package de.hits.prison.base.model.entity;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @OneToOne(mappedBy = "refPrisonPlayer", fetch = FetchType.EAGER)
    PlayerCurrency playerCurrency;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "refPrisonPlayer")
    List<PlayerEnchantment> playerEnchantments;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToOne(mappedBy = "refPrisonPlayer", fetch = FetchType.EAGER)
    PlayerMine playerMine;

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

    public List<PlayerEnchantment> getPlayerEnchantments() {
        return playerEnchantments;
    }

    public PlayerMine getPlayerMine() {
        return playerMine;
    }

    public OfflinePlayer getOfflinePlayer() {
        return Bukkit.getOfflinePlayer(UUID.fromString(getPlayerUuid()));
    }
}
