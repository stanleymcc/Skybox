package finalProj;

import com.jogamp.opengl.GL2;

public class Skybox {
	public static final int numFaces = 6;
	public static final String path = "/Users/Stan's/workspace/Final Project/src/finalProj/textures/";
	public static final String[] skyboxSideLabels = {
		"frontImage.png", "backImage.png",
		"leftImage.png", "rightImage.png",
		"upImage.png", "downImage.png"
	};
	
	protected TextureLoader texture_loader = null;
	private int[] textures = new int[numFaces];
	
	public Skybox(TextureLoader texture_loader, String skybox_name) {
		this.texture_loader = texture_loader;
		loadTextures();
	}
	
	protected void loadTextures() {
		
		for (int i = 0; i < numFaces; ++i) {
			textures[i] = texture_loader.generateTexture();
			
			try {
				texture_loader.loadTexture(textures[i], path + skyboxSideLabels[i]);
			} catch (Exception e) {
				System.err.println("Unable to load texture: " + e.getMessage());
			}
		}
	}
	
	public void drawSky(GL2 gl, float size) {
		final float d = size / 2.0f;
		gl.glDepthMask(false);
		// Front
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[0]);
		gl.glBegin(GL2.GL_QUADS);
		
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(d, -d, d);
		
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(d, -d, -d);
		
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(d, d, -d);
		
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(d, d, d);
		
		gl.glEnd();
		
		// Back
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[1]);
		gl.glBegin(GL2.GL_QUADS);
		
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-d, d, d);
		
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-d, d, -d);
		
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-d, -d, -d);
		
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-d, -d, d);
		
		gl.glEnd();
		
		// Left
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[2]);
		gl.glBegin(GL2.GL_QUADS);
		
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(d, d, d);
		
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(d, d, -d);
		
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-d, d, -d);
		
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-d, d, d);
		
		gl.glEnd();
		
		// Right
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[3]);
		gl.glBegin(GL2.GL_QUADS);
		
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-d, -d, d);
		
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-d, -d, -d);
		
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(d, -d, -d);
		
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(d, -d, d);
		
		gl.glEnd();
		
		// Up
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[4]);
		gl.glBegin(GL2.GL_QUADS);
		
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(-d, -d, d);
		
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(d, -d, d);
		
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(d, d, d);
		
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(-d, d, d);
		
		gl.glEnd();
		
		// Down
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textures[5]);
		gl.glBegin(GL2.GL_QUADS);
		
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3f(d, -d, -d);
		
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3f(-d, -d, -d);
		
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(-d, d, -d);
		
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3f(d, d, -d);
		
		gl.glEnd();
		gl.glDepthMask(true);
	}
}