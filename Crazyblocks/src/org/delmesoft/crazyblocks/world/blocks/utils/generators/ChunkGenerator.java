package org.delmesoft.crazyblocks.world.blocks.utils.generators;

import com.badlogic.gdx.utils.Disposable;

import org.delmesoft.crazyblocks.utils.datastructure.Array;
import org.delmesoft.crazyblocks.utils.threads.ThreadPool;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.ChunkProvider;
import org.delmesoft.crazyblocks.world.blocks.utils.DataPoint;
import org.delmesoft.crazyblocks.world.blocks.utils.DataPointArray;

public interface ChunkGenerator extends Disposable {

	void update(int x, int z);

	void handlePostRunnables();

	void removeBlocksAt(boolean trash, int...points);

	void addBlocksAt(short rawType, int worldX, int worldY, int worldZ);

	void updateMeshes(Chunk chunk, Array<Runnable> runnables);

	ThreadPool getThreadPool();
	
	boolean isUpdating();

    ChunkProvider getChunkProvider();

}
