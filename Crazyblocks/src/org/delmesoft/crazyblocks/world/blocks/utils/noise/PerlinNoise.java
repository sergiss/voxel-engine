package org.delmesoft.crazyblocks.world.blocks.utils.noise;

import java.util.Random;

/**
 * Created by sergi on 22/08/17.
 */

public interface PerlinNoise {

    float noise(float x);

    float noise(float x, float y);

    float fractal(float x, float y, int octaves);

    float noise(float x, float y, float z);

    Random getRandom();

}
