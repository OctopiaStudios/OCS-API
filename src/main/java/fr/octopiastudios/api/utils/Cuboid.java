package fr.octopiastudios.api.utils;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Cuboid {

    private final String worldName;
    private final int x1, x2, y1, y2, z1, z2;

    public Cuboid(Location location1, Location location2) {
        worldName = location1.getWorld().getName();
        x1 = Math.min(location1.getBlockX(), location2.getBlockX());
        y1 = Math.min(location1.getBlockY(), location2.getBlockY());
        z1 = Math.min(location1.getBlockZ(), location2.getBlockZ());
        x2 = Math.max(location1.getBlockX(), location2.getBlockX());
        y2 = Math.max(location1.getBlockY(), location2.getBlockY());
        z2 = Math.max(location1.getBlockZ(), location2.getBlockZ());
    }

    public Cuboid(Location location) {
        this(location, location);
    }

    public Cuboid(Cuboid cuboid) {
        this(cuboid.getWorld().getName(), cuboid.x1, cuboid.y1, cuboid.z1, cuboid.x2, cuboid.y2, cuboid.z2);
    }

    private Cuboid(String string, int n, int n2, int n3, int n4, int n5, int n6) {
        worldName = string;
        x1 = Math.min(n, n4);
        x2 = Math.max(n, n4);
        y1 = Math.min(n2, n5);
        y2 = Math.max(n2, n5);
        z1 = Math.min(n3, n6);
        z2 = Math.max(n3, n6);
    }

    public Location getLowerLocation() {
        return new Location(getWorld(), x1, y1, z1);
    }

    public Location getUpperLocation() {
        return new Location(getWorld(), x2, y2, z2);
    }

    public Location getCenter() {
        int i = getUpperX() + 1;
        int j = getUpperY() + 1;
        int k = getUpperZ() + 1;
        return new Location(getWorld(), getLowerX() + (i - getLowerX()) / 2.0D, getLowerY() + (j - getLowerY()) / 2.0D,
                getLowerZ() + (k - getLowerZ()) / 2.0D);
    }

    public World getWorld() {
        World world = org.bukkit.Bukkit.getWorld(worldName);
        if (world == null) {
            java.util.logging.Logger.getLogger("Le monde " + worldName + "n'est pas charg� !");
        }
        return world;
    }

    public int getSizeX() {
        return x2 - x1 + 1;
    }

    public int getSizeY() {
        return y2 - y1 + 1;
    }

    public int getSizeZ() {
        return z2 - z1 + 1;
    }

    public int getLowerX() {
        return x1;
    }

    public int getLowerY() {
        return y1;
    }

    public int getLowerZ() {
        return z1;
    }

    public int getUpperX() {
        return x2;
    }

    public int getUpperY() {
        return y2;
    }

    public int getUpperZ() {
        return z2;
    }

    public List<Player> getPlayersInside() {
        List<Player> list = new ArrayList<Player>();
        for (Player player : org.bukkit.Bukkit.getServer().getOnlinePlayers()) {
            if (contains(player.getLocation())) {
                list.add(player);
            }
        }
        return list;
    }

    public List<LivingEntity> getLivingEntityInside() {
        List<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity entity : getWorld().getLivingEntities()) {
            if (contains(entity.getLocation())) {
                list.add(entity);
            }
        }
        return list;
    }

    public List<LivingEntity> getLivingEntityInside(org.bukkit.entity.EntityType type) {
        List<LivingEntity> list = new ArrayList<LivingEntity>();
        for (LivingEntity entity : getWorld().getLivingEntities()) {
            if ((contains(entity.getLocation())) && (entity.getType() == type)) {
                list.add(entity);
            }
        }
        return list;
    }

    public boolean isInside(Player player) {
        return contains(player.getLocation());
    }

    public Block[] corners() {
        Block[] arrayOfBlock = new Block[8];
        World localWorld = getWorld();
        arrayOfBlock[0] = localWorld.getBlockAt(x1, y1, z1);
        arrayOfBlock[1] = localWorld.getBlockAt(x1, y1, z2);
        arrayOfBlock[2] = localWorld.getBlockAt(x1, y2, z1);
        arrayOfBlock[3] = localWorld.getBlockAt(x1, y2, z2);
        arrayOfBlock[4] = localWorld.getBlockAt(x2, y1, z1);
        arrayOfBlock[5] = localWorld.getBlockAt(x2, y1, z2);
        arrayOfBlock[6] = localWorld.getBlockAt(x2, y2, z1);
        arrayOfBlock[7] = localWorld.getBlockAt(x2, y2, z2);
        return arrayOfBlock;
    }

    public boolean contains(int n, int n2, int n3) {
        if ((n >= x1) && (n <= x2) && (n2 >= y1) && (n2 <= y2) && (n3 >= z1) && (n3 <= z2)) {
            return true;
        }
        return false;
    }

    public boolean contains(Block block) {
        return contains(block.getLocation());
    }

    public boolean contains(Location location) {
        if (!worldName.equals(location.getWorld().getName())) {
            return false;
        }
        return contains(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public int getVolume() {
        return getSizeX() * getSizeY() * getSizeZ();
    }

    public Cuboid getBoundingCuboid(Cuboid paramCuboid) {
        if (paramCuboid == null) {
            return this;
        }
        int i = Math.min(getLowerX(), paramCuboid.getLowerX());
        int j = Math.min(getLowerY(), paramCuboid.getLowerY());
        int k = Math.min(getLowerZ(), paramCuboid.getLowerZ());
        int m = Math.max(getUpperX(), paramCuboid.getUpperX());
        int n = Math.max(getUpperY(), paramCuboid.getUpperY());
        int i1 = Math.max(getUpperZ(), paramCuboid.getUpperZ());

        return new Cuboid(worldName, i, j, k, m, n, i1);
    }

    public Block getRelativeBlock(int n, int n2, int n3) {
        return getWorld().getBlockAt(x1 + n, y1 + n2, z1 + n3);
    }

    public Block getRelativeBlock(World world, int n, int n2, int n3) {
        return world.getBlockAt(x1 + n, y1 + n2, z1 + n3);
    }

    public List<org.bukkit.Chunk> getChunks() {
        ArrayList<org.bukkit.Chunk> arrayList = new ArrayList<Chunk>();
        World world = getWorld();
        int i = getLowerX() & 0xFFFFFFF0;
        int j = getUpperX() & 0xFFFFFFF0;
        int k = getLowerZ() & 0xFFFFFFF0;
        int m = getUpperZ() & 0xFFFFFFF0;
        for (int n = i; n <= j; n += 16) {
            for (int i1 = k; i1 <= m; i1 += 16) {
                arrayList.add(world.getChunkAt(n >> 4, i1 >> 4));
            }
        }
        return arrayList;
    }

    public Cuboid clone() {
        return new Cuboid(this);
    }

    public String toString() {
        return new String("Cuboid: " + worldName + "," + x1 + "," + y1 + "," + z1 + "=>" + x2 + "," + y2 + "," + z2);
    }
}
