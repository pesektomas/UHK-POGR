package org.uhk.pogr.shadows.projection;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JLabel;

public class GLRenderer implements GLEventListener, KeyListener {

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

		// vypocet normaly
		float normal[] = calculateNormal(plane[0], plane[1], plane[3]);

		// vypocet pozice stinu
		float p1[] = calculateProjection(plane[0], object[0], normal, light_position);
		float p2[] = calculateProjection(plane[0], object[1], normal, light_position);
		float p3[] = calculateProjection(plane[0], object[2], normal, light_position);

		// stin je cerny
		setMaterial(gl, MATERIAL_SHADOW);

		// kresleni
		gl.glBegin(GL2.GL_POLYGON);
		gl.glNormal3f(normal[0], normal[1], normal[2]);
		gl.glVertex3f(p1[0], p1[1], p1[2]);
		gl.glVertex3f(p2[0], p2[1], p2[2]);
		gl.glVertex3f(p3[0], p3[1], p3[2]);
		gl.glEnd();
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
	 * metoda zkontroluje, zda je cii neni trojuhelnik ve zdi, nebo v podlaze
	 * */
	public boolean isInside(float object[][]) {
		float off = 0.025f;
		float x_min = -2.0f + off;
		float x_max = 2.0f - off;
		float y_min = -0.5f + off;
		float y_max = 3.0f - off;
		float z_min = -3.0f + off;
		float z_max = 4.0f - off;

		int check = 0;
		for (int i = 0; i < object.length; i++) {
			if ((x_min <= object[i][0]) && (object[i][0] <= x_max)
					&& (y_min <= object[i][1]) && (object[i][1] <= y_max)
					&& (z_min <= object[i][2]) && (object[i][2] <= z_max))
				check++;
		}

		if (check == 3)
			return true;

		return false;
	}

	/**
	 * metoda zjisti, jak se trojuhelnik otaci a nastavi jeho novou pozici
	 * */
	public void rotate(double rot) {
		float tmp_y[] = new float[3];
		float tmp_x[] = new float[3];
		float tmp_z[] = new float[3];

		for (int k = 0; k < 3; k++) {
			tmp_y[k] = triangle[k][1];
			tmp_x[k] = triangle[k][0];
			tmp_z[k] = triangle[k][2];

			translate(axis_y, -(double) tmp_y[k]);
			translate(axis_x, -(double) tmp_x[k]);
			translate(axis_z, -(double) tmp_z[k]);
		}

		if (rotate_x) {
			for (int j = 0; j < 3; j++) {
				float x = triangle[j][0];
				float y = (triangle[j][1] * (float) Math.cos(Math.toRadians(rot))) - (triangle[j][2] * (float) Math.sin(Math.toRadians(rot)));
				float z = ((float) Math.sin(Math.toRadians(rot) * triangle[j][1])) + (triangle[j][2] * (float) Math.cos(Math.toRadians(rot)));

				triangle[j][0] = x;
				triangle[j][1] = y;
				triangle[j][2] = z;
			}
		} else if (rotate_z) {
			for (int j = 0; j < 3; j++) {
				float x = (triangle[j][0] * (float) Math.cos(Math.toRadians(rot))) + (triangle[j][1] * (float) Math.sin(Math.toRadians(rot)));
				float y = -((float) Math.sin(Math.toRadians(rot) * triangle[j][0])) + (triangle[j][1] * (float) Math.cos(Math.toRadians(rot)));
				float z = triangle[j][2];

				triangle[j][0] = x;
				triangle[j][1] = y;
				triangle[j][2] = z;
			}
		} else {
			System.err.println("Error in method: void rotate()");
			System.exit(-1);
		}

		for (int k = 0; k < 3; k++) {
			translate(axis_y, tmp_y[k]);
			translate(axis_x, tmp_x[k]);
			translate(axis_z, tmp_z[k]);
		}
	}

	/**
	 * posouvani nahoru dolu
	 * */
	public void translate(int axis, double step) {
		for (int i = 0; i < 3; i++) {
			float tmp = triangle[i][axis] + (float) step;
			triangle[i][axis] = tmp;
		}
	}

	/**
	 * Vypocet normaly
	 * */
	public float[] calculateNormal(float p1[], float p2[], float p3[]) {
		float normal[] = new float[3];
		normal[0] = (((p2[1] - p1[1]) * (p3[2] - p1[2])) - ((p2[2] - p1[2]) * (p3[1] - p1[1])));
		normal[1] = (((p2[2] - p1[2]) * (p3[0] - p1[0])) - ((p2[0] - p1[0]) * (p3[2] - p1[2])));
		normal[2] = (((p2[0] - p1[0]) * (p3[1] - p1[1])) - ((p2[1] - p1[1]) * (p3[0] - p1[0])));
		return normal;
	}

	/**
	 * vypocet pozice stinu, detail popsan v tele metody
	 * */
	public float[] calculateProjection(float r[], float p[], float n[],
			float a[]) {
		float projection[] = new float[3];

		/*
		 * TEORIE: 
		 * body x1, x2, x3, p - stinici teleso, a - svetlo
		 *  
		 * x = p + t*a. 
		 * (1) 	x1 = p1 + t*a1 
		 * 		x2 = p2 + t*a2 
		 * 		x3 = p3 + t*a3
		 * 
		 * rovina, n - normala, r - rovina
		 * 
		 * (2) n1*r1 + n2*r2 + n3*r3 + d = 0
		 * 
		 * protnuti primky a roviny
		 * (3) n1(p1 + t*a1) + n2(p2 + t*a2) + n3(p3 + t*a3) = n1*r1 + n2*r2 + n3*r3
		 * 
		 * 
		 * (4) 		n1*p1 + n1*t*a1 + n2*p2 + n2*t*a2 + n3*p3 + n3*t*a3 = 
		 * 			n1*r1 + n2*r2 + n3*r3 n1*t*a1 + n2*t*a2 + n3*t*a3 = 
		 * 			n1*r1 + n2*r2 + n3*r3 - n1*p1 - n2*p2 - n3*p3 t(n1*a1 + n2*a2 + n3*a3) = 
		 * 			n1(r1 - p1) + n2(r2 - p2) + n3(r3 - p3) 
		 * 
		 * 			t = (n1(r1 - p1) + n2(r2 - p2) + n3(r3 - p3))/(n1*a1 + n2*a2 + n3*a3)
		 * 	
		 * 	t dosadime do (1)
		 * 
		 */

		float t = (n[0] * (r[0] - p[0]) + n[1] * (r[1] - p[1]) + n[2]
				* (r[2] - p[2]))
				/ (n[0] * a[0] + n[1] * a[1] + n[2] * a[2]);

		float x1 = p[0] + (t * a[0]);
		float x2 = p[1] + (t * a[1]);
		float x3 = p[2] + (t * a[2]);

		projection[0] = x1;
		projection[1] = x2;
		projection[2] = x3;

		return projection;
	}

	/*
	 * key listenere pro hybani s trojuhelnikem a svetlem
	 * **/
	@Override
	public void keyPressed(KeyEvent e) {
		// sipky
		switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				rotate_x = false;
				rotate_z = true;
				step_rot = -2.0;
				rotate(step_rot);
				if (!isInside(triangle)){
					rotate(-step_rot);
				}
			break;
			case KeyEvent.VK_RIGHT:
				rotate_x = false;
				rotate_z = true;
				step_rot = 2.0;
				rotate(step_rot);
				if (!isInside(triangle)){
					rotate(-step_rot);
				}
				break;
			case KeyEvent.VK_UP:
				rotate_x = true;
				rotate_z = false;
				step_rot = -2.0;
				rotate(step_rot);
				if (!isInside(triangle)){
					rotate(-step_rot);
				}	
				break;
			case KeyEvent.VK_DOWN:
				rotate_x = true;
				rotate_z = false;
				step_rot = 2.0;
				rotate(step_rot);
				if (!isInside(triangle)){
					rotate(-step_rot);
				}
				break;
			case KeyEvent.VK_F1:
				light_x += 0.25;
				light_position[0] = light_x;
				break;
			case KeyEvent.VK_F2:
				light_x -= 0.25;
				light_position[0] = light_x;
				break;
			case KeyEvent.VK_F3:
				light_y -= 0.25;
				light_position[1] = light_y;
				break;
			case KeyEvent.VK_F4:
				light_y += 0.25;
				light_position[1] = light_y;
				break;
		}

		// znaky
		switch (e.getKeyChar()) {
			case 'D':
			case 'd':
				step_trans = -0.025;
				translate(axis_y, step_trans);
				if (!isInside(triangle)){
					translate(axis_y, -step_trans);
				}
				break;
			case 'U':
			case 'u':
				step_trans = 0.025;
				translate(axis_y, step_trans);
				if (!isInside(triangle)){
					translate(axis_y, -step_trans);
				}
				break;
			case 'T':
			case 't':
				step_trans = 0.025;
				translate(axis_z, step_trans);
				if (!isInside(triangle)){
					translate(axis_z, -step_trans);
				}
				break;
			case 'A':
			case 'a':
				step_trans = -0.025;
				translate(axis_z, step_trans);
				if (!isInside(triangle)){
					translate(axis_z, -step_trans);
				}
				break;
			case 'L':
			case 'l':
				step_trans = -0.025;
				translate(axis_x, step_trans);
				if (!isInside(triangle)){
					translate(axis_x, -step_trans);
				}
				break;
			case 'R':
			case 'r':
				step_trans = 0.025;
				translate(axis_x, step_trans);
				if (!isInside(triangle)){
					translate(axis_x, -step_trans);
				}
				break;
		}
		mainPanel.display();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	@Override
	public void dispose(GLAutoDrawable arg0) {}
}
