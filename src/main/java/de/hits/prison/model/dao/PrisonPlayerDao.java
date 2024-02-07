package de.hits.prison.model.dao;

import de.hits.prison.model.entity.PrisonPlayer;
import de.hits.prison.model.helper.Repository;

public class PrisonPlayerDao extends Repository<PrisonPlayer, Long> {

    public PrisonPlayerDao() {
        super(PrisonPlayer.class);
    }

    public PrisonPlayer findById(Long id) {
        return finder().equal("id", id).findFirst();
    }
}
