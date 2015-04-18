package com.mygatech;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GuestStudent extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gueststudent);
		getActionBar().hide();
		Button guest = (Button)findViewById(R.id.guest);
		Button student = (Button)findViewById(R.id.student);
//		guest.getBackground().setColorFilter(Color.YELLOW,PorterDuff.Mode.MULTIPLY);
		guest.setAlpha((float) 1.7);
		student.setAlpha((float)1.7);
//		student.getBackground().setColorFilter(Color.YELLOW,PorterDuff.Mode.SCREEN);
		Typeface tf =  Typeface.createFromAsset(getAssets(), "myriadwebpro-bold.ttf");
		guest.setTypeface(tf);
		guest.setText("    Guest    ");
		student.setTypeface(tf);
		student.setText("  Student  ");
		final SharedPreferences sp = getSharedPreferences(MainActivity.USER_DETAILS, 0);
		
		
		((TextView)findViewById(R.id.textApp)).setText("Two\nButtons\nFor\nMy GaTech");
		((TextView)findViewById(R.id.textApp)).setTypeface(Typeface.createFromAsset(getAssets(), "HelveticaNeue-Light.ttf"));
		
		guest.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	 (sp.edit()).putBoolean("Student", false).commit();
            	 (sp.edit()).putBoolean("hasLoggedIn", true).commit();

            	 Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
     			 mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
     			 startActivity(mainIntent);
     			 overridePendingTransition(R.anim.toleftenter, R.anim.toleftleave);
             }
         });
		student.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						StartActivity.class));
				overridePendingTransition(R.anim.toleftenter, R.anim.toleftleave);
			}
		});

	}
}
