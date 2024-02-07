package de.hits.prison.model.entity;

import javax.persistence.*;

@Entity
@Table(name = "test2")
public class TestEntity2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String testString;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTestString() {
        return testString;
    }

    public void setTestString(String testString) {
        this.testString = testString;
    }
}
