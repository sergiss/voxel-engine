package org.delmesoft;

import org.delmesoft.crazyblocks.CrazyBlocks;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	
	public static void main(String[] args) {
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();

		cfg.title = "Level3dTest";
		cfg.resizable = false;
		cfg.fullscreen = false;		

		setResolution(ScreenMode.S_840X480, cfg);
		
		new LwjglApplication(CrazyBlocks.getInstance(), cfg);

	}

	private enum ScreenMode{

		S_1600X900, S_1366X768, S_840X480, S_320X240;

	}
	private static void setResolution(ScreenMode screenMode, LwjglApplicationConfiguration cfg){

		switch (screenMode) {
		case S_320X240:
			cfg.width = 320;
			cfg.height = 240;
			break;
		case S_840X480:
			cfg.width = 840;
			cfg.height = 480;
			break;
		case S_1366X768:
			cfg.width = 1366;
			cfg.height = 768;
			break;
		case S_1600X900:
			cfg.width = 1600;
			cfg.height = 900;
			break;
		default:
			break;		
		}

	}
	
}