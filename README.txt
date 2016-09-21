Added Progress
* Collision Detection
* Random Asteriods 
* Asteriods are moving
* Free view




-Code Structure- 
 
Libraries: 
LWJGL used for object loading only 
JOGL 
glugen-rt 

We had 2 packages: finalProj and utility 

*finalProj has four java and deals mainly with the game and game logic. 
  The 4 java files are: 
	* Main.java
	* JoglEventListener.java -> this handles game logic and pretty much everything 
	* Skybox.java -> creates the skybox and binds the textures  to it. 
	* TextureLoader.java -> loads the textures from files that are given. 

* utility: Has four java files and loads with the object files
   The 4 java files are: 
	* BufferTools.java
	* Face.java 
	* Model.java -> Creates the vectors where the object files are to be stored. 
	* OBJLoader.java -> Parses through the obj file spilts based off "vt", "vn", "v " and"f".  

We also have two folders:
	models: Which holds the obj files for loading 
	textures: Holds the texture for wrapping aroung the obj files. 


User-Manual
Controls:
	Space Bar: Free View and Mouse Look 
	W:  Forward Acceleration 
	 S:  Reverse Acceleration 
	 A:  Move Left 
	 D:  Move Right
	 Z:   Break
	 H:   HUD display 
	Mouse Cursor: Point to move

//To Begin: Change the path of the Textures located on line 97-102 in the code to where they are stored on your computer.
 After running, click the screen and begin. 
Use the space bar so that the screen will capture the mouse and use it for movements 
 


	

 
