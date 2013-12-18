package com.example.shooter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.util.Log;


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
	
	private float m_xVelo;
	private float m_yVelo;
	private float m_zVelo;
	
	private float replay_xPos;
	private float replay_yPos;
	private float replay_zPos;
	private float replay_xVelo;
	private float replay_yVelo;
	private float replay_zVelo;	
	
	private float m_eyeX;
	private float m_eyeY;
	
	private Sphere m_ball;
	private Cube m_backBoard;
	private Cube m_floor;
	private Cube m_backDrop;
	private Circle m_rim;
	
	private boolean gameModeFlag;
	private boolean animateModeFlag;	
	private boolean rimPassed;
	private boolean floorCollide;
	
	public static final float GRAVITY = -0.08f;
	
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
		Matrix.scaleM(scale, 0, 0.5f, 0.5f, 0.5f);
		
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
		m_eyeAngleY = (float) Math.toRadians(20.0);
		m_shootAngleX = 0.0f;
		m_shootAngleY = (float) Math.toRadians(45.0);
		m_camPos = 0.0f;
		m_ballPosX = 0.0f;
		m_ballPosY = 0.0f;
		m_ballPosZ = 1.0f;
		
		m_xVelo = 0;
		m_yVelo = 0;
		m_zVelo = 0;
		
		m_eyeX = (float) (Math.tan(m_eyeAngleX)*m_Zoom);
		m_eyeY = (float) (Math.tan(m_eyeAngleY)*m_Zoom);
		
		gameModeFlag = true;
		animateModeFlag = false;
		rimPassed = false;
		floorCollide = false;
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
		if(m_eyeAngleX>=(float)Math.PI)
			m_eyeAngleX = (float) (Math.PI);
		if(m_eyeAngleX<=(float)-Math.PI)
			m_eyeAngleX=(float)-Math.PI;		
		m_eyeX = (float) (Math.tan(m_eyeAngleX)*m_Zoom);		
	}	
	
	public void setEyeAngleY(float angle) {
		m_eyeAngleY+=angle;
		if(m_eyeAngleY>=(float)Math.PI/2)
			m_eyeAngleY = (float)(Math.PI/2);
		if(m_eyeAngleY<=0.0f)
			m_eyeAngleY =0.0f;
		
		m_eyeY = (float) (Math.tan(m_eyeAngleY)*m_Zoom);		
	}
	
	public void setShootAngleY(float angle) {
		m_shootAngleY+=angle;
		if(m_shootAngleY>=(float)Math.PI/2)
			m_shootAngleY = (float)(Math.PI/2);
		if(m_shootAngleY<=0.0f)
			m_shootAngleY =0.0f;		
	}
	
	public void setShootAngleX(float angle) {
		m_shootAngleX+=angle;
		if(m_shootAngleX>=(float)Math.PI)
			m_shootAngleX = (float)Math.PI;
		if(m_shootAngleX<=(float)-Math.PI)
			m_shootAngleX =(float)-Math.PI;		
	}

	public float getZoom() {
		return m_Zoom;
	}

	public boolean isGameMode() {
		return gameModeFlag;
	}
	
	public boolean isAnimating(){
		return animateModeFlag;
	}

	public void setCamPosZ(float pos) {
		m_camPosZ+=pos;
		
		if(m_camPosZ <= -10.0f)//prevent zooming too far
			m_camPosZ = -10.0f;
		else if (m_camPosZ >= 5.0f)//prevent zooming to close
			m_camPosZ = 5.0f;		
	}

	public void setVelocity(float velocity) {
		m_xVelo = (float) (velocity*Math.sin(m_shootAngleX)*Math.cos(m_shootAngleY));
		m_yVelo = (float) (velocity*Math.sin(m_shootAngleY));		
		m_zVelo = (float) (velocity*Math.cos(m_shootAngleX)*Math.cos(m_shootAngleY));
		
		Log.i("YVELO", "x"+m_xVelo+"y"+m_yVelo+"z"+m_zVelo);
		
	}

	public void setAnimateFlag(boolean flag) {
		animateModeFlag = flag;		
	}
	
	public void moveBall(int frameRate){
		float moverate = frameRate/100f;
		m_ballPosX = m_ballPosX + m_xVelo*moverate;
		m_ballPosY = m_ballPosY + m_yVelo*moverate;
		m_ballPosZ = m_ballPosZ + m_zVelo*moverate;
		m_yVelo = m_yVelo+GRAVITY*moverate;
		//Log.i("moves", "yPos"+m_ballPosY+"zPos");
		if(!rimPassed && m_ballPosY>4.0f)
			rimPassed = true;
	}
	
	public void detectBoardCollision(){
		if(Math.abs(m_ballPosX) <= 3.5f && m_ballPosY<=6.5f && m_ballPosY>=3.5f){
			//check collision with board
			if(m_ballPosZ+0.5f >= 14.9f && m_ballPosZ+0.5f<=15.5f){
				m_zVelo = -m_zVelo;
				m_ballPosZ = 14.9f-0.5f;
			}				
		}			
	}
	
	public void detectFloorCollision(){
		if(m_ballPosY-0.5f<=-3.0f && m_yVelo <= 0){
			m_ballPosY = 0.0f;
			m_yVelo = -m_yVelo;
			floorCollide = true;
		}
	}
	
	public boolean detectRimCollision(){
		float[] points = new float[32];
		float angle = (float)Math.toRadians(360f/16);
		float curAngle = 0;
		for(int i=0; i<32; i+=2){
			points[i] = (float)Math.cos(curAngle);
			points[i+1] = (float)Math.sin(curAngle)+14f;
			curAngle+=angle;
		}
		boolean collide = false;
		if(m_ballPosY-0.5f<=4.0f && m_ballPosY+0.5f>=4.0f)
			for(int i=0; i<32; i+=2){
				double xDiff = m_ballPosX-points[i];
				double zDiff = m_ballPosZ-points[i+1];
				double distance = Math.sqrt(Math.pow(xDiff, 2)+Math.pow(zDiff, 2));
				if(distance <=0.5){
					collide = true;
					break;
				}
			}
		return collide;
	}
	
	public boolean checkWin(){
		boolean win = false;
		if(rimPassed)
			if(m_ballPosY+0.5>=4.0f && m_ballPosY-0.5f<=4.0f){
				double xDiff = m_ballPosX-0;
				double zDiff = m_ballPosZ-14;
				double distance = Math.sqrt(Math.pow(xDiff, 2)+Math.pow(zDiff, 2));
				if(distance<=0.5f)
					win = true;			
			}		
		return win;
	}
	
	public boolean checkLose(){
		boolean lose = false;
		
		if(m_ballPosZ>=15.0f || m_ballPosZ<0f)
			lose = true;
		
		if(floorCollide)
			lose = true;		
		return lose;
	}
	
	public void setGameMode(boolean mode) {
		gameModeFlag = mode;		
	}

	public void setBallVelocity(float x, float y, float z) {
		m_xVelo = x;
		m_yVelo = y;
		m_zVelo = z;		
	}

	public void setReplayParams() {
		replay_xPos = m_ballPosX;
		replay_yPos = m_ballPosY;
		replay_zPos = m_ballPosZ;
		
		replay_xVelo = m_xVelo;
		replay_yVelo = m_yVelo;
		replay_zVelo = m_zVelo;
	}
	
	public void loadReplayParams(){
		m_ballPosX = replay_xPos;
		m_ballPosY = replay_yPos;
		m_ballPosZ = replay_zPos;
		
		m_xVelo = replay_xVelo;
		m_yVelo = replay_yVelo;
		m_zVelo = replay_zVelo;
	}
	
	public void resetGame(){		
		m_Zoom = -1.0f;
		m_eyeAngleX = 0.0f;
		m_eyeAngleY = (float) Math.toRadians(20.0);
		m_shootAngleX = 0.0f;
		m_shootAngleY = (float) Math.toRadians(45.0);
		m_camPos = 0.0f;
		m_ballPosX = 0.0f;
		m_ballPosY = 0.0f;
		m_ballPosZ = 1.0f;
		
		m_xVelo = 0;
		m_yVelo = 0;
		m_zVelo = 0;
		
		m_eyeX = (float) (Math.tan(m_eyeAngleX)*m_Zoom);
		m_eyeY = (float) (Math.tan(m_eyeAngleY)*m_Zoom);
		
		gameModeFlag = true;
		animateModeFlag = false;
		rimPassed = false;
		floorCollide = false;		
	}	
	
}

