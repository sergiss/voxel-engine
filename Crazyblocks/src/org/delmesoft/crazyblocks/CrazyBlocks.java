
/*
 * Copyright (c) 2020, Sergio S.- sergi.ss4@gmail.com http://sergiosoriano.com
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *    	
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 */
package org.delmesoft.crazyblocks;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;

import org.delmesoft.crazyblocks.screen.AbstractScreen;
import org.delmesoft.crazyblocks.screen.GameScreen;

public class CrazyBlocks implements ApplicationListener {

	public static final String VERSION = "0.2.5";
	
	private static CrazyBlocks instance;

	public static CrazyBlocks getInstance() {

		if(instance == null) {
			instance = new CrazyBlocks();
		}

		return instance;
	}

	public AbstractScreen screen;
	
	private CrazyBlocks() {}

	@Override
	public void create() {

		System.out.printf("CrazyBlocks v%s\n", VERSION);
				
		screen = new GameScreen();
		screen.create();	
	}

	@Override
	public void render() {

		final Graphics graphics = Gdx.graphics;
		
		graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		screen.update(graphics.getDeltaTime());

		screen.render();

		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}

	}

	@Override
	public void resize(int width, int height) {
		screen.resize(width, height);
	}

	@Override
	public void pause() {
		
		screen.setPaused(true);
		
		if(screen instanceof GameScreen) { // TODO 
			GameScreen gScreen = (GameScreen) screen;
			gScreen.world.save();
		}		
		
	}

	@Override
	public void resume() {
		screen.setPaused(false);
	}

	@Override
	public void dispose() {
		screen.dispose();
	}

}
