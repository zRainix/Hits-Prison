package de.hits.prison.model.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prison_pickaxe")
public class PrisonPickaxe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "pickaxe_id", nullable = false)
    String pickaxeId;

    @Column(name = "fortune")
    String fortune;

    @Column(name = "efficiency")
    String efficiency;

    @Column(name = "Speed")
    String speed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPickaxeId() {
        return pickaxeId;
    }

    public void setPickaxeId(String pickaxeId) {
        this.pickaxeId = pickaxeId;
    }

    public String getFortune() {
        return fortune;
    }

    public void setFortune(String fortune) {
        this.fortune = fortune;
    }

    public String getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(String efficiency) {
        this.efficiency = efficiency;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }



}
