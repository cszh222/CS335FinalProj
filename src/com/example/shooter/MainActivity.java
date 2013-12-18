package com.example.shooter;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	MyGLSurface m_glSurface;
	Button m_moveLeft;
	Button m_moveRight;
	Button m_lookLeft;
	Button m_lookRight;
	Button m_lookUp;
	Button m_lookDown;
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
		m_lookLeft.setOnClickListener(look);
		m_lookRight.setOnClickListener(look);
		m_lookUp.setOnClickListener(look);
		m_lookDown.setOnClickListener(look);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
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

}
