package org.delmesoft.crazyblocks.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;

import org.delmesoft.crazyblocks.entity.Entity;
import org.delmesoft.crazyblocks.entity.Player;
import org.delmesoft.crazyblocks.graphics.Renderer;
import org.delmesoft.crazyblocks.graphics.g3d.decal.DecalRenderer;
import org.delmesoft.crazyblocks.math.MathHelper;
import org.delmesoft.crazyblocks.math.Vec3i;
import org.delmesoft.crazyblocks.screen.GameScreen;
import org.delmesoft.crazyblocks.utils.datastructure.Array;
import org.delmesoft.crazyblocks.utils.datastructure.ChunkMap;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Blocks.Block;
import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.Plot;
import org.delmesoft.crazyblocks.world.blocks.g3d.ChunkRenderer;
import org.delmesoft.crazyblocks.world.blocks.g3d.ChunkRendererImpl;
import org.delmesoft.crazyblocks.world.blocks.g3d.environment.Environment;
import org.delmesoft.crazyblocks.world.blocks.utils.generators.ChunkGenerator;
import org.delmesoft.crazyblocks.world.blocks.utils.generators.ChunkGeneratorImpl;
import org.delmesoft.crazyblocks.world.blocks.utils.generators.FluidPoint;

import java.util.Comparator;

import static org.delmesoft.crazyblocks.math.MathHelper.fastFloor;
import static org.delmesoft.crazyblocks.world.blocks.Chunk.HD;
import static org.delmesoft.crazyblocks.world.blocks.Chunk.VERTICAL_SIZE;
import static org.delmesoft.crazyblocks.world.blocks.Plot.BIT_OFFSET;
import static org.delmesoft.crazyblocks.world.blocks.Plot.SIZE;

public class World implements Renderer, Disposable {

    public static final float TIME_STEP = 1F / 60F;

    public static final float GRAVITY  = 0.016F;
    public static final float FRICTION = 0.99F;

    final GameScreen screen;

    final Array<Entity> entities;

    public final ChunkGenerator chunkGenerator;

    private RenderContext context;
    private ChunkRenderer chunkRenderer;

    private DecalRenderer decalRenderer;

    private float accumulator;

    private Environment environment;

    private final ChunkMap chunkMap;
    private final Array<Chunk> renderList;
    private final Array<FluidPoint> fluids;

    private int x, z;

    private boolean ready;

    public World(GameScreen screen) {

        this.screen = screen;

        chunkMap = new ChunkMap();
        renderList = new Array<Chunk>();

        entities = new Array<Entity>();

        chunkGenerator = new ChunkGeneratorImpl(this);

        context = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));
        chunkRenderer = new ChunkRendererImpl(context);

        chunkRenderer.setTexture(Settings.worldTexture);

        environment = new Environment(this);

        decalRenderer = new DecalRenderer(context);

        fluids = new Array<FluidPoint>();

    }

    public void update(float delta) {

        if(ready) {

            final Array<Entity> entities = this.entities;

            // Update physics
            float frameTime =  delta < 0.25F ? delta : 0.25F;
            accumulator += frameTime;
            while (accumulator >= TIME_STEP) {
                for (int i = 0, n = entities.size; i < n; i++) {
                    entities.get(i).tick(TIME_STEP);
                }
                accumulator -= TIME_STEP;
            }

            final Vector3 cameraPos = screen.camera.position;

            int x = fastFloor(cameraPos.x) >> BIT_OFFSET;
            int z = fastFloor(cameraPos.z) >> BIT_OFFSET;

            if(x != this.x || z != this.z) {

                this.x = x;
                this.z = z;

                // TODO : add
                chunkGenerator.update(x, z);
            }
            synchronized (fluids) {
                for (int i = 0; i < fluids.size; i++) {
                    fluids.get(i).update(delta);
                }
            }

        }

        chunkGenerator.handlePostRunnables();
        //renderList.sort(comparator);

    }

    private Comparator<Chunk> comparator = new Comparator<Chunk>() {
        @Override
        public int compare(Chunk c1, Chunk c2) {
            float d1 = MathHelper.len2(x, z, c1.localX, c1.localZ);
            float d2 = MathHelper.len2(x, z, c2.localX, c2.localZ);
            return Float.compare(d1, d2);
        }
    };

    public void add(Chunk chunk) {
        chunkMap.put(chunk.hashCode(), chunk);
        renderList.add(chunk);
    }

    public void remove(Chunk chunk) {
        chunkMap.removeKey(chunk.hashCode());
        renderList.removeValue(chunk);
        // TODO : remove entities
    }

    public void render(final Camera camera) {

        if(ready) {

            final ChunkRenderer chunkRenderer = this.chunkRenderer;

            context.begin();

            environment.renderSky(camera, context); // Sky

            chunkRenderer.begin(environment.sunLight, camera);
            Plot[] plots; Plot plot;
            Chunk chunk;
            for(int i = 0; i < renderList.size; i++) {
                chunk = renderList.get(i);
                plots = chunk.plots;
                for (int j = chunk.maxHeight >> Plot.BIT_OFFSET; j >= 0; --j) {
                    plot = plots[j];
                    if (plot.rendering && plot.isVisible(camera.frustum.planes))  {
                        chunkRenderer.render(plot);
                    }
                }
            }
            chunkRenderer.end();

            environment.renderClouds(camera, context); // Clouds

            decalRenderer.begin(camera);

            // environment.renderRain(camera, this);

            // Render entities
            final Array<Entity> entities = this.entities;
            for (int i = 0; i < entities.size; i++) {
               entities.get(i).render(this);
            }
            decalRenderer.end();
            context.end();

        }

    }

    public short isContactWith(float cx, float cy, float cz, float width, float height, float depth, byte type) {

        final Block[] values = Blocks.values;

        Chunk chunk;
        int index;

        int y0 = Math.max(1, (int) (cy)) << BIT_OFFSET;
        int y1 = Math.min(VERTICAL_SIZE, (int) (cy + height) + 1) << BIT_OFFSET;

        int x0 = fastFloor(cx);
        int x1 = fastFloor(cx + width) + 1;
        int z0 = fastFloor(cz);
        int z1 = fastFloor(cz + depth) + 1;

        short rawData;
        for (int x = x0; x < x1; x++) {
            for (int z = z0; z < z1; z++) {

                chunk = getChunkAbsolute(x, z);

                if(chunk != null) {
                    index = (x - chunk.worldX) * HD + z - chunk.worldZ;
                    for (int y = y0; y < y1; y += SIZE) {
                        rawData = chunk.chunkData.getRawType(index + y);
                        if ((rawData & 0xFF) == type) {
                            return rawData;
                        }
                    }
                }

            }
        }

        return Blocks.AIR.id;

    }

    public boolean isFree(float cx, float cy, float cz, float width, float height, float depth) {

        final Block[] values = Blocks.values;

        Chunk chunk;
        int index;

        int y0 = Math.max(1, (int) (cy)) << BIT_OFFSET;
        int y1 = Math.min(VERTICAL_SIZE, (int) (cy + height) + 1) << BIT_OFFSET;

        int x0 = fastFloor(cx);
        int x1 = fastFloor(cx + width) + 1;
        int z0 = fastFloor(cz);
        int z1 = fastFloor(cz + depth) + 1;

        for (int x = x0; x < x1; x++) {
            for (int z = z0; z < z1; z++) {

                chunk = getChunkAbsolute(x, z);

                if(chunk != null) {
                    index = (x - chunk.worldX) * HD + z - chunk.worldZ;
                    for (int y = y0; y < y1; y += SIZE) {
                        if (values[chunk.chunkData.getBlockType(index + y)].renderMode == 1) {
                            return false;
                        }
                    }
                }

            }
        }

        return true;
    }

    public Block ray(float originX, float originY, float originZ, float directionX, float directionY, float directionZ, float radius, Vec3i store) {

        final Block[] vals = Blocks.values;

        int stepX = 1, x = fastFloor(originX);
        float tMaxX, tDeltaX;

        if (directionX != 0F) {

            float invDirX = 1F / directionX;
            if (directionX > 0F) {
                tMaxX = ((x + 1F) - originX) * invDirX;
            } else {
                tMaxX = (x - originX) * invDirX;
                stepX = -1;
            }

            tDeltaX = stepX * invDirX;

        } else {
            tMaxX = Float.MAX_VALUE;
            tDeltaX = 0F;
        }

        int stepY = 1, y = fastFloor(originY);

        float tMaxY, tDeltaY;

        if (directionY != 0F) {

            float invDirY = 1F / directionY;
            if (directionY > 0F) {
                tMaxY = ((y + 1F) - originY) * invDirY;
            } else {
                tMaxY = (y - originY) * invDirY;
                stepY = -1;
            }
            tDeltaY = stepY * invDirY;

        } else {
            tMaxY = Float.MAX_VALUE;
            tDeltaY = 0F;
        }

        int stepZ = 1, z = fastFloor(originZ);
        float tMaxZ, tDeltaZ;

        if (directionZ != 0F) {

            float invDirZ = 1F / directionZ;
            if (directionZ > 0F) {
                tMaxZ = ((z + 1F) - originZ) * invDirZ;
            } else {
                tMaxZ = (z - originZ) * invDirZ;
                stepZ = -1;
            }
            tDeltaZ = stepZ * invDirZ;

        } else {
            tMaxZ = Float.MAX_VALUE;
            tDeltaZ = 0F;
        }

        int endX = fastFloor(originX + directionX * radius);
        int endY = fastFloor(originY + directionY * radius);
        int endZ = fastFloor(originZ + directionZ * radius);

        while (x != endX || y != endY || z != endZ) {

            if (tMaxX < tMaxY) {

                if (tMaxX < tMaxZ) {

                    tMaxX += tDeltaX;
                    x += stepX;

                } else {

                    tMaxZ += tDeltaZ;
                    z += stepZ;
                }

            } else {

                if (tMaxY < tMaxZ) {

                    tMaxY += tDeltaY;
                    y += stepY;

                } else {

                    tMaxZ += tDeltaZ;
                    z += stepZ;
                }

            }

            if(y >= 1 && y < VERTICAL_SIZE) {

                final Chunk chunk = getChunkAbsolute(x, z);

                if(chunk == null) return Blocks.AIR;

                Block block = vals[chunk.chunkData.getBlockType(x - chunk.worldX, y, z - chunk.worldZ)];

                if (block.renderMode > 0 && block.renderMode != 2) {
                    if(store != null) {
                        store.set(x, y, z);
                    }
                    return block;
                }

            }

        }

        return Blocks.AIR;
    }

    public Chunk getChunkAbsolute(int x, int z) {
        return chunkMap.get(Chunk.hashCode(x >> BIT_OFFSET, z >> BIT_OFFSET));
    }

    public void addEntity(final Entity entity) {

        if(!entities.contains(entity)) {
            entities.add(entity);

            entity.world = this;

            if(entity instanceof Player) {

                this.x = fastFloor(entity.x) >> BIT_OFFSET;
                this.z = fastFloor(entity.z) >> BIT_OFFSET;

                chunkGenerator.update(x, z);

                chunkMap.setDistinctListener(new ChunkMap.DistinctListener() {
                    @Override
                    public void event(int size) {

                        if (size >= 32) {
                            if(screen.player.y <= 0) {
                                int wx = fastFloor(entity.x);
                                int wz = fastFloor(entity.z);
                                Chunk chunk = getChunkAbsolute(wx, wz);
                                entity.y = (chunk.chunkData.getHeightAt(wx - chunk.worldX, wz - chunk.worldZ) & 0xFF) + 2;
                            }
                            ready = true;
                            chunkMap.setDistinctListener(null);
                        }

                    }
                });

            }

        }

    }

    public boolean removeEntity(Entity entity) {
        return entities.removeValue(entity);
    }

    public Array<Entity> getEntities() {
        return entities;
    }

    public void save() {
      // chunkGenerator.getChunkProvider().save();
    }

    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public void dispose() {
        entities.clear();
        chunkRenderer.dispose();
        chunkGenerator.dispose();
    }

    public synchronized void addFluidPoint(FluidPoint fluidPoint) {
        fluids.add(fluidPoint);
    }

    @Override
    public ModelBatch getModelBatch() {
        return null;
    }

    @Override
    public DecalRenderer getDecalRenderer() {
        return decalRenderer;
    }

    @Override
    public RenderContext getRenderContext() {
        return context;
    }

    @Override
    public Camera getCamera() {
        return screen.camera;
    }

    @Override
    public float getAmbientLight() {
        return environment.sunLight;
    }

    public void addBlocksAt(short rawType, int x, int y, int z) {

        chunkGenerator.addBlocksAt(rawType, x, y, z);
        Chunk chunk = getChunkAbsolute(x, z);
        if(chunk != null) {
            chunkGenerator.updateMeshes(chunk, new Array<Runnable>());
        }

    }
}
