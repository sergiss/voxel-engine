package org.delmesoft.crazyblocks.world.blocks.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import org.delmesoft.crazyblocks.math.Vec3i;
import org.delmesoft.crazyblocks.screen.GameScreen;
import org.delmesoft.crazyblocks.utils.datastructure.HashMap.Entry;
import org.delmesoft.crazyblocks.utils.datastructure.HashMap.EntryIterator;
import org.delmesoft.crazyblocks.world.Settings;
import org.delmesoft.crazyblocks.world.blocks.Blocks;
import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.utils.generators.ChunkGenerator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class WorldIO {

	public static final String FOLDER = File.separator + "saves" + File.separator;
	private static final String chunkFormat = FOLDER + "%s" + File.separator + "c.%d.%d";
	private static final String infoFormat  = FOLDER + "%s" + File.separator + "world.dat";

	private static WorldIO instance;

	public static final WorldIO getInstance() {
		if(instance == null) instance = new WorldIO();
		return instance;
	}

	private String levelName;

	private WorldIO() {}

	public void loadChangedData(Chunk chunk) {

		try {

			chunk.lock.lock();

			if(chunk.state[2] == false) {

				int x = chunk.localX;
				int z = chunk.localZ;

				FileHandle fileHandle = Gdx.files.local(String.format(chunkFormat, levelName, x, z));

				if(fileHandle.exists()) {

					ChunkData data = chunk.chunkData;

					try {

						InputStream reader = fileHandle.read();

						int index;
						short blockType;

						byte[] buffer = WorldIO.buffer;

						while (reader.read(buffer, 0, 6) > 0) {
							index = buffer[0] << 24 | (buffer[1] & 0xFF) << 16 | (buffer[2] & 0xFF) << 8 | (buffer[3] & 0xFF);
							blockType = (short) (buffer[4] << 8 | (buffer[5] & 0xFF));
							chunk.changeMap.put(index, blockType);
							data.setRawType(index, blockType);
						}

						reader.close();

					} catch (IOException e) {
						e.printStackTrace(); // TODO : log
					}

				}

				chunk.state[2] = true; // update

			}

		} finally {
			chunk.lock.unlock();
		}

	}

	public void saveChangedData(Chunk chunk) {

		try {

			chunk.lock.lock();

			if (chunk.changeMap.size() == 0) return;

			int x = chunk.localX;
			int z = chunk.localZ;

			FileHandle fileHandle = Gdx.files.local(String.format(chunkFormat, levelName, x, z));

			try {

				final OutputStream writer = fileHandle.write(false);

				EntryIterator it = new EntryIterator() {

					@Override
					public void next(Entry entry) {

						try {
							writer.write(toByteArray((int) entry.key, (Short) entry.object), 0, 6);
						} catch (IOException e) {
							 e.printStackTrace(); // TODO : log
						}

					}
				};

				chunk.changeMap.iterate(it);

				writer.flush();
				writer.close();

			} catch (Exception e) {
				 e.printStackTrace(); // TODO : log
			}


		} finally {
			chunk.lock.unlock();
		}

	}

	public void saveWorldData() {

		FileHandle fileHandle = Gdx.files.local(String.format(infoFormat, levelName));

		try {

			final OutputStream writer = fileHandle.write(false);
			// Seed
			writer.write(longToBytes(Settings.seed), 0, 8);
			// player position
			writer.write(floatToBytes(GameScreen.instance.player.x), 0, 8);
			writer.write(floatToBytes(GameScreen.instance.player.y), 0, 8);
			writer.write(floatToBytes(GameScreen.instance.player.z), 0, 8);
			// camera direction
			writer.write(floatToBytes(GameScreen.instance.camera.direction.x), 0, 8);
			writer.write(floatToBytes(GameScreen.instance.camera.direction.y), 0, 8);
			writer.write(floatToBytes(GameScreen.instance.camera.direction.z), 0, 8);
			// world rotation
			writer.write(floatToBytes(GameScreen.instance.world.getEnvironment().getRotation()), 0, 8);

			writer.flush();
			writer.close();

		} catch (Exception e) {
			e.printStackTrace(); // TODO : log
		}

	}

	public void loadWorldData(String levelName) {

		this.levelName = levelName;

		FileHandle fileHandle = Gdx.files.local(String.format(infoFormat, levelName));

		if(fileHandle.exists()) {

			try {

				InputStream reader = fileHandle.read();

				byte[] buffer = WorldIO.buffer;

				// ChunkVisibility

				// fieldOfView

				// smoothLighting

				// Seed
				reader.read(buffer, 0, 8);
				Settings.seed = bytesToLong(buffer, 0);

				// Spawn point
				if(reader.read(buffer, 0, 24) == 24) {
					GameScreen.instance.player.x = bytesToFloat(buffer, 0);
					GameScreen.instance.player.y = bytesToFloat(buffer, 8);
					GameScreen.instance.player.z = bytesToFloat(buffer, 16);
				}
				// Camera direction
				if(reader.read(buffer, 0, 24) == 24) {
					GameScreen.instance.camera.direction.set(bytesToFloat(buffer, 0),
							bytesToFloat(buffer, 8),
							bytesToFloat(buffer, 16));
				}

				if(reader.read(buffer, 0, 8) == 8) {
					Settings.worldRotation = (bytesToFloat(buffer, 0));
				}

				reader.close();

			} catch (IOException e) {
				e.printStackTrace(); // TODO : log
			}

		}

	}

	public String getLevelName() {
		return levelName;
	}

	private static final byte[] buffer = new byte[32];

	public static byte[] toByteArray(int index, short data) {

		buffer[0] = (byte)(index >>> 24);
		buffer[1] = (byte)(index >>> 16);
		buffer[2] = (byte)(index >>> 8);
		buffer[3] = (byte) index;

		buffer[4] = (byte)(data >>> 8);
		buffer[5] = (byte) data;

		return buffer;
	}

	public static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(x);
		return buffer.array();
	}

	public static long bytesToLong(byte[] bytes, int offset) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.put(bytes, offset, 8);
		buffer.flip();//need flip
		return buffer.getLong();
	}

	public static byte[] floatToBytes(float x) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putFloat(x);
		return buffer.array();
	}

	public static float bytesToFloat(byte[] bytes, int offset) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.put(bytes, offset, 8);
		buffer.flip();//need flip
		return buffer.getFloat();
	}

}
