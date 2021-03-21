package org.delmesoft.crazyblocks.world.blocks.utils.generators;

import static org.delmesoft.crazyblocks.world.blocks.Chunk.HD;
import static org.delmesoft.crazyblocks.world.blocks.Plot.BIT_OFFSET;
import static org.delmesoft.crazyblocks.world.blocks.Plot.SIZE;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

import org.delmesoft.crazyblocks.entity.particle.ParticleTrash;
import org.delmesoft.crazyblocks.graphics.g2d.TextureCoordinates;
import org.delmesoft.crazyblocks.graphics.g3d.FastMesh;
import org.delmesoft.crazyblocks.math.MathHelper;
import org.delmesoft.crazyblocks.math.Vec3i;
import org.delmesoft.crazyblocks.utils.datastructure.Array;
import org.delmesoft.crazyblocks.utils.datastructure.ChunkMap.Iterator;
import org.delmesoft.crazyblocks.utils.datastructure.HashMap;
import org.delmesoft.crazyblocks.utils.datastructure.Pool;
import org.delmesoft.crazyblocks.utils.threads.ThreadPool;
import org.delmesoft.crazyblocks.utils.threads.ThreadPool.ExceptionListener;
import org.delmesoft.crazyblocks.utils.threads.ThreadPool.MyRunnable;
import org.delmesoft.crazyblocks.world.Settings;
import org.delmesoft.crazyblocks.world.World;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Blocks.Block;
import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.ChunkProvider;
import org.delmesoft.crazyblocks.world.blocks.ChunkProviderImpl;
import org.delmesoft.crazyblocks.world.blocks.Plot;
import org.delmesoft.crazyblocks.world.blocks.g3d.MeshManager;
import org.delmesoft.crazyblocks.world.blocks.g3d.MeshManagerFast;
import org.delmesoft.crazyblocks.world.blocks.g3d.MeshManagerFluid;
import org.delmesoft.crazyblocks.world.blocks.g3d.MeshManagerSmoothLighting;
import org.delmesoft.crazyblocks.world.blocks.utils.ChunkData;
import org.delmesoft.crazyblocks.world.blocks.utils.DataPointArray;
public class ChunkGeneratorImpl implements ChunkGenerator {

    protected static final long CLEAR_TIME = 10000;

    private boolean updating;
    private long lastClear;

    private int x, z;

    private final World world;
    private final ChunkProvider chunkProvider;

    private final ChunkDataGenerator chunkDataGenerator;
    private final ChunkLightGenerator chunkLightGenerator;

    private final ThreadPool threadPool;
    private final ExecutorService executor;

    private final Pool<MeshContextGenerator> meshContextPool;

    private final ReentrantLock reentrantLock;

    private final Array<MyRunnable> runnables;
    private final Array<Runnable> postRunnables;

    public ChunkGeneratorImpl(World world) {

        this.world = world;

        this.chunkProvider = new ChunkProviderImpl();

        chunkDataGenerator  = new ChunkDataGeneratorImpl();
        chunkLightGenerator = new ChunkLightGeneratorImpl();

        threadPool = new ThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors() >> 1));
        threadPool.setExceptionListener(new ExceptionListener() {
            @Override
            public void onException(Exception e) {
                e.printStackTrace();
            }
        });
        executor = Executors.newSingleThreadExecutor();

        meshContextPool = new Pool<ChunkGeneratorImpl.MeshContextGenerator>(64) {
            @Override
            protected MeshContextGenerator newObject() {
                return new MeshContextGenerator();
            }
        };

        reentrantLock = new ReentrantLock(true);

        runnables     = new Array<MyRunnable>();
        postRunnables = new Array<Runnable>();

    }

    @Override
    public void handlePostRunnables() {
        if(postRunnables.size > 0) {
            try {
                reentrantLock.lock();
                // int n = Math.min(Settings.chunkVisibility, postRunnables.size);
                for(int i = 0; i < postRunnables.size; i++) {
                    postRunnables.get(i).run();
                }
                postRunnables.clear();
                //  postRunnables.size -= n;
                //  System.arraycopy(postRunnables.items, n, postRunnables.items, 0, postRunnables.size);

            } finally {
                reentrantLock.unlock();
            }
        }
    }

    @Override
    public void update(int x, int z) {
        if (updating == false) {
            updating = true;
            this.x = x;
            this.z = z;
            executor.execute(updateTask);
        }
    }

    private Runnable updateTask = new Runnable() {
        @Override
        public void run() {

            threadPool.clear(true);
            //System.out.printf("Generating data at %d, %d\n", x, z);
            int chunkVisibility = Settings.chunkVisibility;

            final int minX = x - chunkVisibility;
            final int minZ = z - chunkVisibility;
            final int maxX = x + chunkVisibility;
            final int maxZ = z + chunkVisibility;

            long time = System.currentTimeMillis();
            if(time - lastClear > CLEAR_TIME) { // Clear memory
                lastClear = time;
                final Array<Chunk> removeList = new Array<Chunk>();

                chunkProvider.iterate(new Iterator() {
                    @Override
                    public void next(Chunk chunk) {
                        if (chunk.localX + 3 < minX ||
                            chunk.localX - 3 > maxX ||
                            chunk.localZ + 3 < minZ ||
                            chunk.localZ - 3 > maxZ) {
                            removeList.add(chunk);
                        }
                    }
                });

                for (int i = 0; i < removeList.size; ++i) {
                    final Chunk chunk = removeList.get(i);
                    chunkProvider.removeChunk(chunk);
                    try {
                        reentrantLock.lock();
                        postRunnables.add(new Runnable() {
                            @Override
                            public void run() {
                                world.remove(chunk);
                                chunk.dispose();
                            }
                        });
                    } finally {
                        reentrantLock.unlock();
                    }
                }
            }

            for (int i = minX; i <= maxX; ++i) {
                for (int j = minZ; j <= maxZ; ++j) {
                    final Chunk chunk = chunkProvider.getChunkRelative(i, j);
                    if (chunk.state[0] == false) {

                        runnables.add(new MyRunnable((int) MathHelper.len2(x, z, i, j)) {

                            @Override
                            public void run() {

                                chunk.state[0] = true;

                                int x1 = chunk.localX - 2;
                                int x2 = chunk.localX + 3;
                                int z1 = chunk.localZ - 2;
                                int z2 = chunk.localZ + 3;

                                int cols = (x2 - x1);
                                int rows = (z2 - z1);

                                Chunk[] chunks = new Chunk[cols * rows];
                                int i = 0;
                                // 1 .- generate data
                                for (int x = x1; x < x2; ++x) {
                                    for (int z = z1; z < z2; ++z) {
                                        Chunk c = chunkProvider.getChunkRelative(x, z);
                                        chunkDataGenerator.generate(c);
                                        chunks[i++] = c;
                                    }
                                }

                                for(Chunk c : chunks) {
                                   // if(c.state[0] == false) {
                                        chunkProvider.load(c);

                                  //  }
                                }

                                x1 = cols - 1;
                                z1 = rows - 1;

                                // 2 .- generate sun light
                                for (int x = 1; x < x1; ++x) {
                                    for (int z = 1; z < z1; ++z) {
                                        chunkLightGenerator.generateSunLight(chunks[x * rows + z]);
                                    }
                                }

                                // 3 .- spread lights
                                try {

                                    chunk.lock.lock();

                                    chunk.changeMap.iterate(new HashMap.EntryIterator() {
                                        @Override
                                        public void next(HashMap.Entry e) {
                                            short rawData = (Short) e.object;
                                            Block block = Blocks.values[rawData & 0xFF];
                                            if (block.lightEmmiter) {
                                                Vec3i position = ChunkData.indexToPosition((int) e.key);
                                                chunkLightGenerator.spreadBlockLight(chunk, chunk.worldX + position.x, position.y, chunk.worldZ + position.z, (byte) 14, new DataPointArray()); // TODO : cuanta luz ?as
                                            }
                                        }
                                    });

                                } finally {
                                    chunk.lock.unlock();
                                }

                                chunkLightGenerator.spreadSkyLight(chunk);

                                for (int x = 1; x < x1; ++x) {
                                    for (int z = 1; z < z1; ++z) {
                                        generateChunk(chunks[x * rows + z]);
                                    }
                                }
                            }

                        });
                    }
                }
            }

            threadPool.execute(runnables);
            runnables.clear();
            updating = false;

        } // run()

    };

    private void generateChunk(final Chunk chunk) {

        try {
            chunk.lock.lock();
            if(chunk.state[5] || chunk.isReady() == false) {
                return;
            }
            chunk.state[5] = true; // generated
        } finally {
            chunk.lock.unlock();
        }

        for (int i = chunk.maxHeight >> BIT_OFFSET; i >= 0; --i) {
            generateMesh(chunk.plots[i]).run();
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                world.add(chunk);
            }
        };

        try {
            reentrantLock.lock();
            postRunnables.add(runnable);
        } finally {
            reentrantLock.unlock();
        }

    }

    private final DataPointArray stack1 = new DataPointArray(1024);
    private final DataPointArray stack2 = new DataPointArray(1024);

    @Override
    public void addBlocksAt(short rawType, int worldX, int worldY, int worldZ) {

        synchronized (ChunkGeneratorImpl.this) {

            Chunk chunk = world.getChunkAbsolute(worldX, worldZ);

            int localX = worldX - chunk.worldX;
            int localZ = worldZ - chunk.worldZ;

            final int preIndex = localX * HD + localZ;
            final int index = preIndex + (worldY << BIT_OFFSET);

            chunk.changeMap.put(index, rawType);
            chunk.chunkData.setRawType(index, rawType);
            byte light;
            // Update height
            int height = chunk.chunkData.getHeightAt(preIndex) & 0xFF;
            if(height < worldY) { // add highest block
                chunk.chunkData.setHeightAt(preIndex, (byte) worldY);
                if(chunk.maxHeight < worldY) {
                    chunk.maxHeight = worldY;
                }
                chunkLightGenerator.clearSunLightColumn(chunk.chunkData, worldY, height, preIndex);
                chunkLightGenerator.unspreadSkyLightColumn(chunk, worldX, height, worldZ, localX * HD, localZ, Blocks.SUN_LIGHT, stack1, stack2);
                // Info: height is < than worldY ;)
            } else {
                light = chunk.chunkData.getSkyLight(index);
                chunk.chunkData.setSkyLight(index, Blocks.NULL_LIGHT);
                chunkLightGenerator.unspreadSkyLight(chunk, worldX, worldY, worldZ, light, stack1, stack2);
            }

            if(Blocks.values[rawType & 0xFF].lightEmmiter) {
                chunkLightGenerator.spreadBlockLight(chunk, worldX, worldY, worldZ, (byte) 14, stack1); // TODO : ¿Cuanta luz?
            } else if((light = chunk.chunkData.getBlockLight(index)) > 0) {
                chunk.chunkData.setBlockLight(index, Blocks.NULL_LIGHT);
                chunkLightGenerator.unspreadBlockLight(chunk, worldX, worldY, worldZ, light, stack1, stack2);
            }

        }

    }

    @Override
    public void removeBlocksAt(boolean trash, final int...points) {

        Array<Runnable> tmp = new Array<Runnable>();

        Chunk chunk = null;
        synchronized (ChunkGeneratorImpl.this) {

            for (int i = 0; i < points.length; i += 3) {
                final int worldX = points[i];
                final int worldY = points[i + 1];
                final int worldZ = points[i + 2];

                chunk = world.getChunkAbsolute(worldX, worldZ);

                int localX = worldX - chunk.worldX;
                int localZ = worldZ - chunk.worldZ;

                final int preIndex = localX * HD + localZ;
                final int index = preIndex + (worldY << BIT_OFFSET);

                final Block block = Blocks.values[chunk.chunkData.getBlockType(index)];
                if(block == Blocks.AIR) continue;;

                //chunk.state[3] = true;
                chunk.changeMap.put(index, (short) Blocks.AIR.id);

                // ACTION1 BLOCK
                //byte blockType = chunk.chunkData.getBlockType(index);

                chunk.chunkData.setBlockType(index, Blocks.AIR.id);

                final int height = chunk.chunkData.getHeightAt(preIndex) & 0xFF;
                int newHeight;
                if (height == worldY) { // breaks highest block
                    // UPDATE HEIGHT
                    for (int y = height - 1, j = index - SIZE; y > 0; j -= SIZE, --y) {
                        if ((chunk.chunkData.getBlockType(j)) != Blocks.AIR.id) {
                            chunk.chunkData.setHeightAt(localX, localZ, (byte) y);
                            break;
                        }
                    }

                    if (height == chunk.maxHeight) {
                        chunk.updateHeights();
                    }

                    newHeight = chunk.chunkData.getHeightAt(preIndex) & 0xFF;

                    // UPDATE SUN LIGHT COLUMN
                    // if (height == VERTICAL_SIZE - 1 || chunk.chunkData.getSkyLight(preIndex + ((height + 1) << BIT_OFFSET)) == Blocks.SUN_LIGHT) {
                    chunkLightGenerator.generateSunLightColumn(chunk.chunkData, worldY, newHeight, preIndex);
                    chunkLightGenerator.spreadSkyLightColumn(chunk, worldX, newHeight, worldZ, localX * HD, localZ, Blocks.SUN_LIGHT, stack1);
                    //}

                } else {

                    // Spread sky light **********************************************************************************************
                    Chunk c;
                    byte light = 0, tLight;
                    Blocks.Side side = null;
                    for(Blocks.Side s : Blocks.Side.values()) {
                        c = chunk.getChunkAbsolute(worldX + s.x, worldZ + s.z);
                        tLight = c.chunkData.getSkyLight((worldX + s.x) - c.worldX, worldY + s.y, (worldZ + s.z) - c.worldZ);
                        if(tLight > light) {
                            light = tLight;
                            side = s;
                        }
                    }

                    if(light > 0) {
                        c = chunk.getChunkAbsolute(worldX + side.x, worldZ + side.z);
                        chunkLightGenerator.spreadSkyLight(c, worldX + side.x, worldY + side.y, worldZ + side.z, light, stack1);
                    }

                }

                // Spread block light **********************************************************************************************
                if(block.lightEmmiter) {
                    chunkLightGenerator.unspreadBlockLight(chunk, worldX, worldY, worldZ, (byte) 14, stack1, stack2); // TODO : ¿Cuanta luz?
                } else {
                    byte light = 0, tLight;
                    Blocks.Side side = null;
                    Chunk c;
                    for(Blocks.Side s : Blocks.Side.values()) {
                        c = chunk.getChunkAbsolute(worldX + s.x, worldZ + s.z);
                        tLight = c.chunkData.getBlockLight((worldX + s.x) - c.worldX, worldY + s.y, (worldZ + s.z) - c.worldZ);
                        if(tLight > light) {
                            light = tLight;
                            side = s;
                        }
                    }
                    if(light > 0) {
                        c = chunk.getChunkAbsolute(worldX + side.x, worldZ + side.z);
                        chunkLightGenerator.spreadBlockLight(c, worldX + side.x, worldY + side.y, worldZ + side.z, light, stack1);
                    }
                }

                if(trash) {
                    tmp.add(new Runnable() { // ParticleTrash
                        @Override
                        public void run() {
                            float scl = 10F;
                            TextureCoordinates[] textureCoordinates = block.textureRegions[2].split(4, 4);
                            float size = (textureCoordinates[0].u2 - textureCoordinates[0].u) * scl;
                            ParticleTrash particle;
                            for (TextureCoordinates tc : textureCoordinates) {
                                particle = new ParticleTrash(worldX + 0.5F, worldY + 0.5F, worldZ + 0.5F, size, size, Settings.worldTexture, tc);
                                particle.scale = scl;
                                world.addEntity(particle);
                            }
                        }
                    });
                }

                for(Blocks.Side s : Blocks.Side.values()) {
                    short rawType = chunk.getRawTypeAbsolute(worldX + s.x, worldY + s.y, worldZ + s.z);
                    if(ChunkData.getType(rawType) == Blocks.WATER.id) {
                        world.addFluidPoint(new FluidPoint(worldX + s.x, worldY + s.y, worldZ + s.z, rawType, (byte) 7, world)); // TODO : rango maximo ?
                        break;
                    }
                }

            } // end for

            updateMeshes(chunk, tmp);

        }

    }

    @Override
    public void updateMeshes(final Chunk chunk, final Array<Runnable> runnables) {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                Chunk c;
                for (int x = -1; x < 2; ++x) {
                    for (int z = -1; z < 2; ++z) {
                        c = chunk.getChunkRelative(chunk.localX + x, chunk.localZ + z);
                        for (int j = c.maxHeight >> BIT_OFFSET; j >= 0; --j) {
                            runnables.add(generateMesh(c.plots[j]));
                        }
                    }
                }

                try {
                    reentrantLock.lock();
                    postRunnables.addAll(runnables);
                } finally {
                    reentrantLock.unlock();
                }
            }

        });

        Thread.yield();
    }

    private MeshContextGenerator generateMesh(Plot plot) {

        MeshContextGenerator mContextGen;

        synchronized (meshContextPool) {
            mContextGen = meshContextPool.obtain();
        }

        mContextGen.setPlot(plot);

        final MeshManager mOpaque  = mContextGen.mManagers[0];
        final MeshManager mBlended = mContextGen.mManagers[1];
        final MeshManager mCross   = mContextGen.mManagers[2];
        final MeshManager mLiquid  = mContextGen.mManagers[3];

        final ChunkData chunkData = plot.chunk.chunkData;

        final ChunkData data = chunkData;
        final ChunkData d0 = plot.chunk.neighbors[0].chunkData,
                        d1 = plot.chunk.neighbors[1].chunkData,
                        d2 = plot.chunk.neighbors[2].chunkData,
                        d3 = plot.chunk.neighbors[3].chunkData;

        final int worldX = plot.getWorldX();
        final int worldY = plot.getWorldY();
        final int worldZ = plot.getWorldZ();

        final int plotSize = Plot.SIZE;
        final int mask = plotSize - 1;

        final Block[] blocks = Blocks.values;

        MeshManager meshManager;
        short rawType;
        Block block;

        int height, preIndex, index;

        for (int x1 = 0, x2 = worldX; x1 < plotSize; ++x1, ++x2) {
            for (int z1 = 0, z2 = worldZ; z1 < plotSize; ++z1, ++z2) {
                preIndex = x1 * HD + z1;
                height = chunkData.getHeightAt(preIndex);
                for (int y1 = 0, y2 = worldY, i = y2 << BIT_OFFSET; y1 < SIZE && y2 <= height; ++y1, ++y2, i += SIZE) {

                    if (y2 > 0) { // FIXME : bedrock no se genera...

                        index = preIndex + i;
                        rawType = data.getRawType(index);
                        block = blocks[rawType & 0xFF];

                        if(block.renderMode == 1) {  // Opaque

                            //meshManager = null;

                            if (block.equals(blocks[data.getBlockType(index + plotSize)]) == false) { // TOP
                                //if(meshManager == null) {
                                meshManager = block.opaque ? mOpaque : mBlended;
                                chunkData.setReferencePoint(x1, y2, z1);
                                meshManager.begin(x2, y2, z2, rawType, chunkData);
                                // }
                                meshManager.createTop(chunkData);
                            } else {
                                meshManager = null;
                            }

                            if (y2 == 1 || block.equals(blocks[data.getBlockType(index - plotSize)]) == false) { // BOTTOM
                                if(meshManager == null) {
                                    meshManager = block.opaque ? mOpaque : mBlended;
                                    chunkData.setReferencePoint(x1, y2, z1);
                                    meshManager.begin(x2, y2, z2, rawType, chunkData);
                                }
                                meshManager.createBottom(chunkData);
                            }

                            if (z1 < mask) {

                                if (block.equals(blocks[data.getBlockType(index + 1)]) == false) {
                                    if(meshManager == null) {
                                        meshManager = block.opaque ? mOpaque : mBlended;
                                        chunkData.setReferencePoint(x1, y2, z1);
                                        meshManager.begin(x2, y2, z2, rawType, chunkData);
                                    }
                                    meshManager.createFront(chunkData);
                                }

                            } else if (block.equals(blocks[d2.getBlockType(index - z1)]) == false) {
                                if(meshManager == null) {
                                    meshManager = block.opaque ? mOpaque : mBlended;
                                    chunkData.setReferencePoint(x1, y2, z1);
                                    meshManager.begin(x2, y2, z2, rawType, chunkData);
                                }
                                meshManager.createFront(chunkData);
                            }

                            if (z1 > 0) {

                                if (block.equals(blocks[data.getBlockType(index - 1)]) == false) {
                                    if(meshManager == null) {
                                        meshManager = block.opaque ? mOpaque : mBlended;
                                        chunkData.setReferencePoint(x1, y2, z1);
                                        meshManager.begin(x2, y2, z2, rawType, chunkData);
                                    }
                                    meshManager.createBack(chunkData);
                                }

                            } else if (block.equals(blocks[d3.getBlockType(index + (mask - z1))]) == false) {
                                if(meshManager == null) {
                                    meshManager = block.opaque ? mOpaque : mBlended;
                                    chunkData.setReferencePoint(x1, y2, z1);
                                    meshManager.begin(x2, y2, z2, rawType, chunkData);
                                }
                                meshManager.createBack(chunkData);
                            }

                            if (x1 < mask) {

                                if (block.equals(blocks[data.getBlockType(index + HD)]) == false) {
                                    if(meshManager == null) {
                                        meshManager = block.opaque ? mOpaque : mBlended;
                                        chunkData.setReferencePoint(x1, y2, z1);
                                        meshManager.begin(x2, y2, z2, rawType, chunkData);
                                    }
                                    meshManager.createRight(chunkData);
                                }

                            } else if (block.equals(blocks[d0.getBlockType(index - (x1 * HD))]) == false) {
                                if(meshManager == null) {
                                    meshManager = block.opaque ? mOpaque : mBlended;
                                    chunkData.setReferencePoint(x1, y2, z1);
                                    meshManager.begin(x2, y2, z2, rawType, chunkData);
                                }
                                meshManager.createRight(chunkData);
                            }

                            if (x1 > 0) {

                                if (block.equals(blocks[data.getBlockType(index - HD)]) == false) {
                                    if(meshManager == null) {
                                        meshManager = block.opaque ? mOpaque : mBlended;
                                        chunkData.setReferencePoint(x1, y2, z1);
                                        meshManager.begin(x2, y2, z2, rawType, chunkData);
                                    }
                                    meshManager.createLeft(chunkData);
                                }

                            } else if (block.equals(blocks[d1.getBlockType(index + ((mask - x1) * HD))]) == false) {
                                if(meshManager == null) {
                                    meshManager = block.opaque ? mOpaque : mBlended;
                                    chunkData.setReferencePoint(x1, y2, z1);
                                    meshManager.begin(x2, y2, z2, rawType, chunkData);
                                }
                                meshManager.createLeft(chunkData);
                            }

                        } else if(block.renderMode == 2) { // fluid

                            mLiquid.yOff = (data.getBlockType(index + plotSize) != block.id) ? 0.11F : 0F;
                            if (block.equals(blocks[data.getBlockType(index + plotSize)]) == false) { // TOP
                                //if(meshManager == null) {
                                meshManager = mLiquid;
                                chunkData.setReferencePoint(x1, y2, z1);
                                meshManager.begin(x2, y2, z2, rawType, chunkData);
                                // }
                                meshManager.createTop(chunkData);
                            } else {
                                meshManager = null;
                            }

                            if (y2 == 1 || block.equals(blocks[data.getBlockType(index - plotSize)]) == false) { // BOTTOM
                                if(meshManager == null) {
                                    meshManager = mLiquid;
                                    chunkData.setReferencePoint(x1, y2, z1);
                                    meshManager.begin(x2, y2, z2, rawType, chunkData);
                                }
                                meshManager.createBottom(chunkData);
                            }

                            if (z1 < mask) {

                                if (block.equals(blocks[data.getBlockType(index + 1)]) == false) {
                                    if(meshManager == null) {
                                        meshManager = mLiquid;
                                        chunkData.setReferencePoint(x1, y2, z1);
                                        meshManager.begin(x2, y2, z2, rawType, chunkData);
                                    }
                                    meshManager.createFront(chunkData);
                                }

                            } else if (block.equals(blocks[d2.getBlockType(index - z1)]) == false) {
                                if(meshManager == null) {
                                    meshManager = mLiquid;
                                    chunkData.setReferencePoint(x1, y2, z1);
                                    meshManager.begin(x2, y2, z2, rawType, chunkData);
                                }
                                meshManager.createFront(chunkData);
                            }

                            if (z1 > 0) {

                                if (block.equals(blocks[data.getBlockType(index - 1)]) == false) {
                                    if(meshManager == null) {
                                        meshManager = mLiquid;
                                        chunkData.setReferencePoint(x1, y2, z1);
                                        meshManager.begin(x2, y2, z2, rawType, chunkData);
                                    }
                                    meshManager.createBack(chunkData);
                                }

                            } else if (block.equals(blocks[d3.getBlockType(index + (mask - z1))]) == false) {
                                if(meshManager == null) {
                                    meshManager = mLiquid;
                                    chunkData.setReferencePoint(x1, y2, z1);
                                    meshManager.begin(x2, y2, z2, rawType, chunkData);
                                }
                                meshManager.createBack(chunkData);
                            }

                            if (x1 < mask) {

                                if (block.equals(blocks[data.getBlockType(index + HD)]) == false) {
                                    if(meshManager == null) {
                                        meshManager = mLiquid;
                                        chunkData.setReferencePoint(x1, y2, z1);
                                        meshManager.begin(x2, y2, z2, rawType, chunkData);
                                    }
                                    meshManager.createRight(chunkData);
                                }

                            } else if (block.equals(blocks[d0.getBlockType(index - (x1 * HD))]) == false) {
                                if(meshManager == null) {
                                    meshManager = mLiquid;
                                    chunkData.setReferencePoint(x1, y2, z1);
                                    meshManager.begin(x2, y2, z2, rawType, chunkData);
                                }
                                meshManager.createRight(chunkData);
                            }

                            if (x1 > 0) {

                                if (block.equals(blocks[data.getBlockType(index - HD)]) == false) {
                                    if(meshManager == null) {
                                        meshManager = mLiquid;
                                        chunkData.setReferencePoint(x1, y2, z1);
                                        meshManager.begin(x2, y2, z2, rawType, chunkData);
                                    }
                                    meshManager.createLeft(chunkData);
                                }

                            } else if (block.equals(blocks[d1.getBlockType(index + ((mask - x1) * HD))]) == false) {
                                if(meshManager == null) {
                                    meshManager = mLiquid;
                                    chunkData.setReferencePoint(x1, y2, z1);
                                    meshManager.begin(x2, y2, z2, rawType, chunkData);
                                }
                                meshManager.createLeft(chunkData);
                            }

                        } else if(block.renderMode == 3) { // Crossed

                            chunkData.setReferencePoint(x1, y2, z1);
                            mCross.crossPlant(x2, y2, z2, rawType, chunkData);

                        }

                    }

                }

            }
        }

        return mContextGen;

    }

    private class MeshContextGenerator implements Runnable {

        final MeshManager[] mManagers;

        public MeshContextGenerator() {

            if(Settings.smoothLighting) {

                mManagers = new MeshManager[]{
                            new MeshManagerSmoothLighting(), // opaque
                            new MeshManagerSmoothLighting(), // blend
                            new MeshManagerFast(), 		     // cross
                            new MeshManagerFluid()};         // fluid
            } else {

                mManagers = new MeshManager[]{
                            new MeshManagerFast(),  // opaque
                            new MeshManagerFast(),  // blend
                            new MeshManagerFast(),  // cross
                            new MeshManagerFast()}; // fluid

            }

        }

        private Plot plot;

        public void setPlot(Plot plot) {
            this.plot = plot;
        }

        @Override
        public void run() {

            final Plot plot = this.plot;
            plot.rendering = false;

            plot.dispose();

            final FastMesh[] meshes = plot.meshes;
            final MeshManager[] mManagers = this.mManagers;
            MeshManager meshManager;
            for (int i = 0; i < 4; i++) {
                meshManager = mManagers[i];
                if (meshManager.iOff > 0) {
                    plot.rendering = true;
                    meshes[i] = meshManager.end();
                }
            }

            synchronized (meshContextPool) {
                meshContextPool.free(this);
            }

        }

    }

    @Override
    public ThreadPool getThreadPool() {
        return threadPool;
    }

    @Override
    public boolean isUpdating() {
        return updating;
    }

    @Override
    public ChunkProvider getChunkProvider() {
        return chunkProvider;
    }

    @Override
    public void dispose() {
        chunkProvider.dispose();
        executor.shutdown();
    }

}