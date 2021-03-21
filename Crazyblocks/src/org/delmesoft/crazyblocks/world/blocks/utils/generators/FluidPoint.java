package org.delmesoft.crazyblocks.world.blocks.utils.generators;

import org.delmesoft.crazyblocks.utils.datastructure.Array;
import org.delmesoft.crazyblocks.world.World;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.utils.ChunkData;
import org.delmesoft.crazyblocks.world.blocks.utils.DataPoint;

/**
 * Created by sergi on 19/09/17.
 */

public class FluidPoint {

    public static final double STEP_TIME = 0.3F;

    private final World world;
    private final byte type;
    private final byte maxDepth;

    private double time;

    private Array<DataPoint> liquid = new Array<DataPoint>();
    private Array<DataPoint> tmp    = new Array<DataPoint>();

    public FluidPoint(int x, int y, int z, short rawType, byte maxRange, World world) {
        this(x, y, z, (byte) (rawType & 0xFF), (byte) ((rawType & 0xFF00) >>> 8), maxRange, world);
    }

    public FluidPoint(int x, int y, int z, byte type, byte data, byte maxRange, World world) {
        this(x, y, z, type, getDirection(data), getDepth(data), maxRange, world);
    }

    private FluidPoint(int x, int y, int z, byte type, byte direction, byte depth, byte maxRange, World world) {
        byte data = getData(depth, direction);
        world.chunkGenerator.addBlocksAt(ChunkData.getRawType(type, data), x, y, z);
        world.chunkGenerator.updateMeshes(world.getChunkAbsolute(x, z), new Array<Runnable>());
        this.liquid.add(new DataPoint(x, y, z, data));
        this.type = type;
        this.maxDepth = maxRange;
        this.world = world;
    }

    public void update(float delta) {

        if(hasPoints() && (time += delta) > STEP_TIME) {
            time = 0;

            boolean update = false;

            Chunk chunk;

            byte data;
            int direction;
            byte depth;
            int x, y, z;
            DataPoint point;

            do {

                // TODO : ver condición de reparto para no repartir en exceso

                point = liquid.pop();

                x = point.x;
                y = point.y;
                z = point.z;

                data = point.data;

                depth     = getDepth(data);
                direction = getDirection(data);

                chunk = world.getChunkAbsolute(x, z);

                short b = chunk.getRawTypeAbsolute(x, y - 1, z);
                if (ChunkData.getType(b) == Blocks.AIR.id) { // vertical (bottom = air)

                    data = getData(1, Blocks.Side.BOTTOM.ordinal());
                    if(direction == Blocks.Side.BOTTOM.ordinal()) { // Si ya esta cayendo
                        world.chunkGenerator.addBlocksAt(ChunkData.getRawType(type, getData(depth, direction)), x, y, z);
                        update = true;
                    }
                    tmp.add(new DataPoint(x, y - 1, z, data));

                } else if(depth < maxDepth) {

                    if (ChunkData.getType(b) != type) { // Horizontal

                        // TODO : si se ramifica por dos direcciones direccion diagonal
                        // Ej: direccion bottom = 0x001, si se ramifica por la iz = 0x011, si tambien
                        // se ramifica por la der = 0x111 (en este caso volveria a ser 0x001)

                        if (isAccessible(chunk, x - 1, y, z, Blocks.Side.LEFT, depth)) { // left
                            data = getData(depth + 1, Blocks.Side.LEFT.ordinal());
                            world.chunkGenerator.addBlocksAt(ChunkData.getRawType(type, data), x - 1, y, z);
                            tmp.add(new DataPoint(x - 1, y, z, data));
                            update = true;
                        }
                        if (isAccessible(chunk, x + 1, y, z, Blocks.Side.RIGHT, depth)) { // right
                            data = getData(depth + 1, Blocks.Side.RIGHT.ordinal());
                            world.chunkGenerator.addBlocksAt(ChunkData.getRawType(type, data), x + 1, y, z);
                            tmp.add(new DataPoint(x + 1, y, z, data));
                            update = true;
                        }
                        if (isAccessible(chunk, x, y, z - 1, Blocks.Side.BACK, depth)) { // back
                            data = getData(depth + 1, Blocks.Side.BACK.ordinal());
                            world.chunkGenerator.addBlocksAt(ChunkData.getRawType(type, data), x, y, z - 1);
                            tmp.add(new DataPoint(x, y, z - 1, data));
                            update = true;
                        }
                        if (isAccessible(chunk, x, y, z + 1, Blocks.Side.FRONT, depth)) { // front
                            data = getData(depth + 1, Blocks.Side.FRONT.ordinal());
                            world.chunkGenerator.addBlocksAt(ChunkData.getRawType(type, data), x, y, z + 1);
                            tmp.add(new DataPoint(x, y, z + 1, data));
                            update = true;
                        }

                    } else {

                        world.chunkGenerator.addBlocksAt(ChunkData.getRawType(type, data), x, y, z);
                        world.chunkGenerator.updateMeshes(world.getChunkAbsolute(x, z), new Array<Runnable>());

                    }

                }

            } while(hasPoints());

            liquid.addAll(tmp);
            tmp.clear();

            if(update)
                world.chunkGenerator.updateMeshes(chunk, new Array<Runnable>());

        }

    }

    private boolean isAccessible(Chunk chunk, int x, int y, int z, Blocks.Side side, byte depth) {
        short rawType = chunk.getRawTypeAbsolute(x, y, z);
        if(ChunkData.getType(rawType) == Blocks.AIR.id) {
            /*int n = maxDepth;
            for(int x1 = 0; x1 < 1 + Math.abs(side.x) * n; x1++) {
                for(int z1 = 0; z1 < 1 + Math.abs(side.z) * n; z1++) {
                    for(int y1 = y; y1 < y + 1; y1++) {
                        if (chunk.getBlockTypeAbsolute(x + x1 * side.x, y1, z + z1 * side.z) != Blocks.AIR.id) {
                            return false;
                        }
                    }
                }
            }*/
            return true;
        }
        return ChunkData.getType(rawType) == type && getDepth(ChunkData.getData(rawType)) > depth;
    }

    public boolean hasPoints() {
        return liquid.size > 0;
    }

    public static byte getDepth(byte data) {
        return (byte) (data & 0xF);
    }

    public static byte getDirection(byte data) {
        return (byte) ((data & 0xF0) >>> 4);
    }

    private static byte getData(int depth, int direction) {
        return (byte) ((depth & 0xF) | ((direction & 0xF) << 4));
    }

}
