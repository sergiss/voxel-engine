package org.delmesoft.crazyblocks.utils;

import com.badlogic.gdx.graphics.Pixmap;

public class PixmapUtils {

	public static Pixmap copyTiledPixmap(Pixmap srcPixmap, int cols, int rows, int margin, int padding) {

		int srcWidth  = srcPixmap.getWidth();
		int srcHeight = srcPixmap.getHeight();

		int stw  = srcWidth / cols;
		int sth  = srcHeight / rows;

		int dstWidth  = srcWidth  + cols * (margin << 1) + cols * (padding << 1);
		int dstHeight = srcHeight + rows * (margin << 1) + rows * (padding << 1);

		int dtw = dstWidth / cols;
		int dth = dstHeight / rows;

		Pixmap dstPixmap = new Pixmap(dstWidth, dstHeight, srcPixmap.getFormat());

		for(int x = 0; x < cols; x++) {

			int sx1 = x * stw;
			int dx1 = sx1 + margin + padding + x * (margin << 1) + x * (padding << 1);

			for(int y = 0; y < rows; y++) {				

				int sy1 = y * sth;
				int dy1 = sy1 + margin + padding + y * (margin << 1) + y * (padding << 1);

				dstPixmap.drawPixmap(srcPixmap, dx1, dy1, sx1, sy1, stw, sth);

			}
		}

		for(int x = 0; x < dstWidth; x++) {

			int relX = x - ((x / dtw) * dtw);

			for(int y = 0; y < dstHeight; y++) {

				int pixel = dstPixmap.getPixel(x, y);

				if(pixel == 0) {

					int relY = y - ((y / dth) * dth);

					if(y > 0 && relY < dth - margin && relY >= dth - (padding + margin)) {

						int nextPixel = dstPixmap.getPixel(x, y - 1);
						if(nextPixel != 0) {
							dstPixmap.drawPixel(x, y, nextPixel);
						}

					}

					if(x > 0 && relX < dtw - margin && relX >= dtw - (padding + margin)) {

						int nextPixel = dstPixmap.getPixel(x - 1, y);
						if(nextPixel != 0) {
							dstPixmap.drawPixel(x, y, nextPixel);
						}

					}

				}
			}
		}

		for(int x = dstWidth - 1; x >= 0; x--) {

			int relX = x - ((x / dtw) * dtw);

			for(int y = dstHeight - 1; y >= 0; y--) {

				int pixel = dstPixmap.getPixel(x, y);

				if(pixel == 0) {

					int relY = y - ((y / dth) * dth);

					if(y < dstHeight - 1 && relY < padding + margin && relY >= margin) {

						int nextPixel = dstPixmap.getPixel(x, y + 1);
						if(nextPixel != 0) {
							dstPixmap.drawPixel(x, y, nextPixel);
						}

					}

					if(x < dstWidth - 1 && relX < padding + margin && relX >= margin) {

						int nextPixel = dstPixmap.getPixel(x + 1, y);
						if(nextPixel != 0) {
							dstPixmap.drawPixel(x, y, nextPixel);
						}

					}

				}
			}
		}		
	
		return dstPixmap;
	}

}
