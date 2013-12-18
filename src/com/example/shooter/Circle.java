package com.example.shooter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;


// triangle shape to be drawn in the context of an OpenGL ES view
public class Circle {
	
	private final String vertexShaderCode =
			"uniform mat4 uMVPMatrix;" +
			"attribute vec4 vPosition;" +
			"void main() {" +
			"    gl_Position = uMVPMatrix * vPosition ;" +
			"}";
	
	private final String fragmentShaderCode =
			"precision mediump float;" +
			"uniform vec4 vColor;" +
			"void main() {" +
			"    gl_FragColor = vColor;" +
			"}";
	
	private FloatBuffer vertexBuffer;
	private final int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;

	// number of coordinates per vertex in this array
	private final int COORDS_PER_VERTEX = 3;
	private float vertices[];
	
	private final int vertexCount;
	private final int vertexStride = COORDS_PER_VERTEX * 4;	// bytes per vertex
	
	// Set color with red, green, blue and alpha (opacity) values
	private float color[] = {1.0f, 0.0f, 0.0f, 1.0f };
	
	public Circle() {
		createCircle();
		vertexCount = vertices.length / COORDS_PER_VERTEX;
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect (vertices.length * 4);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		
		// create a floating point buffer from the ByteBuffer
		vertexBuffer = bb.asFloatBuffer();
		// add the coordinates to the FloatBuffer
		vertexBuffer.put(vertices);
		// set the buffer to read the first coordinate
		vertexBuffer.position(0);
		
		int vertexShader = MyGLSurfaceRender.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = MyGLSurfaceRender.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		
		mProgram = GLES20.glCreateProgram();				// create empty OpenGL ES program
		GLES20.glAttachShader(mProgram,  vertexShader);		// add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader);	// add the fragment shader to program
		GLES20.glLinkProgram(mProgram);						// creates OpenGL ES program executables
	}
	
	public void draw(float[] mvpMatrix) {	// pass in the calculated transformation matrix
		// Add program to OpenGL ES environment
		GLES20.glUseProgram(mProgram);
		
		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		
		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		
		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
									GLES20.GL_FLOAT, false,
									vertexStride, vertexBuffer);
		
		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		
		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		
		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		
		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
		
		// Draw the circle
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
		
		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
	
	private void createCircle() {
		int triangles_per_segment = 8;
		int vertices_per_triangle = 3;
		int points_per_segment = triangles_per_segment*vertices_per_triangle*COORDS_PER_VERTEX;
		int segments = 15;
		float innerRadius = 0.9f;
		float outerRadius = 1.0f;
		vertices = new float[points_per_segment*segments];		
		
		float angle = (float) Math.toRadians(360.0f/segments);
		float curAngle = 0;
		for(int i=0; i<vertices.length; i+=points_per_segment){			
			//top side triangles
			vertices[i] = (float) Math.cos(curAngle)*innerRadius;			
			vertices[i+1] = 0;
			vertices[i+2] = (float)Math.sin(curAngle)*innerRadius;			
			
			vertices[i+3] = (float) Math.cos(curAngle)*outerRadius;			
			vertices[i+4] = 0;
			vertices[i+5] = (float)Math.sin(curAngle)*outerRadius;			
			
			vertices[i+6] = (float)Math.cos(curAngle+angle)*outerRadius;		
			vertices[i+7] = 0;
			vertices[i+8] = (float)Math.sin(curAngle+angle)*outerRadius;			
			
			vertices[i+9] = (float) Math.cos(curAngle)*innerRadius;	
			vertices[i+10] = 0;
			vertices[i+11] = (float)Math.sin(curAngle)*innerRadius;			
			
			vertices[i+12] = (float) Math.cos(curAngle+angle)*innerRadius;
			vertices[i+13] = 0;
			vertices[i+14] = (float)Math.sin(curAngle+angle)*innerRadius;			
			
			vertices[i+15] = (float)Math.cos(curAngle+angle)*outerRadius;
			vertices[i+16] = 0;
			vertices[i+17] = (float)Math.sin(curAngle+angle)*outerRadius;
			
			//outer side triangles
			vertices[i+18] = (float) Math.cos(curAngle)*outerRadius;
			vertices[i+19] = 0;
			vertices[i+20] = (float) Math.sin(curAngle)*outerRadius;
			
			
			vertices[i+21] = (float) Math.cos(curAngle)*outerRadius;
			vertices[i+22] = -0.1f;
			vertices[i+23] = (float) Math.sin(curAngle)*outerRadius;			
			
			vertices[i+24] =(float) Math.cos(curAngle+angle)*outerRadius;
			vertices[i+25] = -0.1f;
			vertices[i+26] =(float) Math.sin(curAngle+angle)*outerRadius;			
			
			vertices[i+27] = (float) Math.cos(curAngle+angle)*outerRadius;			
			vertices[i+28] = -0.1f;
			vertices[i+29] = (float) Math.sin(curAngle+angle)*outerRadius;			
			
			vertices[i+30] = (float) Math.cos(curAngle+angle)*outerRadius;
			vertices[i+31] = 0;
			vertices[i+32] = (float) Math.sin(curAngle+angle)*outerRadius;			
			
			vertices[i+33] = (float) Math.cos(curAngle)*outerRadius;			
			vertices[i+34] = 0;
			vertices[i+35] = (float) Math.sin(curAngle)*outerRadius;
			
			//inner side triangles
			vertices[i+36] = (float) Math.cos(curAngle)*innerRadius;
			vertices[i+37] = 0;
			vertices[i+38] = (float) Math.sin(curAngle)*innerRadius;			
			
			vertices[i+39] = (float) Math.cos(curAngle)*innerRadius;
			vertices[i+40] = -0.1f;
			vertices[i+41] = (float) Math.sin(curAngle)*innerRadius;			
			
			vertices[i+42] =(float) Math.cos(curAngle+angle)*innerRadius;
			vertices[i+43] = -0.1f;
			vertices[i+44] =(float) Math.sin(curAngle+angle)*innerRadius;			
			
			vertices[i+45] = (float) Math.cos(curAngle+angle)*innerRadius;			
			vertices[i+46] = -0.1f;
			vertices[i+47] = (float) Math.sin(curAngle+angle)*innerRadius;			
			
			vertices[i+48] = (float) Math.cos(curAngle+angle)*innerRadius;
			vertices[i+49] = 0;
			vertices[i+50] = (float) Math.sin(curAngle+angle)*innerRadius;			
			
			vertices[i+51] = (float) Math.cos(curAngle)*innerRadius;			
			vertices[i+52] = 0;
			vertices[i+53] = (float) Math.sin(curAngle)*innerRadius;
			
			//bottom side triangles
			vertices[i+54] = (float) Math.cos(curAngle)*innerRadius;
			vertices[i+55] = -0.1f;
			vertices[i+56] = (float)Math.sin(curAngle)*innerRadius;;
			
			vertices[i+57] = (float) Math.cos(curAngle)*outerRadius;
			vertices[i+58] = -0.1f;
			vertices[i+59] = (float)Math.sin(curAngle)*outerRadius;
			
			vertices[i+60] = (float)Math.cos(curAngle+angle)*outerRadius;
			vertices[i+61] = -0.1f;
			vertices[i+62] = (float)Math.sin(curAngle+angle)*outerRadius;
			
			vertices[i+63] = (float) Math.cos(curAngle)*innerRadius;
			vertices[i+64] = -0.1f;
			vertices[i+65] = (float)Math.sin(curAngle)*innerRadius;;
			
			vertices[i+66] = (float) Math.cos(curAngle+angle)*innerRadius;
			vertices[i+67] = -0.1f;
			vertices[i+68] = (float)Math.sin(curAngle+angle)*innerRadius;
						
			vertices[i+69] = (float)Math.cos(curAngle+angle)*outerRadius;
			vertices[i+70] = -0.1f;
			vertices[i+71] = (float)Math.sin(curAngle+angle)*outerRadius;
			
			curAngle+=angle;
		}		
	}
}