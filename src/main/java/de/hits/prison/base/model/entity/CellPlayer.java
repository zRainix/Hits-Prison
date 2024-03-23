package de.hits.prison.base.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "cell_player")
public class CellPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "refCellId", nullable = false)
    private Cell cell;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(unique = true, nullable = false)
    private PrisonPlayer refPrisonplayer;

    public enum Role {
        MEMBER,
        MODERATOR,
        Admin,
        OWNER;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public PrisonPlayer getRefPrisonplayer() {
        return refPrisonplayer;
    }

    public void setRefPrisonplayer(PrisonPlayer player) {
        this.refPrisonplayer = player;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
