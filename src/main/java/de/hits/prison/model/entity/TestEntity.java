package de.hits.prison.model.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "test")
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String testString;
    @OneToOne(fetch = FetchType.LAZY)
    TestEntity2 testEntity2;

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

    public TestEntity2 getTestEntity2() {
        return testEntity2;
    }

    public void setTestEntity2(TestEntity2 testEntity2) {
        this.testEntity2 = testEntity2;
    }
}
