package org.delmesoft.crazyblocks.world;

import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

import org.delmesoft.crazyblocks.entity.Player;

public class Settings {

	public static final int DEFAULT_CHUNK_VISIBILITY = 10; // 2, 3, 4, 6, 8, 10, 16

	public static int chunkVisibility = DEFAULT_CHUNK_VISIBILITY;

	// Camera
	public static float fieldOfView = 70f;

	public static Texture worldTexture;

	public static String levelName = "CrazyBlocks";

	public static long seed = generateSeed();

	public static float worldRotation = 90;

	public static boolean smoothLighting = true;

	public static boolean android;

	public static long generateSeed() {
		long seed = MathUtils.random.nextLong();
		return seed;
	}

	// Inputs
	public static int leftKey    = Keys.A;
	public static int rightKey   = Keys.D;
	public static int upKey      = Keys.W;
	public static int downKey    = Keys.S;
	public static int jumpKey    = Keys.SPACE;
	public static int action1Key = Buttons.LEFT;
	public static int action2Key = Buttons.RIGHT;
	public static int action3Key = Keys.SHIFT_LEFT;

}