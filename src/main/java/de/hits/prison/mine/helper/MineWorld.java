package de.hits.prison.mine.helper;

import de.hits.prison.HitsPrison;
import de.hits.prison.base.autowire.anno.Autowired;
import de.hits.prison.base.autowire.anno.Component;
import de.hits.prison.base.model.dao.PlayerMineDao;
import de.hits.prison.base.model.entity.PlayerMine;
import de.hits.prison.base.model.entity.PrisonPlayer;
import de.hits.prison.mine.fileUtil.MineTemplateUtil;
import de.hits.prison.mine.fileUtil.MineUtil;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.Chunk;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginLogger;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MineWorld {

    @Autowired
    private static Logger logger;

    @Autowired
    private static MineUtil mineUtil;
    @Autowired
    private static PlayerMineDao playerMineDao;

    World world;
    MineTemplateUtil mineTemplateUtil;
    PrisonPlayer prisonPlayer;
    File templateFolder;

    Block currentMineCenterBlock;
    int currentMineSize, currentMineDepth;

    public MineWorld(File templateFolder, MineTemplateUtil mineTemplateUtil, PrisonPlayer prisonPlayer) throws IOException {
        String worldName = "player-mine-" + prisonPlayer.getId();
        this.world = MineHelper.loadWorld(worldName, templateFolder, false);
        this.mineTemplateUtil = mineTemplateUtil;
        this.prisonPlayer = prisonPlayer;
        this.templateFolder = templateFolder;

        this.currentMineCenterBlock = null;
        this.currentMineSize = 0;
        this.currentMineDepth = 0;
        updateMine();
    }

    private void setWorldBorder() {
        double x = mineTemplateUtil.getCenterBlock().getBlockX();
        double z = mineTemplateUtil.getCenterBlock().getBlockZ();
        this.world.getWorldBorder().setCenter(x + 1, z + 1);
        this.world.getWorldBorder().setSize(mineTemplateUtil.getBorderRadius() * 2);
    }

    public void updateMine() {
        setWorldBorder();

        PlayerMine playerMine = playerMineDao.findByPrisonPlayer(prisonPlayer);

        Block centerBlock = this.world.getBlockAt(mineTemplateUtil.getCenterBlock());

        this.currentMineCenterBlock = centerBlock;

        MineUtil.AreaLevel areaLevel = mineUtil.calculateAreaLevel(playerMine.getAreaLevel());

        if (areaLevel == null) {
            areaLevel = MineUtil.getDefaultAreaLevel();
        }

        int size = areaLevel.getMineSize();
        int depth = areaLevel.getMineDepth();

        if (size < currentMineSize || depth < currentMineDepth) {
            String worldName = world.getName();
            try {
                this.world = MineHelper.loadWorld(worldName, templateFolder, true);
                this.currentMineSize = 0;
                this.currentMineDepth = 0;
                updateMine();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error while loading world: " + worldName + " for player " + prisonPlayer.getPlayerName(), e);
            }
            return;
        }

        this.currentMineSize = size;
        this.currentMineDepth = depth;

        List<Block> mineBlocks = new ArrayList<>();

        Block bottomCorner = centerBlock.getRelative(-size / 2 - 1, -depth, -size / 2 - 1);

        int fillX = bottomCorner.getX();
        int fillY = bottomCorner.getY();
        int fillZ = bottomCorner.getZ();

        fill(world, Material.BEDROCK, fillX, fillY, fillZ, fillX + size + 1, fillY + depth, fillZ + size + 1);

        for (int offsetX = 0; offsetX < size; offsetX++) {
            int x = offsetX - size / 2;
            for (int offsetZ = 0; offsetZ < size; offsetZ++) {
                int z = offsetZ - size / 2;
                for (int offsetY = 0; offsetY < depth; offsetY++) {
                    int y = -offsetY;
                    Block mineBlock = centerBlock.getRelative(x, y, z);
                    mineBlocks.add(mineBlock);
                }
            }
        }

        Collections.shuffle(mineBlocks);

        MineUtil.BlockLevel blockLevel = mineUtil.calculateBlockLevel(playerMine.getBlockLevel());
        if (blockLevel == null)
            blockLevel = MineUtil.getDefaultBlockLevel();

        Map<Material, Integer> blockMap = blockLevel.getBlocks();

        Optional<BigDecimal> optionalMax = blockMap.values().stream().map(BigDecimal::valueOf).reduce(BigDecimal::add);

        BigDecimal max = optionalMax.orElse(BigDecimal.ONE);

        Iterator<Map.Entry<Material, Integer>> entryIterator = blockLevel.getBlocks().entrySet().iterator();

        Map.Entry<Material, Integer> currentEntry = entryIterator.next();
        int sum = currentEntry.getValue();

        for (int i = 0; i < mineBlocks.size(); i++) {
            BigDecimal percentage = BigDecimal.valueOf(i).divide(BigDecimal.valueOf(mineBlocks.size()), 3, RoundingMode.HALF_UP);
            BigDecimal blocksPercentage = BigDecimal.valueOf(sum).divide(max, 3, RoundingMode.HALF_UP);

            if(blocksPercentage.compareTo(percentage) < 0 && entryIterator.hasNext()) {
                currentEntry = entryIterator.next();
                sum += currentEntry.getValue();
            }

            setBlockInNativeChunk(mineBlocks.get(i), currentEntry.getKey());
        }

        for (Player player : world.getPlayers()) {
            if (isMineBlock(player.getLocation()))
                player.teleport(centerBlock.getRelative(0, 1, 0).getLocation().add(0.5, 0, 0.5));
        }

        updateChunks(world.getPlayers());
    }

    private final Set<Chunk> chunks = new HashSet<>();

    private void setBlockInNativeChunk(Block block, Material material) {
        setBlockInNativeChunk(block.getWorld(), block.getX(), block.getY(), block.getZ(), CraftMagicNumbers.getBlock(material).o());
    }

    private void setBlockInNativeChunk(World world, int x, int y, int z, IBlockData ibd) {
        net.minecraft.world.level.World nmsWorld = ((CraftWorld) world).getHandle();
        Chunk nmsChunk = nmsWorld.d(x >> 4, z >> 4);
        BlockPosition bp = new BlockPosition(x, y, z);
        nmsChunk.setBlockState(bp, ibd, false, false);
        chunks.add(nmsChunk);
    }

    private void updateChunks(List<Player> players) {
        chunks.forEach(chunk -> {
            ClientboundLevelChunkWithLightPacket reload = new ClientboundLevelChunkWithLightPacket(chunk, chunk.r.z_(), null, null);
            for (Player player : players) {
                EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
                entityPlayer.c.b(reload);
            }
        });
        chunks.clear();
    }

    public int[] getMineBounds() {
        Block centerBlock = currentMineCenterBlock;
        int size = currentMineSize;
        int depth = currentMineDepth;

        int startX = centerBlock.getX() - size / 2;
        int startY = centerBlock.getY() - depth + 1;
        int startZ = centerBlock.getZ() - size / 2;
        int endX = startX + size - 1;
        int endY = startY + depth - 1;
        int endZ = startZ + size - 1;

        return new int[]{startX, startY, startZ, endX, endY, endZ};
    }

    public boolean isMineBlock(Block block) {
        return isMineBlock(block.getLocation());
    }

    public boolean isMineBlock(Location location) {
        int blockX = location.getBlockX();
        int blockY = location.getBlockY();
        int blockZ = location.getBlockZ();

        int[] bounds = getMineBounds();

        int startX = bounds[0];
        int startY = bounds[1];
        int startZ = bounds[2];
        int endX = bounds[3];
        int endY = bounds[4];
        int endZ = bounds[5];

        return (blockX >= startX && blockX <= endX) && (blockY >= startY && blockY <= endY) && (blockZ >= startZ && blockZ <= endZ);
    }

    private void fill(World world, Material material, int startX, int startY, int startZ, int endX, int endY, int endZ) {
        if (world == null || material == null)
            return;

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                for (int z = startZ; z <= endZ; z++) {
                    if (x == startX || x == endX || y == startY || y == endY || z == startZ || z == endZ)
                        setBlockInNativeChunk(world.getBlockAt(x, y, z), material);
                }
            }
        }
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public MineTemplateUtil getMineTemplateUtil() {
        return mineTemplateUtil;
    }

    public void setMineTemplateUtil(MineTemplateUtil mineTemplateUtil) {
        this.mineTemplateUtil = mineTemplateUtil;
    }

    public PrisonPlayer getPrisonPlayer() {
        return prisonPlayer;
    }

    public void setPrisonPlayer(PrisonPlayer prisonPlayer) {
        this.prisonPlayer = prisonPlayer;
    }

}
