package de.hits.prison.base.model.dao;

import de.hits.prison.base.model.anno.Repository;
import de.hits.prison.base.model.entity.Cell;
import de.hits.prison.base.model.entity.CellPlayer;
import de.hits.prison.base.model.helper.PrisonRepository;

import java.util.List;

@Repository
public class CellDao extends PrisonRepository<Cell, Long> {

    public CellDao() {
        super(Cell.class);
    }

    public Cell findById(Long id) {
        return super.findById(id);
    }

    public List<Cell> findBySameCellId(Cell cell) {
        return finder()
                .equal("refCellId", cell.getCellId())
                .findAll();
    }
}
