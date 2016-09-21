package finalProj;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.jogamp.opengl.*;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.awt.TextRenderer;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import org.lwjgl.util.vector.Vector3f;
import utility.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class JoglEventListener implements GLEventListener, KeyListener, MouseListener, MouseMotionListener {
	private int windowWidth, windowHeight;
	
	File shipModel = new File("src/finalProj/models/ship.obj");
	File asteroidModel = new File("src/finalProj/models/asteroid.obj");
	File checkpointModel = new File("src/finalProj/models/checkpoint.obj");
	File consoleModel = new File("src/finalProj/models/console.obj");
	File collider = new File("src/finalProj/models/sphereCollider.obj");
	
	public TextureLoader textureLoader = null;
	private final float skyboxSize = 3000.0f;
	private final String skyName = "DeepSpaceBlueWithPlanet";
	private Skybox skybox = null;
	public int texID[]  = new int[4];
	private float[] velocity = {0,0,0};
	private float[] acceleration = {0,0,0};
	private float maxVelocity = 3.0f;
	private float maxAcceleration = 0.8f;
	
	// Define starting position for player and direction they are looking
	private float cameraX = 0.0f;
	private float cameraY = 0.0f;
	private float cameraZ = 0.0f;
	private float lookDirectionX = 1.0f;
	private float lookDirectionY = 0.0f;
	private float lookDirectionZ = 0.0f;
	
	// Variables used for mouse operations
	private int mouse_x0 = 0;
	private int mouse_y0 = 0;
	private boolean mouseLookEnabled = false;
	
	// Variables to define keys
	private boolean[] keys = new boolean[256];
	private boolean hud = true; 
	private GLU glu = new GLU();
	
	//Gate Count
	private int total_gates = 6; 
	private int Captured_Gates = 0;
	private int Gates_Left = total_gates - Captured_Gates; 
	
	//Displaying stuff
	private boolean ship = true; 
	
	//Time
	 private long start = System.currentTimeMillis(); 
	 private long end; 
	 private long total;
	 
	 // Max and min values for asteroid coordinates
	 int numAsteroids = 100;
	 public float xMin = -200;
	 public float yMin = -200;
	 public float zMin = -200;
	 public float xMax = 200;
	 public float yMax = 200;
	 public float zMax = 200;
	 public float scaleMin = 0.2f;
	 public float scaleMax = 5.0f;
	 
	 
	 public JFrame mainFrame;
	 	
	 public List<Vector4f> ast = new ArrayList<Vector4f>();
	 public float[][] randomIncrementCoords = new float[3][10];
	 public float[] incrementAngles = new float[numAsteroids]; 
		
	 
	 public List<Vector3f> positions = new ArrayList<Vector3f>();
	 private boolean Game_Ended = false; 
	 private boolean win = false; 
	 
	public void displayChanged(GLAutoDrawable gLDrawable, boolean modeChanged, boolean deviceChanged){
		
	}

	@Override
	public void init(GLAutoDrawable gLDrawable) {
		
		GL2 gl = gLDrawable.getGL().getGL2();
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glEnable(GL.GL_TEXTURE_2D);
		
		// Initialize texture loader and skybox.
		textureLoader = new TextureLoader(gl);
		skybox = new Skybox(textureLoader, skyName);
		
		// Load HUD textures
		try {
			gl.glGenTextures(texID.length, texID, 0);
			textureLoader.loadTexture(texID[0], "src/finalProj/textures/picture-18.png");
			textureLoader.loadTexture(texID[1], "src/finalProj/textures/ship_fighter.jpg");
			textureLoader.loadTexture(texID[2], "src/finalProj/textures/asteroid.png");
			textureLoader.loadTexture(texID[3], "src/finalProj/textures/checkpoint.png");
		} 
		catch (IOException e1){
			e1.printStackTrace();
		}
		catch (InterruptedException e1){
			e1.printStackTrace();
		}
		
		// Initialize the keys.
		for (int i = 0; i < keys.length; ++i)
			keys[i] = false;
		
		// Generate coordinates for random asteroids
		for(int i = 0; i < numAsteroids; i++)
		{           
		   float x = xMin + (float)(Math.random() * ((xMax - xMin) + 1));
		   float y = yMin + (float)(Math.random() * ((yMax - yMin) + 1));
		   float z = zMin + (float)(Math.random() * ((zMax - zMin) + 1));
	       ast.add(new Vector4f(x,y,z,20.0f));      
		}
		 positions.add(new Vector3f(55,-62,-2));
			positions.add(new Vector3f(60,-5,-2));
			positions.add(new Vector3f(10,31,-100));
			positions.add(new Vector3f(100,12,2));
			positions.add(new Vector3f(-30,-45,2));
			positions.add(new Vector3f(-100,-22,-2));
		
		for(int i = 0; i < 10; i++){
			incrementAngles[i] += 20;
		}
		
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glLoadIdentity();
		 
		/*if(win == false && Game_Ended == true)
		{
			end(); 
		}
		else if(win == true && Game_Ended == true); 
		{
			win(); 
		}*/
		
	}
	
	@Override
	public void reshape(GLAutoDrawable gLDrawable, int x, int y, int width, int height) {
		windowWidth = width;
		windowHeight = height > 0 ? height : 1;
		
		final GL2 gl = gLDrawable.getGL().getGL2();
		
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(60.0f, (float) windowWidth / windowHeight, 0.1f, skyboxSize * (float) Math.sqrt( 3.0 ) / 2.0f);
	}

	@Override
	public void display(GLAutoDrawable gLDrawable) {
		final GL2 gl = gLDrawable.getGL().getGL2();
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
		gl.glPushMatrix();
		
		float forNormxy = 1;
		
		if(keys[KeyEvent.VK_Z]){
			allStop(velocity);
		}
		
		if(keys[KeyEvent.VK_W] || keys[KeyEvent.VK_S] || keys[KeyEvent.VK_A] || keys[KeyEvent.VK_D] || keys[KeyEvent.VK_R] || keys[KeyEvent.VK_F]){
			// Get user input and update camera position values	
			if (keys[KeyEvent.VK_W] || keys[KeyEvent.VK_S]) {
				
				forNormxy = (float) Math.sqrt((lookDirectionX * lookDirectionX) + (lookDirectionY * lookDirectionY));
				
				if(keys[KeyEvent.VK_W]){
					acceleration[0] += 0.001;
				
					if(acceleration[0] > maxAcceleration)
						acceleration[0] = maxAcceleration;
					else if(acceleration[0] < -maxAcceleration)
						acceleration[0] = -maxAcceleration;
					
					velocity[0] += acceleration[0];
					
					if(velocity[0] > maxVelocity)
						velocity[0] = maxVelocity;
					else if(velocity[0] < -maxVelocity)
						velocity[0] = -maxVelocity;
				}
				
				if(keys[KeyEvent.VK_S]){
					acceleration[0] -= 0.001;
				
					if(acceleration[0] > maxAcceleration)
						acceleration[0] = maxAcceleration;
					else if(acceleration[0] < -maxAcceleration)
						acceleration[0] = -maxAcceleration;
					
					velocity[0] += acceleration[0];
					
					if(velocity[0] > maxVelocity)
						velocity[0] = maxVelocity;
					else if(velocity[0] < -maxVelocity)
						velocity[0] = -maxVelocity;
				}
			}
			
			if (keys[KeyEvent.VK_R] || keys[KeyEvent.VK_F]) {
				if ( keys[KeyEvent.VK_R] ) {
					
					acceleration[2] += 0.001;
					
					if(acceleration[2] > maxAcceleration)
						acceleration[2] = maxAcceleration;
					else if(acceleration[2] < -maxAcceleration)
						acceleration[2] = -maxAcceleration;
					
					velocity[2] += acceleration[2];
					
					if(velocity[2] > maxVelocity)
						velocity[2] = maxVelocity;
					else if(velocity[2] < -maxVelocity)
						velocity[2] = -maxVelocity;
					
					cameraZ += velocity[2];
				}
				
				if ( keys[KeyEvent.VK_F] ) {
					
					acceleration[2] -= 0.001;
					
					if(acceleration[2] > maxAcceleration)
						acceleration[2] = maxAcceleration;
					else if(acceleration[2] < -maxAcceleration)
						acceleration[2] = -maxAcceleration;
					
					velocity[2] += acceleration[2];
					
					if(velocity[2] > maxVelocity)
						velocity[2] = maxVelocity;
					else if(velocity[2] < -maxVelocity)
						velocity[2] = -maxVelocity;
					
					cameraZ += velocity[2];
				}
			}
			
			if (keys[KeyEvent.VK_A] || keys[KeyEvent.VK_D]) {
				
				float theta = (float) Math.atan2(lookDirectionY, lookDirectionX);
				float phi = (float) Math.acos(lookDirectionZ);
				
				if(keys[KeyEvent.VK_A]){
					
					acceleration[1] += 0.001;
				
					if(acceleration[1] > maxAcceleration)
						acceleration[1] = maxAcceleration;
					else if(acceleration[1] < -maxAcceleration)
						acceleration[1] = -maxAcceleration;
					
					velocity[1] += acceleration[1];
					
					if(velocity[1] > maxVelocity)
						velocity[1] = maxVelocity;
					else if(velocity[1] < -maxVelocity)
						velocity[1] = -maxVelocity;
				}
				
				if(keys[KeyEvent.VK_D]){
					
					acceleration[1] += 0.001;
				
					if(acceleration[1] > maxAcceleration)
						acceleration[1] = maxAcceleration;
					else if(acceleration[1] < -maxAcceleration)
						acceleration[1] = -maxAcceleration;
					
					velocity[1] -= acceleration[1];
					
					if(velocity[1] > maxVelocity)
						velocity[1] = maxVelocity;
					else if(velocity[1] < -maxVelocity)
						velocity[1] = -maxVelocity;
				}
				
				if (keys[KeyEvent.VK_A])
					theta -= Math.PI / 2.0;
				else if (keys[KeyEvent.VK_D])
					theta += Math.PI / 2.0;
				
				float strafeX = (float)(Math.cos(theta) * Math.sin(phi));
				float strafeY = (float)(Math.sin(theta) * Math.sin(phi));
				float latNormxy = (float) Math.sqrt(strafeX * strafeX + strafeY * strafeY);
				cameraX += strafeX / latNormxy * velocity[1];
				cameraY += strafeY / latNormxy * velocity[1];
			}
		}
		else
			flattenAcceleration(acceleration);
		
		cameraX += lookDirectionX / forNormxy * velocity[0];
		cameraY += lookDirectionY / forNormxy * velocity[0];
		cameraZ += lookDirectionZ / forNormxy * velocity[0];
		
		// Update position of the camera
		glu.gluLookAt(cameraX, cameraY, cameraZ,
				cameraX + lookDirectionX, cameraY + lookDirectionY, cameraZ + lookDirectionZ,
				0.0f, 0.0f, 1.0f);
		
			// Draw skybox and move it along with player
			gl.glPushMatrix();
				gl.glTranslatef(cameraX, cameraY, cameraZ);
				skybox.drawSky(gl, skyboxSize);
				
			gl.glPopMatrix();
			
			// Draw any additional 3d models
			if(ship && hud == false)
			{
				drawModel(gl,shipModel,1,3,0,0,5);
			}
			
			drawTrack(gl);
		//	drawStar(gl,3f,55,-62,-2); 
			gl.glPushMatrix();
			drawAsteroids(gl);
			gl.glPopMatrix();
			
			gl.glPopMatrix();
			
			if(hud)
			{
				drawHUD(gl);
				ship = false; 
			}
			end = System.currentTimeMillis();
	        total = (end - start)/1000; 
	        checkpoint_collisions(); 
	      
	        	
	}
	
	void drawHUD(final GL2 gl)
	{
        // -----------Speed Text Rendering ----------------- 
        TextRenderer speed = new TextRenderer(new Font("Helvatica",Font.BOLD,16)); 
        speed.beginRendering(windowWidth, windowHeight);
        speed.setColor(1.0f,1.0f,1.0f,0.8f);
        speed.draw("Speed", 130, 75);
        speed.endRendering();
        
        TextRenderer speed_num = new TextRenderer(new Font("Helvatica",Font.BOLD,16));
        speed_num.beginRendering(windowWidth, windowHeight);
        speed_num.setColor(1.0f,1.0f,1.0f,0.8f);
        // Put speed
        float v1 = velocity[0] * 1000; 
        String v = Float.toString(v1); 
        speed_num.draw(v, 150, 55);
        speed_num.endRendering();
        //-------------------------
        
        //----Time Display-------
        TextRenderer time = new TextRenderer(new Font("Helvatica",Font.BOLD,16)); 
        time.beginRendering(windowWidth, windowHeight);
        time.setColor(1.0f,1.0f,1.0f,0.8f);
        time.draw("Time", 950, 75);
        time.endRendering();
        // Actual Time
        float sec = total; 
        String t =Float.toString(sec);
        TextRenderer time_num = new TextRenderer(new Font("Helvatica",Font.ITALIC,14));
        time_num.beginRendering(windowWidth, windowHeight);
        time_num.setColor(1.0f,1.0f,1.0f,0.8f);
        // Replace "Time" with time variable 
        time_num.draw(t, 970, 55);
        time_num.endRendering();
        //------------------------
        
        //----Current Captured Gate Count-----------
        TextRenderer cap_gate = new TextRenderer(new Font("Helvatica",Font.BOLD,16));
        cap_gate.beginRendering(windowWidth, windowHeight);
        cap_gate.setColor(1.0f,1.0f,1.0f,0.8f);
        cap_gate.draw("Captured Gates", 1450, 75);
        cap_gate.endRendering();
        
        // Captured Gate Number
        String cap = Integer.toString(Captured_Gates);
        
        TextRenderer cap_number = new TextRenderer(new Font("Helvatica",Font.BOLD,16));
        cap_number.beginRendering(windowWidth, windowHeight);
        cap_number.setColor(1.0f,1.0f,1.0f,0.8f);
        cap_number.draw(cap, 1500, 55);
        cap_number.endRendering();
        
        //-------------------------------------
        
        //----Remaining Gates---------
        TextRenderer r_gate = new TextRenderer(new Font("Helvatica",Font.BOLD,16)); 
        r_gate.beginRendering(windowWidth, windowHeight);
        r_gate.setColor(1.0f,1.0f,1.0f,0.8f);
        r_gate.draw("Remaining Gates", 1650, 75);
        r_gate.endRendering();
        String left = Integer.toString(Gates_Left);
        TextRenderer r_number = new TextRenderer(new Font("Helvatica",Font.BOLD,16)); 
        r_number.beginRendering(windowWidth, windowHeight);
        r_number.setColor(1.0f,1.0f,1.0f,0.8f);
        r_number.draw(left, 1715, 55);
        r_number.endRendering();
        
        //----------------------------------
       
	}
	void drawTrack(final GL2 gl)
	{
		drawModel(gl,checkpointModel,3,1,positions.get(0).getX(),positions.get(0).getY(),positions.get(0).getZ());
		 
		drawModel(gl,checkpointModel,3,1,positions.get(1).getX(),positions.get(1).getY(),positions.get(1).getZ());
		 
		drawModel(gl,checkpointModel,3,1,positions.get(2).getX(),positions.get(2).getY(),positions.get(2).getZ());
		
		drawModel(gl,checkpointModel,3,1,positions.get(3).getX(),positions.get(3).getY(),positions.get(3).getZ());
		 
		drawModel(gl,checkpointModel,3,1,positions.get(4).getX(),positions.get(4).getY(),positions.get(4).getZ());
		
		drawModel(gl,checkpointModel,3,1,positions.get(5).getX(),positions.get(5).getY(),positions.get(5).getZ());		
	}
	
	public void checkpoint_collisions()
	{
		float radius = 3f; 
		for(int i =0; i < positions.size();i++)
		{
			if(cameraX >= positions.get(i).getX() - radius && cameraX >= positions.get(i).getX() + radius
					&& cameraY >= positions.get(i).getY() - radius && cameraY <=  positions.get(i).getY() + radius
						&& cameraZ >=  positions.get(i).getZ() - radius && cameraZ <=  positions.get(i).getZ() + radius)
			{
				Captured_Gates += 1; 
				System.out.print("Captured"); 
			}
		}
		
	}
	
	public void asteriods_collisions()
	{
		for(int i =0; i < ast.size();i++)
		{
			if(cameraX >= ast.get(i).getY() - ast.get(i).getX() && cameraX >= ast.get(i).getY() + ast.get(i).getX()
					&& cameraY >= ast.get(i).getY() - ast.get(i).getX() && cameraY <=  ast.get(i).getY() + ast.get(i).getX()
						&& cameraZ >=  ast.get(i).getZ() - ast.get(i).getX() && cameraZ <=  ast.get(i).getZ() + ast.get(i).getX())
			{
				 end(); 
			}
		}
	}
	
	public void end()
	{
		 	JFrame mainFrame;
	   	 	mainFrame = new JFrame("Destoryed");
	   	 	mainFrame.setSize(400, 400);
	   	 	JLabel msglabel; 
        	System.out.print("The Game has ended");
        	msglabel = new JLabel("The Game has ended",JLabel.CENTER); 
        	mainFrame.add(msglabel);
        	mainFrame.setVisible(true); 	
        	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void win()
	{
		//JFrame mainFrame;
   	 	mainFrame = new JFrame("Winning");
   	 	mainFrame.setSize(400, 400);
   	 	JLabel msglabel; 
   	 	JLabel msg2;
    	msglabel = new JLabel("You Won",JLabel.CENTER); 
    	msg2 = new JLabel("HIIIII",JLabel.CENTER); 
    	mainFrame.add(msglabel);
    	mainFrame.add(msg2); 
    	mainFrame.setVisible(true); 	
    	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	void drawStar(final GL2 gl, float radius, float x, float y, float z){
		
		int segments = 20;
		gl.glPushMatrix();
		gl.glTranslatef(x, y, z);
		GLUquadric star = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(star, GLU.GLU_FILL);
		glu.gluQuadricNormals(star, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(star, GLU.GLU_OUTSIDE);
		glu.gluSphere(star,radius,segments,segments);
		glu.gluDeleteQuadric(star);
		gl.glPopMatrix();
	}
	void drawModel(final GL2 gl, File f, int textNum, float scale, float x, float y, float z){
		Model m = null;
		try{
			m = OBJLoader.loadModel(f);
		} catch (FileNotFoundException e){
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e){
			e.printStackTrace();
			System.exit(1);
		}

		gl.glBindTexture(GL2.GL_TEXTURE_2D, texID[(int) textNum]);
		gl.glBegin(GL2.GL_TRIANGLES);
		for(Face face : m.faces){
			
			Vector3f n1 = m.normals.get((int) face.normal.x - 1);
			gl.glNormal3f((n1.x + x) * scale,  (n1.y + y) * scale,  (n1.z + z) * scale);
			Vector2f vt1 = m.textVertices.get((int) face.vertex.x - 1);
			gl.glTexCoord2f(vt1.getX(),vt1.getY());
			Vector3f v1 = m.vertices.get((int) face.vertex.x - 1);
			gl.glVertex3f((v1.x + x) * scale,  (v1.y + y) * scale,  (v1.z + z) * scale);
			
			Vector3f n2 = m.normals.get((int) face.normal.y - 1);
			gl.glNormal3f((n2.x + x) * scale,  (n2.y + y) * scale,  (n2.z + z) * scale);
			Vector2f vt2 = m.textVertices.get((int) face.vertex.y - 1);
			gl.glTexCoord2f(vt2.getX(),vt2.getY());
			Vector3f v2 = m.vertices.get((int) face.vertex.y - 1);
			gl.glVertex3f((v2.x + x) * scale,  (v2.y + y) * scale,  (v2.z + z) * scale);
			
			Vector3f n3 = m.normals.get((int) face.normal.z - 1);
			gl.glNormal3f((n3.x + x) * scale,  (n3.y + y) * scale,  (n3.z + z) * scale);			
			Vector2f vt3 = m.textVertices.get((int) face.vertex.z - 1);
			gl.glTexCoord2f(vt3.getX(),vt3.getY());
			Vector3f v3 = m.vertices.get((int) face.vertex.z - 1);
			gl.glVertex3f((v3.x + x) * scale,  (v3.y + y) * scale,  (v3.z + z) * scale);
		}
		gl.glEnd();
	}
	
	void drawAsteroids(final GL2 gl){
		
		gl.glPushMatrix();
		gl.glRotatef(incrementAngles[0], 1, 1, 1);
		gl.glTranslatef(0,randomIncrementCoords[1][0],randomIncrementCoords[2][0]);
		for(int i = 0; i < numAsteroids - 90; i++){
			
			drawModel(gl,asteroidModel,2,ast.get(i).getW(),ast.get(i).getX(), ast.get(i).getY(), ast.get(i).getZ());
			
			System.out.printf("First Set: %d\n", i);
			
			//incrementAngles[0] += 0.5f;
			if(incrementAngles[0] > 360)
				incrementAngles[0] = 0;
			
			randomIncrementCoords[0][0] += 0.7f;
			if(randomIncrementCoords[0][0] > 900)
				randomIncrementCoords[0][0] = -900;
			
			randomIncrementCoords[1][0] += 0.7f;
			if(randomIncrementCoords[1][0] > 900)
				randomIncrementCoords[1][0] = -900;
			
			randomIncrementCoords[2][0] -= 0.7f;
			if(randomIncrementCoords[2][0] > 900)
				randomIncrementCoords[2][0] = -900;
			else if(randomIncrementCoords[2][0] < -900)
				randomIncrementCoords[2][0] = 900;
		}
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(incrementAngles[1], 1, 1, 1);
		gl.glTranslatef(randomIncrementCoords[0][1],0,randomIncrementCoords[2][1]);
		for(int i = 10; i < numAsteroids - 80; i++){
			
			drawModel(gl,asteroidModel,2,ast.get(i).getW(),ast.get(i).getX(), ast.get(i).getY(), ast.get(i).getZ());
			
			System.out.printf("Second Set: %d\n", i);
			
			//incrementAngles[1] += 0.5f;
			if(incrementAngles[1] > 360)
				incrementAngles[1] = 0;
			
			randomIncrementCoords[0][1] -= 0.7f;
			if(randomIncrementCoords[0][1] > 900)
				randomIncrementCoords[0][1] = -900;
			else if(randomIncrementCoords[0][1] < -900)
				randomIncrementCoords[0][1] = 900;
			
			randomIncrementCoords[1][1] += 0.7f;
			if(randomIncrementCoords[1][1] > 900)
				randomIncrementCoords[1][1] = -900;
			
			randomIncrementCoords[2][1] += 0.7f;
			if(randomIncrementCoords[2][1] > 900)
				randomIncrementCoords[2][1] = -900;
		}
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(incrementAngles[2], 1, 1, 1);
		gl.glTranslatef(0,randomIncrementCoords[1][2],0);
		for(int i = 20; i < numAsteroids - 70; i++){
			
			drawModel(gl,asteroidModel,2,ast.get(i).getW(),ast.get(i).getX(), ast.get(i).getY(), ast.get(i).getZ());
			
			System.out.printf("Third Set: %d\n", i);
			
			//incrementAngles[2] += 0.5f;
			if(incrementAngles[2] > 360)
				incrementAngles[2] = 0;
			
			randomIncrementCoords[0][2] += 0.7f;
			if(randomIncrementCoords[0][2] > 900)
				randomIncrementCoords[0][2] = -900;
			
			randomIncrementCoords[1][2] -= 0.7f;
			if(randomIncrementCoords[1][2] > 900)
				randomIncrementCoords[1][2] = -900;
			else if(randomIncrementCoords[1][2] < -900)
				randomIncrementCoords[1][2] = 900;
			
			randomIncrementCoords[2][2] += 0.7f;
			if(randomIncrementCoords[2][2] > 900)
				randomIncrementCoords[2][2] = -900;
		}
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(incrementAngles[3], 1, 1, 1);
		gl.glTranslatef(0,0,randomIncrementCoords[2][3]);
		for(int i = 30; i < numAsteroids - 60; i++){
			
			drawModel(gl,asteroidModel,2,ast.get(i).getW(),ast.get(i).getX(), ast.get(i).getY(), ast.get(i).getZ());
			
			System.out.printf("Fourth Set: %d\n", i);
			
			//incrementAngles[3] += 0.5f;
			if(incrementAngles[3] > 360)
				incrementAngles[3] = 0;
			
			randomIncrementCoords[0][3] += 0.7f;
			if(randomIncrementCoords[0][3] > 900)
				randomIncrementCoords[0][3] = -900;
			
			randomIncrementCoords[1][3] += 0.7f;
			if(randomIncrementCoords[1][3] > 900)
				randomIncrementCoords[1][3] = -900;
			
			randomIncrementCoords[2][3] += 0.7f;
			if(randomIncrementCoords[2][3] > 900)
				randomIncrementCoords[2][3] = -900;
		}
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(incrementAngles[4], 1, 1, 1);
		gl.glTranslatef(randomIncrementCoords[0][4],0,randomIncrementCoords[2][4]);
		for(int i = 40; i < numAsteroids - 50; i++){
			
			drawModel(gl,asteroidModel,2,ast.get(i).getW(),ast.get(i).getX(), ast.get(i).getY(), ast.get(i).getZ());
			
			System.out.printf("Fifth Set: %d\n", i);
			
			//incrementAngles[4] += 0.5f;
			if(incrementAngles[4] > 360)
				incrementAngles[4] = 0;
			
			randomIncrementCoords[0][4] -= 0.7f;
			if(randomIncrementCoords[0][4] > 900)
				randomIncrementCoords[0][4] = -900;
			else if(randomIncrementCoords[0][4] < -900)
				randomIncrementCoords[0][4] = 900;
			
			randomIncrementCoords[1][4] += 0.7f;
			if(randomIncrementCoords[1][4] > 900)
				randomIncrementCoords[1][4] = -900;
			
			randomIncrementCoords[2][4] += 0.7f;
			if(randomIncrementCoords[2][4] > 900)
				randomIncrementCoords[2][4] = -900;
		}
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(incrementAngles[5], 1, 1, 1);
		gl.glTranslatef(0,randomIncrementCoords[1][5],0);
		for(int i = 50; i < numAsteroids - 40; i++){
			
			drawModel(gl,asteroidModel,2,ast.get(i).getW(),ast.get(i).getX(), ast.get(i).getY(), ast.get(i).getZ());
			
			System.out.printf("Sixth Set: %d\n", i);
			
			//incrementAngles[5] += 0.5f;
			if(incrementAngles[5] > 360)
				incrementAngles[5] = 0;
			
			randomIncrementCoords[0][5] += 0.7f;
			if(randomIncrementCoords[0][5] > 900)
				randomIncrementCoords[0][5] = -900;
			
			randomIncrementCoords[1][5] += 0.7f;
			if(randomIncrementCoords[1][5] > 900)
				randomIncrementCoords[1][5] = -900;
			
			randomIncrementCoords[2][5] += 0.7f;
			if(randomIncrementCoords[2][5] > 900)
				randomIncrementCoords[2][5] = -900;
		}
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(incrementAngles[6], 1, 1, 1);
		gl.glTranslatef(0,randomIncrementCoords[1][6],randomIncrementCoords[2][6]);
		for(int i = 60; i < numAsteroids - 30; i++){
			
			drawModel(gl,asteroidModel,2,ast.get(i).getW(),ast.get(i).getX(), ast.get(i).getY(), ast.get(i).getZ());
			
			System.out.printf("Seventh Set: %d\n", i);
			
			//incrementAngles[6] += 0.5f;
			if(incrementAngles[6] > 360)
				incrementAngles[6] = 0;
			
			randomIncrementCoords[0][6] += 0.7f;
			if(randomIncrementCoords[0][6] > 900)
				randomIncrementCoords[0][6] = -900;
			
			randomIncrementCoords[1][6] -= 0.7f;
			if(randomIncrementCoords[1][6] > 900)
				randomIncrementCoords[1][6] = -900;
			else if(randomIncrementCoords[1][6] < -900)
				randomIncrementCoords[1][6] = 900;
			
			randomIncrementCoords[2][6] += 0.7f;
			if(randomIncrementCoords[2][6] > 900)
				randomIncrementCoords[2][6] = -900;
		}
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(incrementAngles[7], 1, 1, 1);
		gl.glTranslatef(randomIncrementCoords[0][7],randomIncrementCoords[1][7],0);
		for(int i = 70; i < numAsteroids - 20; i++){
			
			drawModel(gl,asteroidModel,2,ast.get(i).getW(),ast.get(i).getX(), ast.get(i).getY(), ast.get(i).getZ());
			
			System.out.printf("Eigth Set: %d\n", i);
			
			//incrementAngles[7] += 0.5f;
			if(incrementAngles[7] > 360)
				incrementAngles[7] = 0;
			
			randomIncrementCoords[0][7] += 0.7f;
			if(randomIncrementCoords[0][7] > 900)
				randomIncrementCoords[0][7] = -900;
			
			randomIncrementCoords[1][7] -= 0.7f;
			if(randomIncrementCoords[1][7] > 900)
				randomIncrementCoords[1][7] = -900;
			else if(randomIncrementCoords[1][7] < -900)
				randomIncrementCoords[1][7] = 900;
			
			randomIncrementCoords[2][7] += 0.7f;
			if(randomIncrementCoords[2][7] > 900)
				randomIncrementCoords[2][7] = -900;
		}
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(incrementAngles[8], 1, 1, 1);
		gl.glTranslatef(randomIncrementCoords[0][8],0,randomIncrementCoords[2][8]);
		for(int i = 80; i < numAsteroids - 10; i++){
			
			drawModel(gl,asteroidModel,2,ast.get(i).getW(),ast.get(i).getX(), ast.get(i).getY(), ast.get(i).getZ());
			
			System.out.printf("Ninth Set: %d\n", i);
			
			//incrementAngles[8] += 0.5f;
			if(incrementAngles[8] > 360)
				incrementAngles[8] = 0;
			
			randomIncrementCoords[0][8] -= 0.7f;
			if(randomIncrementCoords[0][8] > 900)
				randomIncrementCoords[0][8] = -900;
			if(randomIncrementCoords[0][8] < -900)
				randomIncrementCoords[0][8] = 900;
			
			randomIncrementCoords[1][8] += 0.7f;
			if(randomIncrementCoords[1][8] > 900)
				randomIncrementCoords[1][8] = -900;
			
			randomIncrementCoords[2][8] += 0.7f;
			if(randomIncrementCoords[2][8] > 900)
				randomIncrementCoords[2][8] = -900;
		}
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(incrementAngles[9], 1, 1, 1);
		gl.glTranslatef(0,randomIncrementCoords[1][9],randomIncrementCoords[2][9]);
		for(int i = 90; i < numAsteroids; i++){
			
			drawModel(gl,asteroidModel,2,ast.get(i).getW(),ast.get(i).getX(), ast.get(i).getY(), ast.get(i).getZ());
			
			System.out.printf("Tenth Set: %d\n", i);
			
			//incrementAngles[9] += 0.5f;
			if(incrementAngles[9] > 360)
				incrementAngles[9] = 0;
			
			randomIncrementCoords[0][9] += 0.7f;
			if(randomIncrementCoords[0][9] > 900)
				randomIncrementCoords[0][9] = -900;
			
			randomIncrementCoords[1][9] -= 0.7f;
			if(randomIncrementCoords[1][9] > 900)
				randomIncrementCoords[1][9] = -900;
			else if(randomIncrementCoords[1][9] < -900)
				randomIncrementCoords[1][9] = 900;
			
			randomIncrementCoords[2][9] += 0.7f;
			if(randomIncrementCoords[2][9] > 900)
				randomIncrementCoords[2][9] = -900;
		}
		gl.glPopMatrix();
	}
	
	void flattenAcceleration(float[] acceleration){
		
		if(acceleration[0] < 0){
			acceleration[0] += 0.01;
			if(acceleration[0] > 0)
				acceleration[0] = 0;
		}
		else if(acceleration[0] > 0){
			acceleration[0] -= 0.01;
			if(acceleration[0] < 0)
				acceleration[0] = 0;
		}
		
		if(acceleration[1] < 0){
			acceleration[1] += 0.01;
			if(acceleration[1] > 0)
				acceleration[1] = 0;
		}
		else if(acceleration[1] > 0){
			acceleration[1] -= 0.01;
			if(acceleration[1] < 0)
				acceleration[1] = 0;
		}
		
		if(acceleration[2] < 0){
			acceleration[2] += 0.01;
			if(acceleration[2] > 0)
				acceleration[2] = 0;
		}
		else if(acceleration[2] > 0){
			acceleration[2] -= 0.01;
			if(acceleration[2] < 0)
				acceleration[2] = 0;
		}
	}
	
	void allStop(float[] velocity){
		
		if(velocity[0] < 0){
			velocity[0] += 0.1;
			if(velocity[0] > 0)
				velocity[0] = 0;
		}
		else if(velocity[0] > 0){
			velocity[0] -= 0.1;
			if(velocity[0] < 0)
				velocity[0] = 0;
		}
		
		if(velocity[1] < 0){
			velocity[1] += 0.1;
			if(velocity[1] > 0)
				velocity[1] = 0;
		}
		else if(velocity[1] > 0){
			velocity[1] -= 0.1;
			if(velocity[1] < 0)
				velocity[1] = 0;
		}
		
		if(velocity[2] < 0){
			velocity[2] += 0.1;
			if(velocity[2] > 0)
				velocity[2] = 0;
		}
		else if(velocity[2] > 0){
			velocity[2] -= 0.1;
			if(velocity[2] < 0)
				velocity[2] = 0;
		}
	}
	
	void allStopLat(float[] velocity){
		
		if(velocity[1] < 0){
			velocity[1] += 0.1;
			if(velocity[1] > 0)
				velocity[1] = 0;
		}
		else if(velocity[1] > 0){
			velocity[1] -= 0.1;
			if(velocity[1] < 0)
				velocity[1] = 0;
		}
	}
	
	void allStopVert(float[] velocity){
		
		if(velocity[2] < 0){
			velocity[2] += 0.1;
			if(velocity[2] > 0)
				velocity[2] = 0;
		}
		else if(velocity[2] > 0){
			velocity[2] -= 0.1;
			if(velocity[2] < 0)
				velocity[2] = 0;
		}
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {

	}

	@Override
	public void keyTyped(KeyEvent e) {
		char key = e.getKeyChar();
		
		if(key == KeyEvent.VK_SPACE){
			mouseLookEnabled = !mouseLookEnabled;
		}
		if (key == 'h')
		{
			hud = !hud; 
			ship = !ship; 
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
			
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		
		// Mouse look speed
		// Higher number = slower
		final float mouseSpeed = 150.0f;
		
		float dx = (x - mouse_x0);
		float dy = (y - mouse_y0);
		
		if (mouseLookEnabled) {
			float phi = (float) Math.acos(lookDirectionZ);
			float theta = (float) Math.atan2(lookDirectionY, lookDirectionX);
			
			theta -= dx / mouseSpeed;
			phi += dy / mouseSpeed;
			
			if (theta >= Math.PI * 2.0)
				theta -= Math.PI * 2.0;
			else if (theta < 0)
				theta += Math.PI * 2.0;
			
			if (phi > Math.PI - 0.1)
				phi = (float)( Math.PI - 0.1 );
			else if (phi < 0.1f)
				phi = 0.1f;
			
			lookDirectionX = (float)(Math.cos(theta) * Math.sin(phi));
			lookDirectionY = (float)(Math.sin(theta) * Math.sin(phi));
			lookDirectionZ = (float)(Math.cos(phi));
		}
		mouse_x0 = x;
		mouse_y0 = y;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouse_x0 = e.getX();
		mouse_y0 = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e){
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
}