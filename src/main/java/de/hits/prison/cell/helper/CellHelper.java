package de.hits.prison.cell.helper;

import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.CellPlayerDao;
import de.hits.prison.base.model.entity.Cell;
import de.hits.prison.base.model.entity.CellPlayer;
import org.bukkit.entity.Player;

@Component
public class CellHelper {

    @Autowired
    private static CellPlayerDao cellPlayerDao;

    public Cell getCell(Player player) {
        CellPlayer cellPlayer = cellPlayerDao.findByUuid(player.getUniqueId());

        if(cellPlayer == null) {
            return null;
        }

        return cellPlayer.getCell();
    }
}
