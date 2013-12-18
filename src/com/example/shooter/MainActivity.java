package com.example.shooter;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements SensorEventListener {
	MyGLSurface m_glSurface;
	Button m_moveLeft;
	Button m_moveRight;
	Button m_lookLeft;
	Button m_lookRight;
	Button m_lookUp;
	Button m_lookDown;
	
	Button m_reset;
	Button m_replay;
	
	private SensorManager m_sensorManager;
	private Sensor m_accelerometer;
	
	private float m_previousZ;
	private float m_timestamp;
	private final float VELOCITY_SCALE = (float) 1.5E7f;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		m_glSurface = (MyGLSurface) findViewById(R.id.surface);
		
		m_moveLeft = (Button)findViewById(R.id.moveLeft);
		m_moveRight = (Button)findViewById(R.id.moveRight);
		m_moveLeft.setText("<-");
		m_moveRight.setText("->");
		m_moveLeft.setOnClickListener(move);
		m_moveRight.setOnClickListener(move);
		
		m_lookLeft = (Button)findViewById(R.id.lookLeft);
		m_lookRight = (Button)findViewById(R.id.lookRight);
		m_lookUp = (Button)findViewById(R.id.lookUp);
		m_lookDown = (Button)findViewById(R.id.lookDown);
		m_reset = (Button)findViewById(R.id.resetBut);
		m_replay = (Button)findViewById(R.id.replayBut);
		
		m_lookLeft.setOnClickListener(look);
		m_lookRight.setOnClickListener(look);
		m_lookUp.setOnClickListener(look);
		m_lookDown.setOnClickListener(look);
		m_reset.setOnClickListener(reset);
		m_replay.setOnClickListener(replay);
		
		m_sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		m_accelerometer = m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		m_previousZ = 0.0f;
		m_timestamp = 0.0f;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		m_sensorManager.registerListener(this, m_accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		m_sensorManager.unregisterListener(this);
	}
	
	View.OnClickListener reset = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			m_glSurface.resetGame();		
		}
	};
	
	View.OnClickListener replay = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			m_glSurface.startReplay();		
		}
	};
	
	View.OnClickListener move = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			float moveAmount = 0.5f;
			if(v.getId() == m_moveLeft.getId())
				m_glSurface.move(-moveAmount);
			if(v.getId() == m_moveRight.getId())
				m_glSurface.move(moveAmount);		
		}
	};
	
	View.OnClickListener look = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			float rotateAngle = (float)Math.toRadians(2.0f);
			
			if(v.getId() == m_lookLeft.getId())
				m_glSurface.rotateX(rotateAngle);
			if(v.getId() == m_lookRight.getId())
				m_glSurface.rotateX(-rotateAngle);
			if(v.getId() == m_lookUp.getId())
				m_glSurface.rotateY(rotateAngle);
			if(v.getId() == m_lookDown.getId())
				m_glSurface.rotateY(-rotateAngle);
			
		}
		
	};
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		float curZ = event.values[2];
		float curTime = event.timestamp;
		//only get velocity if the difference of angular axis is large enough
		if(m_timestamp != 0.0f && Math.abs(curZ-m_previousZ)>=2.0f){			
			float velocity = VELOCITY_SCALE*(curZ-m_previousZ)/(curTime-m_timestamp);			
			if(velocity>0.0f)
				m_glSurface.shootBall(velocity);
		}
		m_timestamp = curTime;
		m_previousZ = curZ;
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}


}
