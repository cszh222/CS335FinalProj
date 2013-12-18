package com.example.shooter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;


public class MyGLSurfaceRender implements Renderer {
	
	public volatile float m_Zoom;
	
	private final float[] m_VMat = new float[16];
	private final float[] m_ProjMat = new float[16];
	private final float[] m_MVPMat = new float[16];
	//private final float[] m_RotationMatX = new float [16];
	//private final float[] m_RotationMatY = new float [16];
	private final float[] m_NormalMat = new float[16];
	private final float[] m_MVMat = new float[16];
	
	private float m_ratio;
	
	private float m_camPos;
	private float m_camPosZ;
	private float m_eyeAngleX;
	private float m_eyeAngleY;
	private float m_shootAngleX;
	private float m_shootAngleY;
	private float m_ballPosX;
	private float m_ballPosY;
	private float m_ballPosZ;
	
	private float m_eyeX;
	private float m_eyeY;
	
	private Sphere m_ball;
	private Cube m_backBoard;
	private Cube m_floor;
	private Cube m_backDrop;
	private Circle m_rim;
	
	private boolean gameModeFlag;
	
	@Override
	public void onDrawFrame(GL10 arg0) {
        // Redraw background color and depth
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT|GLES20.GL_DEPTH_BUFFER_BIT);
                
        // Set the camera position (View matrix)
        Matrix.setLookAtM(m_VMat, 0, m_eyeX, m_eyeY, m_Zoom, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);       
        
        //temp = Proj*View
        float[] temp = new float[16];
        Matrix.multiplyMM(temp, 0, m_ProjMat, 0, m_VMat, 0);

        float[] camTranslate = new float[16];        
        Matrix.setIdentityM(camTranslate, 0);
        Matrix.translateM(camTranslate, 0,m_camPos, 0, m_camPosZ);
        
        Matrix.multiplyMM(m_MVPMat, 0, temp, 0, camTranslate, 0);
        Matrix.multiplyMM(m_MVMat, 0, m_VMat, 0, camTranslate, 0);
        
        Matrix.invertM(temp, 0, m_MVMat, 0);
        Matrix.transposeM(m_NormalMat, 0, temp, 0);
        
        drawBackDrop();
        drawRim();
        drawBall();
        drawBackBoard();
        drawFloor();
	}

	private void drawBackDrop() {
		float[] finalTransform = new float[16];
		//do final transforms and render the ball, such as scaling and translation
		float[] scale = new float[16];
		float[] translate = new float[16];
		float[] temp = new float[16];
		
		Matrix.setIdentityM(scale, 0);
		Matrix.scaleM(scale, 0, 30.0f, 25.0f, 0.1f);
		
		Matrix.setIdentityM(translate, 0);
		Matrix.translateM(translate, 0, 0, 0, 25);
		
		Matrix.multiplyMM(temp, 0, translate, 0, scale, 0);
				
		Matrix.multiplyMM(finalTransform, 0, m_MVPMat, 0, temp, 0);			
		
		m_backDrop.draw(finalTransform, m_NormalMat, m_MVMat);
	}

	private void drawFloor() {
		float[] finalTransform = new float[16];
		//do final transforms and render the ball, such as scaling and translation
		float[] scale = new float[16];
		float[] translate = new float[16];
		float[] temp = new float[16];
		
		Matrix.setIdentityM(scale, 0);
		Matrix.scaleM(scale, 0, 30f, 1.0f, 30f);
		
		Matrix.setIdentityM(translate, 0);
		Matrix.translateM(translate, 0, 0, -3, 0);
		
		Matrix.multiplyMM(temp, 0, translate, 0, scale, 0);
				
		Matrix.multiplyMM(finalTransform, 0, m_MVPMat, 0, temp, 0);			

		m_floor.draw(finalTransform, m_NormalMat, m_MVMat);			
	}

	private void drawBackBoard() {
		float[] finalTransform = new float[16];
		//do final transforms and render the ball, such as scaling and translation
		float[] scale = new float[16];
		float[] translate = new float[16];
		float[] temp = new float[16];
		
		Matrix.setIdentityM(scale, 0);
		Matrix.scaleM(scale, 0, 3.5f, 1.5f, 0.1f);
		
		Matrix.setIdentityM(translate, 0);
		Matrix.translateM(translate, 0, 0, 5, 15);
		
		Matrix.multiplyMM(temp, 0, translate, 0, scale, 0);
				
		Matrix.multiplyMM(finalTransform, 0, m_MVPMat, 0, temp, 0);			

		m_backBoard.draw(finalTransform, m_NormalMat, m_MVMat);	
	}
	
	private void drawRim(){
		float[] finalTransform = new float[16];
		float[] scale = new float[16];
		float[] translate = new float[16];
		float[] temp = new float[16];
		
		Matrix.setIdentityM(scale, 0);
		Matrix.scaleM(scale, 0, 1.0f, 1.0f, 1.0f);
		
		Matrix.setIdentityM(translate, 0);
		Matrix.translateM(translate, 0, 0, 4.0f, 14.0f);
		
		Matrix.multiplyMM(temp, 0, translate, 0, scale, 0);
				
		Matrix.multiplyMM(finalTransform, 0, m_MVPMat, 0, temp, 0);
		
		m_rim.draw(finalTransform);
		
	}
	private void drawBall() {
		float[] finalTransform = new float[16];
		//do final transforms and render the ball, such as scaling and translation
		
		float[] scale = new float[16];
		float[] translate = new float[16];
		float[] temp = new float[16];
		
		Matrix.setIdentityM(scale, 0);
		Matrix.scaleM(scale, 0, 0.2f, 0.2f, 0.2f);
		
		Matrix.setIdentityM(translate, 0);
		Matrix.translateM(translate, 0, m_ballPosX, m_ballPosY, m_ballPosZ);
		
		Matrix.multiplyMM(temp, 0, translate, 0, scale, 0);
				
		Matrix.multiplyMM(finalTransform, 0, m_MVPMat, 0, temp, 0);			

		m_ball.draw(finalTransform, m_NormalMat, m_MVMat);			
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		
		m_ratio = (float) width/height;
		Matrix.frustumM(m_ProjMat, 0, -m_ratio, m_ratio, -0.5f, 1, 1,100);
	}

	@Override
	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		GLES20.glClearColor(255.0f, 255.0f, 255.0f, 1.0f);
		
		GLES20.glEnable(GL10.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GL10.GL_LEQUAL);
		
		m_ball = new Sphere(1, 20, 40);
		m_backBoard = new Cube(0.0f, 0.0f, 1.0f);
		m_rim = new Circle();
		m_floor = new Cube(1.0f, 1.0f, 0.0f);
		m_backDrop = new Cube(0.82f, 0.82f, 0.82f);
		
		m_Zoom = -1.0f;
		m_eyeAngleX = 0.0f;
		m_eyeAngleY = 0.0f;
		m_shootAngleX = 0.0f;
		m_shootAngleY = 0.0f;
		m_camPos = 0.0f;
		m_ballPosX = 0.0f;
		m_ballPosY = 0.0f;
		m_ballPosZ = 1.0f;
		
		m_eyeX = (float) (Math.tan(m_eyeAngleX)*m_Zoom);
		m_eyeY = (float) (Math.tan(m_eyeAngleY)*m_Zoom);
		
		gameModeFlag = false;
	}

	public static int loadShader(int type, String shaderCode) {
		int shader = GLES20.glCreateShader(type);
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);
		
		return shader;
	}

	public void changePos(float moveCoord) {
		m_camPos+=moveCoord;		
		m_ballPosX-=moveCoord;
	}

	public void setEyeAngleX(float angle) {
		m_eyeAngleX+=angle;
		if(m_eyeAngleX>=(float)Math.PI/2)
			m_eyeAngleX = (float) (Math.PI/2);
		if(m_eyeAngleX<=(float)-Math.PI/2)
			m_eyeAngleY =(float)-Math.PI/2;		
		m_eyeX = (float) (Math.tan(m_eyeAngleX)*m_Zoom);		
	}	
	
	public void setEyeAngleY(float angle) {
		m_eyeAngleY+=angle;
		if(m_eyeAngleY>=(float)Math.PI/2)
			m_eyeAngleY = (float) (Math.PI/2);
		if(m_eyeAngleY<=0.0f)
			m_eyeAngleY =0.0f;
		
		m_eyeY = (float) (Math.tan(m_eyeAngleY)*m_Zoom);		
	}
	
	public void setShootAngleY(float angle) {
		m_shootAngleY+=angle;
		if(m_shootAngleY>=(float)Math.PI/2)
			m_shootAngleY = (float) (Math.PI/2);
		if(m_shootAngleY<=0.0f)
			m_shootAngleY =0.0f;		
	}
	
	public void setShootAngleX(float angle) {
		m_shootAngleX+=angle;
		if(m_shootAngleX>=(float)Math.PI/2)
			m_shootAngleX = (float) (Math.PI/2);
		if(m_shootAngleX<=0.0f)
			m_shootAngleX =0.0f;		
	}

	public float getZoom() {
		return m_Zoom;
	}

	public boolean isGameMode() {
		return gameModeFlag;
	}

	public void setCamPosZ(float pos) {
		m_camPosZ+=pos;
		
		if(m_camPosZ <= -10.0f)//prevent zooming too far
			m_camPosZ = -10.0f;
		else if (m_camPosZ >= 5.0f)//prevent zooming to close
			m_camPosZ = 5.0f;		
	}	
	
}

