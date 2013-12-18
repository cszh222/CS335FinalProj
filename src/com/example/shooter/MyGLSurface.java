package com.example.shooter;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.widget.Toast;

public class MyGLSurface extends GLSurfaceView implements OnScaleGestureListener {
	private MyGLSurfaceRender m_glRenderer;
	
	private Context m_ctx;
	private final float TOUCH_SCALE_FACTOR = 180.0F / 320;
	private float m_PreviousX;
	private float m_PreviousY;
	
	private Timer playTimer;
	private CustomHandler playHandler = new CustomHandler();
	
	public final static int REFRESH_RATE = 30;
	
	private ScaleGestureDetector sgd;
	private boolean isScaling;
	
	public MyGLSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		m_ctx = context;
		sgd = new ScaleGestureDetector(context, this);
		// Create and OpenGL ES 2.0 context
		setEGLContextClientVersion(2);

		// Set the Renderer for drawing on the GLSurfaceView
		m_glRenderer = new MyGLSurfaceRender();
		setRenderer(m_glRenderer);
		
		// Render the view only when there is a change in the drawing data
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY); // comment out for auto-rotation
	}
	
	
	public void move(float moveCoord){
		if(m_glRenderer.isGameMode() && !m_glRenderer.isAnimating()){
			m_glRenderer.changePos(moveCoord);
			requestRender();
		}
	}

	public void rotateX(float angle) {
		if(m_glRenderer.isGameMode() && !m_glRenderer.isAnimating()){
			m_glRenderer.setEyeAngleX(angle);
			m_glRenderer.setShootAngleX(angle);
			requestRender();
		}		
	}
	
	public void rotateY(float angle){
		if(m_glRenderer.isGameMode() && !m_glRenderer.isAnimating()){
			m_glRenderer.setEyeAngleY(angle);
			m_glRenderer.setShootAngleY(angle);
			requestRender();
		}
	}


	@Override
	public boolean onTouchEvent(MotionEvent e) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.
		
		if(m_glRenderer.isGameMode())//dont allow touch event during game mode
			return true;
		
		sgd.onTouchEvent(e);
		if(isScaling)
			return true;//currently scaling, dont rotate
		float x = e.getX();
		float y = e.getY();
		
		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			
			float dx = (m_PreviousX - x)*TOUCH_SCALE_FACTOR;
			float dy = (m_PreviousY - y)*TOUCH_SCALE_FACTOR;
			float angleX, angleY;

			if(Math.abs(dx) > Math.abs(dy)){
				//calculate angle for x
				angleX = (float) ((Math.atan((dx/2)/Math.abs(m_glRenderer.getZoom())))*
						(180/Math.PI)*TOUCH_SCALE_FACTOR);
				m_glRenderer.setEyeAngleX(angleX/1000f);//Touch event causing too much rotation, needs to be scaled down				
			}
			
			else if (Math.abs(dy) > Math.abs(dx)){
				angleY = (float) ((Math.atan((dy/2)/Math.abs(m_glRenderer.getZoom())))*
						(180/Math.PI)*TOUCH_SCALE_FACTOR);
				m_glRenderer.setEyeAngleY(angleY/1000f);//Touch event causing too much rotation, needs to be scaled down
			}
			requestRender();
		}
		
		m_PreviousX = x;
		m_PreviousY = y;
		
		return true;
	}

	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float scaleFactor = detector.getScaleFactor();
		Log.i("GLEX Error: ", Float.toString(scaleFactor));
		if(scaleFactor>1.0f)//scaling out
			m_glRenderer.setCamPosZ(-0.2f);
		else if (scaleFactor < 1.0f)//scaling in
			m_glRenderer.setCamPosZ(0.2f);
		requestRender();
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector arg0) {
		isScaling = true;//set scaling flag to prevent rotation	
		//Log.i("GLES Error:", "here");
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector arg0) {
		isScaling = false;//reset scaling flag		
	}


	public void shootBall(float velocity) {
		if(!m_glRenderer.isAnimating()){
			m_glRenderer.setVelocity(velocity);
			startAnimating();
		}		
	}


	private void startAnimating() {
		m_glRenderer.setAnimateFlag(true);
		startTimer();
		//set initial time
		//have timer task move the ball	
		//have timer check game over 
	}
	
	private void stopAnimating() {
		//TODO
		cancelTimer();
	}
	
	public void startTimer() {
		if(playTimer != null) {
			playTimer.cancel();
			playTimer = null;
		}
		playTimer = new Timer();
		CustomTimerTask customTimerTask = new CustomTimerTask();
		playTimer.scheduleAtFixedRate(customTimerTask, REFRESH_RATE, REFRESH_RATE);
	}
	
	public void cancelTimer() {
		if (playTimer != null) {
			playTimer.cancel();
	        playTimer= null;
		}
	}
	
	//classes for timers
		class CustomTimerTask extends TimerTask {
			 
	        @Override
	        public void run() {
	            playHandler.sendEmptyMessage(0);
	        }
	    };

	    class CustomHandler extends Handler {
	        @Override
	        public void handleMessage(Message msg) {
	            super.handleMessage(msg);
	            System.out.println("ABDEBUG: timerHandler - entering");
	            
	            m_glRenderer.moveBall(REFRESH_RATE);
	            m_glRenderer.detectBoardCollision();
	            m_glRenderer.detectFloorCollision();
	            requestRender();
	            
	            if(m_glRenderer.isGameMode()) {
	            	cancelTimer();
	            	displayEndGame();
	            }
	        }
	   };
	   
	   public void displayEndGame() {
		   Toast.makeText(m_ctx, "Game Over", Toast.LENGTH_SHORT).show();
	   }
}