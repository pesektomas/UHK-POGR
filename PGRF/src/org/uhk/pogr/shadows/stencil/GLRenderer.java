package org.uhk.pogr.shadows.stencil;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JLabel;

public class GLRenderer implements GLEventListener  {

	private static final byte MATERIAL_WALL = 1;
	private static final byte MATERIAL_TRIANGL = 2;
	private static final byte MATERIAL_SHADOW = 3;
	private static final byte MATERIAL_FLOOR = 4;
	
	private JLabel lightLabel;
	private GLAutoDrawable mainPanel;

	// stupne otoceni
	private double step_rot = 0.0;

	// udaj o tom, na kterou stranu se rotuje
	private boolean rotate_x = false;
	private boolean rotate_z = false;

	// uhly pootoceni
	private final int axis_x = 0;
	private final int axis_y = 1;
	private final int axis_z = 2;

	// krok posunu
	private double step_trans = 0.0;

	// svetlo x,y
	private float light_x = 1.0f;
	private float light_y = 2.0f;

	// pozice svetla
	private float light_position[] = { light_x, light_y, 2.0f, 0.0f };

	// podlaha a zdi
	private float floor[][] 		= { { -2.0f, -0.5f, -3.0f }, { -2.0f, -0.5f,  4.0f }, {  2.0f, -0.5f,  4.0f }, {  2.0f, -0.5f, -3.0f } };
	private float back_wall[][] 	= { { -2.0f, -0.5f, -3.0f }, { -2.0f,  2.5f, -3.0f }, {  2.0f,  2.5f, -3.0f }, {  2.0f, -0.5f, -3.0f } };
	private float left_wall[][] 	= { { -2.0f, -0.5f,  4.0f }, { -2.0f,  2.5f,  4.0f }, { -2.0f,  2.5f, -3.0f }, { -2.0f, -0.5f, -3.0f } };
	private float right_wall[][] 	= { {  2.0f, -0.5f,  4.0f }, {  2.0f,  2.5f,  4.0f }, {  2.0f,  2.5f, -3.0f }, {  2.0f, -0.5f, -3.0f } };

	// normaly pro podlahu a zdi
	private float normals[][] = { 
			{  1.0f, 0.0f, 0.0f },	// leva zed
			{ -1.0f, 0.0f, 0.0f },	// prava zed
			{  0.0f, 0.0f, 1.0f },	// zadni zed
			{  0.0f, 1.0f, 0.0f }	// podlaha
	};

	// trojuhelnik, ktery vrha stin
	float triangle[][] = { { -1.0f, 0.0f, 1.0f }, { 1.0f, 0.0f, 1.0f }, { 0.0f, 0.0f, -1.0f } };

	public GLRenderer(JLabel L) {
		super();
		lightLabel = L;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		mainPanel = drawable;
		GL2 gl = drawable.getGL().getGL2();

		gl.setSwapInterval(1);
		setLigth(gl);

		gl.glClearDepth(1.0);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_NORMALIZE);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glShadeModel(GL2.GL_SMOOTH);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,int height) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();

		if (height <= 0)
			height = 1;

		final float h = (float) width / (float) height;
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(35.0f, h, 1.0, 500.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glClear(GL2.GL_ACCUM_BUFFER_BIT);

		gl.glLoadIdentity();

		glu.gluLookAt(
				0.0f, 2.0f, 7.0f,	// oko
				0.0f, 0.0f, 0.0f, 	// pohled
				0.0f, 1.0f, 0.0f);	// nahoru/dolu

		lightLabel.setText("Positional light at: (" + light_position[0] + ", " + light_position[1] + ", " + light_position[2] + ")");
		
		
		// svetlo
		gl.glPushMatrix();
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);
		gl.glPushMatrix();

		// vykresleni zdi a podlahy
		gl.glPushMatrix();
		gl.glEnable(GL.GL_POLYGON_OFFSET_FILL);
		gl.glPolygonOffset(1.0f, 1.0f);
		drawWall(gl, right_wall, normals[1]);
		drawWall(gl, left_wall, normals[0]);
		drawWall(gl, back_wall, normals[2]);
		drawFloor(gl, floor, normals[3]);
		gl.glDisable(GL.GL_POLYGON_OFFSET_FILL);
		gl.glPopMatrix();

		// smichani stinu s pozadnim
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_DST_COLOR, GL.GL_SRC_COLOR);

		// STIN
		gl.glPushMatrix();
		drawShadow(gl, triangle, right_wall);
		drawShadow(gl, triangle, left_wall);
		drawShadow(gl, triangle, back_wall);
		drawShadow(gl, triangle, floor);
		gl.glPopMatrix();

		gl.glDisable(GL.GL_BLEND);

		// trojuhelnik
		gl.glPushMatrix();
		drawTriangle(gl);
		gl.glPopMatrix();

		gl.glFlush();
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	/**
	 * Metoda nastavi svetlo
	 * 
	 * @param gl
	 * */
	public void setLigth(GL2 gl) {
		float light_ambient[] = { 0.1f, 0.1f, 0.1f, 1.0f };

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light_ambient, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);

		gl.glLightModeli(GL2.GL_LIGHT_MODEL_LOCAL_VIEWER, 1);

		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
	}

	/**
	 * nastaveni materialu
	 * @param gl
	 * @param choice - volba toho, co se ma nastavit
	 * */
	public void setMaterial(GL2 gl, int choice) {
		if (choice == MATERIAL_WALL) {
			// zdi
			float mat_ambient[] = { 0.7f, 0.7f, 0.7f, 1.0f };
			float mat_diffuse[] = { 0.9f, 0.9f, 0.9f, 1.0f };
			float shininess[] = { 0.0f };

			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, mat_ambient, 0);
			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, shininess, 0);
		} else if (choice == MATERIAL_TRIANGL) {
			// trohuhelnik
			float mat_ambient[] = { 0.5f, 0.0f, 0.0f, 1.0f };
			float mat_diffuse[] = { 0.8f, 0.0f, 0.0f, 1.0f };
			float shininess[] = { 100.0f };

			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, mat_ambient, 0);
			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, shininess, 0);
		} else if (choice == MATERIAL_SHADOW) {
			// stin
			float mat_ambient[] = { 0.5f, 0.5f, 0.5f, 1.0f };
			float mat_diffuse[] = { 0.0f, 0.0f, 0.0f, 1.0f };
			float shininess[] = { 0.0f };

			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, mat_ambient, 0);
			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, shininess, 0);
		} else {
			float mat_ambient[] = { 1.0f, 1.0f, 1.0f, 1.0f };
			float mat_diffuse[] = { 1.0f, 1.0f, 1.0f, 1.0f };
			float shininess[] = { 0.0f };

			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, mat_ambient, 0);
			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
			gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SHININESS, shininess, 0);
		}
	}

	/**
	 * vykresleni stinu
	 * @param gl
	 * @param object - stinici teleso - jeho pozice
	 * @param plane - objekt, na ktery se vrha stin (zdi, podlaha)
	 * */
	public void drawShadow(GL2 gl, float object[][], float plane[][]) {
		// stin je cerny
		setMaterial(gl, MATERIAL_SHADOW);
	}

	/**
	 * metoda nakresli podlahu
	 * */
	public void drawFloor(GL2 gl, float coo[][], float normal[]) {
		setMaterial(gl, MATERIAL_FLOOR);

		gl.glBegin(GL2.GL_QUADS);
		gl.glNormal3f(normal[0], normal[1], normal[2]);
		for (int i = 0; i < coo.length; i++) {
			gl.glVertex3f(coo[i][0], coo[i][1], coo[i][2]);
		}
		gl.glEnd();
	}

	/**
	 * metoda nakresli zed
	 * */
	public void drawWall(GL2 gl, float coo[][], float normal[]) {
		setMaterial(gl, MATERIAL_WALL);

		gl.glBegin(GL2.GL_QUADS);
		gl.glNormal3f(normal[0], normal[1], normal[2]);
		for (int i = 0; i < coo.length; i++) {
			gl.glVertex3f(coo[i][0], coo[i][1], coo[i][2]);
		}
		gl.glEnd();
	}

	/**
	 * metoda nakresli trojuhelnik
	 * */
	public void drawTriangle(GL2 gl) {
		setMaterial(gl, MATERIAL_TRIANGL);
		gl.glBegin(GL2.GL_POLYGON);
		for (int i = 0; i < triangle.length; i++)
			gl.glVertex3f(triangle[i][0], triangle[i][1], triangle[i][2]);
		gl.glEnd();
	}

	/**
	 * metoda nakresli stinici teleso 
	 * */
	private void drawShadowVolume(GL2 gl) {
		  gl.glBegin(GL2.GL_POLYGON);
		  gl.glEnd();
		}   
	
	@Override
	public void dispose(GLAutoDrawable arg0) {}
}
