package org.delmesoft.crazyblocks.world.blocks.g3d.environment;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

import org.delmesoft.crazyblocks.graphics.g3d.FastMesh;
import org.delmesoft.crazyblocks.graphics.g3d.MeshBuilder;
import org.delmesoft.crazyblocks.graphics.g3d.ShaderProgram;
import org.delmesoft.crazyblocks.world.Settings;
import org.delmesoft.crazyblocks.world.blocks.Chunk;
import org.delmesoft.crazyblocks.world.blocks.Plot;
import org.delmesoft.crazyblocks.world.blocks.utils.noise.PerlinNoise;

/**
 * Created by sergi on 3/09/17.
 */

public class Cloud {

    public static final Matrix4 CLOUD_TRANS = new Matrix4();
    public static final Matrix4 TMP = new Matrix4();
    public static final int CLOUD_PARTS = 4;
    public static final int CLOUD_PART_SIZE = Plot.SIZE / CLOUD_PARTS;
    public static final int CLOUD_SIZE = CLOUD_PARTS * CLOUD_PART_SIZE;

    public static int lightLocation;
    public static int alphaLocation;
    public static int projTransLocation;

    public static String vertexShader =
                    "attribute vec3 a_position;    \n" +
                    "uniform mat4 u_projTrans;     \n" +
                    "uniform float u_light;        \n" +
                    "uniform float u_alpha;        \n" +
                    "varying vec4 v_color; 		   \n" +
                    "void main() {				   \n" +
                    "	v_color = vec4(u_light, u_light, u_light, u_alpha); \n" +
                    "   gl_Position = u_projTrans * vec4(a_position, 1.0);  \n" +
                    "}" ;

    public static String fragmentShader =
                    "#ifdef GL_ES			     \n" +
                    "precision mediump float;    \n" +
                    "#endif					     \n" +
                    "varying vec4 v_color;  	 \n" +
                    "void main() {				 \n" +
                    "  gl_FragColor = v_color;   \n" +
                    "}";

    private FastMesh mesh;

    public float x, z;

    public Cloud(float x, float z, PerlinNoise perlinNoise) {
        this.x = x;
        this.z = z;
        generateMesh(perlinNoise);
    }

    public void render(Camera camera, ShaderProgram program, float xa) {

        float halfSize = Settings.chunkVisibility * CLOUD_SIZE;

        float camX = camera.position.x;
        float camZ = camera.position.z;

        float minX = camX - halfSize;
        float maxX = camX + halfSize;
        float minZ = camZ - halfSize;
        float maxZ = camZ + halfSize;

        x += xa;

        if(x > maxX) {
            x = minX + (x - maxX);
        } else if(x < minX) {
            x = maxX - (minX - x);
        }

        if(z > maxZ) {
            z = minZ + (z - maxZ);
        } else if(z < minZ) {
            z = maxZ - (minZ - z);
        }

        program.setUniformMatrix(projTransLocation, CLOUD_TRANS.set(camera.combined).mul(TMP.setToTranslation(x, Chunk.VERTICAL_SIZE, z)));

        program.setUniformf(alphaLocation, (1F - Math.min(Vector2.dst2(camX, camZ, x, z) / (halfSize * halfSize), 1F)) * 0.5F);

        mesh.render(program);

    }

    public void generateMesh(PerlinNoise perlinNoise) {

        MeshBuilder meshBuilder = new MeshBuilder(new VertexAttributes(VertexAttribute.Position()));
        short iOff = 0;

        for(int x = 0, n = CLOUD_PARTS; x < n; x++) {
            float minX = x * CLOUD_PART_SIZE;
            float maxX = minX + CLOUD_PART_SIZE;
            for(int z = 0; z < n; z++) {
                float noise = perlinNoise.noise((x * CLOUD_PART_SIZE + this.x) * 0.025F, 0, (z * CLOUD_PART_SIZE + this.z) * 0.025F);
                if(noise < -0.2F) {
                    float minZ = z * CLOUD_PART_SIZE;
                    float maxZ = minZ + CLOUD_PART_SIZE;
                    meshBuilder.vertex(minX, 0, minZ, maxX, 0, minZ, maxX, 0, maxZ, minX, 0, maxZ);
                    meshBuilder.index(iOff, (short) (iOff + 3), (short) (iOff + 2), (short) (iOff + 2), (short) (iOff + 1), iOff);
                    iOff += 4;
                }
            }
        }

        this.mesh = meshBuilder.end();

    }

    public boolean isEmpty() {
        return mesh.isEmpty();
    }

    public void dispose() {
        mesh.dispose();
    }

}



