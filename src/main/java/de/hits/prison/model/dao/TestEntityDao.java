package de.hits.prison.model.dao;

import de.hits.prison.model.entity.TestEntity;
import de.hits.prison.model.helper.Repository;

public class TestEntityDao extends Repository<TestEntity, Long> {

    public TestEntityDao() {
        super(TestEntity.class);
    }
}
