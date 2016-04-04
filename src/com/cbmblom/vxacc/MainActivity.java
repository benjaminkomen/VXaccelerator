package com.cbmblom.vxacc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

import android.R.integer;
import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;



public class MainActivity extends Activity implements SensorEventListener{
	
	private SensorManager mSensorManager; 		//sensor handle
	OutputStreamWriter myOutWriter;				//use for write a file to sd
	FileOutputStream fOut;
	boolean CanWrite;	   						//if user allow to write to file
	
	ImageView i;								//little graph on screen
	LinearLayout mLinearLayout;					// presentation scheme 
	TextView tview;
	
			
	long TimeNew, TimeOld, TimeStart;			// time info
	long sTimeOld, nSamples;
	double  accz;								// measured acc
	
	private Handler handler = new Handler();	// handler for timer to update info on screen
	long mDelay;								// delay id required on this handler
	
	public static double [] md; 								// if store: acc are stored here
	public static  long [] mt;									// according time 
	int mmaxi;							// max array size for date and a pointer which index we are now
	public static int mcuri;
	public static double[] md2; 				// for on screen graph we just present a part of the last samples e.g. 512
	public static long [] mt2;					// according dt
	public static double md2max;				// max value in md2				
	public static int mcuri2;					// where are we now, current index (looping)
	public static int mmaxi2;					// e.g. 512, max size to be shown
	
	double[] md2ori;							// we might present FFT in same graph, therefor remember ori data, when switch back
	long[] mt2ori;
	double md2maxori;
	int mmaxi2ori;
	
	public static boolean SensorStart;			// tells to listen to sensor or not
	public static boolean ShowFFT;				// tells that md2/mt2 contains fft or not
	boolean DoCali;								// tells we do cali, to remove gravity from acc
	double maverage;							// this is the average of cali

	public boolean onCreateOptionsMenu(Menu menu) {	// add fft to option menu
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}

	
	
	public boolean onOptionsItemSelected(MenuItem item) {
		int mo = mmaxi2;
		boolean mrem;
		boolean mredim = false;
		  mrem = SensorStart;
		  if (mrem){ stopsensor();}
		  
		  switch (item.getItemId()) {
		    case R.id.clearfiles:  	mclear();  	break;//return true;
		    case R.id.fft:  	mfft();  		break;//return true;
		    case R.id.s256:    	mmaxi2 = 256; mredim = true; i.invalidate();	break;//return true;
		    case R.id.s512:    	mmaxi2 = 512; mredim = true; i.invalidate(); 	break;//return true;
		    case R.id.s1024:   	mmaxi2 = 1024; mredim = true; i.invalidate(); 	break;//return true;
		    case R.id.s2048:   	mmaxi2 = 2048; mredim = true; i.invalidate(); 	break;//return true;
		    default:
		      //
		  }
		  if (mredim){	  
			  if (mo != mmaxi2){
				  md2 = new double[mmaxi2];
				  mt2 = new long[mmaxi2];
				  mcuri2 = 1;
			  }
		  }
		  if (mrem){startsensor();}
		  return super.onOptionsItemSelected(item);
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setTitle("by dr.ir. CBM Blom");
		setContentView(R.layout.activity_main);
		mLinearLayout = (LinearLayout) findViewById(R.id.linear_layout_tags);
		i = new MyImageView(this);
		mLinearLayout.addView(i);
		LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,340);
		i.setLayoutParams(parms);
		tview = new TextView(this);
		mLinearLayout.addView(tview);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		SensorStart = false; ShowFFT = false; DoCali = false; CanWrite = false;
		nSamples = 0;
		mDelay = 10;
		mmaxi = 1000000;
		mmaxi2 = 512;//1024;
		mcuri = 0; 	mcuri2 = 0;
		maverage = 10.09;
		md = new double[mmaxi]; 						//store recorded data
		mt = new long[mmaxi]; 							//stores according time stamps
		md2 = new double[mmaxi2]; 						//holds data for 1024 samples
		mt2 = new long[mmaxi2];  						//holds time for 1024 samples
		
		
	    
	    final Button button5 = (Button) findViewById(R.id.button5);		//calibration
	    button5.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	if (!DoCali){
	        		startsensorCali();
	        	} else{
	        		stopsensor();
	        		maverage = 0;
	        		for (int i=1; i<= mcuri; i++){
	        			maverage += md[i];
	        		}
	        		maverage /= mcuri;
	        		md2max = 0;
	        		
	        		Toast.makeText(getBaseContext(),"average: " + String.format("%.2f", maverage),Toast.LENGTH_SHORT).show();
	        	}
	        }
	    });
	    
	    final Button button4 = (Button) findViewById(R.id.button4);			//sensor
	    button4.setOnClickListener(new View.OnClickListener() {	
	        public void onClick(View v) {
	        	if (SensorStart){
		        	Toast.makeText(getBaseContext(),"Sensor stop",Toast.LENGTH_SHORT).show();
		        	stopsensor();
	        	}else{
	        		Toast.makeText(getBaseContext(),"Sensor start",Toast.LENGTH_SHORT).show();
	        		startsensor();
	        	}
	        }
	    });
	    
	    final Button button2 = (Button) findViewById(R.id.button2);			//recording
	    button2.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	Toast.makeText(getBaseContext(), "You clicked: start/stop recording", Toast.LENGTH_SHORT).show();
	        	if (CanWrite){mStop();} else {mStart();}
	        }
	    });
   
	    final Button button = (Button) findViewById(R.id.button1);			//mail
	    button.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	Toast.makeText(getBaseContext(), "You clicked: send", Toast.LENGTH_SHORT).show();
	        	if (CanWrite){
	        			Toast.makeText(getBaseContext(), "Cannot send, first click stop", Toast.LENGTH_LONG).show();
	        	}
	        	else {
	        		mMail();
	        	}
	        }
	    });
	    
	    
	    
	} //end onCreate
	

	protected void onResume() {
		super.onResume();
		SensorStart = false;
		TimeOld = System.nanoTime();//SystemClock.elapsedRealtimeNanos();// System.nanoTime();
		handler.postDelayed(runnable, mDelay);
		//i.invalidate();
	}
	
	protected void onPause() {
				super.onPause();
				stopsensor();
				handler.removeCallbacks(runnable);
	}
	
	private void startsensorCali(){
		//mSensorManager.registerListener(this,  mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
		startsensor();
		DoCali = true;
		mcuri = 0;
		//md2max  = 0.1f;
		//ShowFFT = false;
	}
	
	private void stopsensor(){
		mSensorManager.unregisterListener(this);
		SensorStart = false;
		DoCali = false;
	}
	
	private void startsensor(){
		mSensorManager.registerListener(this,  mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
				SensorManager.SENSOR_DELAY_FASTEST);
		SensorStart = true;
		md2 = new double[mmaxi2];
		mt2 = new long[mmaxi2];
		md2max  = 0.1f;
		mcuri2 = 0;
		mcuri = 0;
		ShowFFT = false;
		sTimeOld = System.nanoTime();//SystemClock.elapsedRealtimeNanos();// System.nanoTime()  ;
		nSamples = 0;
	}
	
	private Runnable runnable = new Runnable() { //this is the timer to update info on screen
		@Override
		public void run() {
			handler.postDelayed(this, mDelay);
			TextView tvX= (TextView)findViewById(R.id.x_axis);
    		tvX.setText(String.format("%.2f", maverage));
			tvX= (TextView)findViewById(R.id.a);
			tvX.setText(String.valueOf(mcuri));
		  	tvX= (TextView)findViewById(R.id.y_axis);
		  	tvX.setText(String.format("%.2f", accz - maverage));
		  	tvX= (TextView)findViewById(R.id.d);
		  	tvX.setText(String.format("%.2f", md2max));
		  	
		  	if (SensorStart){i.invalidate();}
		  	float mt;
		  	if (CanWrite){
		  		tvX= (TextView)findViewById(R.id.b);
		  		//mt = System.nanoTime() - TimeStart;
		  		mt = (System.nanoTime() - TimeStart)/1e9f; //SystemClock.elapsedRealtimeNanos()
			  	tvX.setText(String.format("%.0f", mt));
			  	/*tvX= (TextView)findViewById(R.id.c);
			  	if (mt == 0){mt=1;}
			  	tvX.setText(Float.toString(mcuri/mt*1000));*/
		  	} 
		  	if (SensorStart) 	{
		  		tvX= (TextView)findViewById(R.id.c);
		  		//mt = System.nanoTime() - sTimeOld;
		  		mt =  System.nanoTime() - sTimeOld;//SystemClock.elapsedRealtimeNanos() - sTimeOld;//
			  	if (mt == 0){mt=1;}
			  	float mv = nSamples/mt * 1e9f; //samples per millisec
			  	tvX.setText(String.format("%.1f", mv  ));
			  	if (mv == 0){mv=1;}
			  	tview.setText("time length graph: " + String.format("%.1f", mmaxi2 / mv) + " s");
		  	}
		  	
	  	  
		}
	};
	
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) { //needed to avoid class error
		// can be safely ignored for this demo
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor;
	    if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
	    	nSamples += 1;
	    	accz = event.values[2];
	    	if (CanWrite || DoCali){
	    		if (mcuri == 0){TimeStart = event.timestamp;}
		    	mcuri = mcuri + 1;
		    	md[mcuri] = Math.pow(Math.pow(event.values[0],2)+Math.pow(event.values[1],2)+Math.pow(event.values[2],2),0.5)*Math.signum(event.values[2]);
		    	mt[mcuri] = event.timestamp - TimeStart;// System.nanoTime() - TimeStart;
	    	}
	    	if (Math.abs(accz-maverage)> md2max){md2max = Math.abs(accz-maverage);}
	    	mcuri2 += 1;
	    	md2[mcuri2] = accz-maverage;
	    	mt2[mcuri2] = event.timestamp - TimeOld;// System.nanoTime() - TimeOld;
	    	TimeOld = event.timestamp;//SystemClock.elapsedRealtimeNanos();//System.nanoTime();
	    	if (mcuri2 >= mmaxi2-1){mcuri2 = 0;}
	    	if (mcuri2 % 100 == 0){md2max  *= 0.9;} //slowly decreasing scaler for graph
	    }
	    if (mcuri > mmaxi){mcuri = 0;}
	}
	
	public void mMail(){
		try 
		   {   
				TextView mv= (TextView)findViewById(R.id.num);
				String ms = mv.getText().toString();
				File F = new File("/sdcard/mysdfile_" + ms + ".txt");
				if(F.exists()) {
					
					Toast.makeText(getBaseContext(),"try send 'mysdfile_" + ms + ".txt'",Toast.LENGTH_SHORT).show();
				   //File F = new File("/sdcard/mysdfile.txt");
				   Uri U = Uri.fromFile(F);
				   Intent i;
				   i = new Intent(Intent.ACTION_SEND);
				   //i = new Intent(Intent.ACTION_SEND_MULTIPLE);
				   i.setType("image/png");
				   i.putExtra(Intent.EXTRA_STREAM, U);
				   i.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "cbmblom@gmail.com" });
				   String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new Date());
				   i.putExtra(Intent.EXTRA_SUBJECT,"VXacc " + currentDateTimeString);
				   startActivity(Intent.createChooser(i,"Email:"));
				}
				else
				{
					Toast.makeText(getBaseContext(),"no such file 'mysdfile_" + ms + ".txt'",Toast.LENGTH_SHORT).show();
				}
				
		   } 
	   catch (Exception e) {}
	}
	
	protected void mStart(){
		// write on SD card file data in the text box
		try {
			CanWrite = true;
			stopsensor();
			startsensor();
			TimeStart = System.nanoTime();//SystemClock.elapsedRealtimeNanos();// System.nanoTime();		
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
		}
	}
	
	protected void mStop(){
		CanWrite = false;
		Toast.makeText(getBaseContext(),
				"Stop recording. Samples to write = " + mcuri, Toast.LENGTH_SHORT).show();
		try {
			stopsensor();
			
			TextView mv= (TextView)findViewById(R.id.num);
    		//String ms = mv.getText().toString();
    		boolean found = false;
    		File myFile;
    		int mnum = 0;
    		myFile = new File("/sdcard/mysdfile_" + String.valueOf(mnum) + ".txt");
    		while (!found){
    			
    			myFile = new File("/sdcard/mysdfile_" + String.valueOf(mnum) + ".txt");
    			if (!myFile.exists()){found = true;} else {mnum++;}
    		}
    		mv.setText(String.valueOf(mnum));
			myFile.createNewFile();
			fOut = new FileOutputStream(myFile);
			myOutWriter = new OutputStreamWriter(fOut);
			String s, ss;
//			s = Double.toString(TimeStart);
//			s = s.replace(".", ",");
//			ss = Double.toString(SystemClock.elapsedRealtimeNanos());
//			ss = ss.replace(".", ",");
//			myOutWriter.append(ss + ";" + s + "\n");
			//myOutWriter.append(mcuri + ";" + 0 + "\n"); //write number of samples
			for (int i=1; i<= mcuri; i++){
				s = Double.toString(md[i] - maverage);
				s = s.replace(".", ",");
				ss = Double.toString(mt[i]/1e9f);
				ss = ss.replace(".", ",");
				
				myOutWriter.append(ss + ";" + s + "\n");
			
			} 
			
//			if (mcuri > 0){
//				double sum;
//				double nextT, dT;
//				long sumI, k;
//				dT = 0.01;
//				nextT = mt[0]/1e9f + dT;
//				sum = 0; sumI = 0;
//			
//				for (int i=0; i<= mcuri; i++){
//					
//					sum += md[i];
//					sumI += 1;
//					if (mt[i]/1e9f > nextT){
//						//String s = Double.toString(md[i] - maverage);
//						String s = Double.toString(sum/sumI - maverage);
//						s = s.replace(".", ",");
//						//myOutWriter.append(mt[i] + ";" + s + "\n");
//						String ss = Double.toString(nextT);
//						ss = ss.replace(".", ",");
//						myOutWriter.append(ss + ";" + s + ";" + "\n");
//						// nextT = Math.floor(mt[i]/1e9 /dT) * dT + dT; //deze rondt af op 2 decimalen
//						nextT = mt[i]/1e9f + dT;
//						sum = 0; sumI = 0;
//					}
//				}
//			}
			myOutWriter.close();
			fOut.close();
			
			Toast.makeText(getBaseContext(),"Done writing SD 'mysdfile_" + String.valueOf(mnum) + ".txt'",Toast.LENGTH_SHORT).show();
			
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
		}
	}
	
	

	public void mclear(){
		startActivity(new Intent(MainActivity.this, Activity2.class));
	}
	
	
	public void mfft(){
		
		
		if (!ShowFFT){
			Toast.makeText(getBaseContext(),"You clicked: fft",Toast.LENGTH_SHORT).show();
			md2ori = md2.clone();
			mt2ori = mt2.clone();
			md2maxori = md2max;
			mmaxi2ori =mmaxi2;
			//FFT mfft = new FFT(mmaxi2);
			int z;
			int retN[], retM[];
			retN = new int[1];
			retM = new int[1];
			//retN = -1; retM = -1;
			
			z = mcuri;// mmaxi2;
			
			FFT mfft = new FFT(z,retN,retM);

			mmaxi2 = retN[0];
			double md2reeel[], md2complex[];
			md2reeel = new double[mmaxi2];
			md2complex = new double[mmaxi2];
			
			mfft.fft( md2reeel, md2complex, md);
			
			int j;
			md2 = new double[mmaxi2];
			mt2 = new long[mmaxi2];
		
			for (j=0;j< mmaxi2;j++){ //calc amplitude of complex data
				md2[j] = Math.pow((Math.pow( md2reeel[j],2) + Math.pow(md2complex[j],2)),0.5);
				mt2[j] = mt[j];// (long) ((long) j*incr);
			}
			md2reeel = null; md2complex = null;
			
			md2max = 0;
			for (j=0;j< mmaxi2;j++){
				if (Math.abs(md2[j])>md2max){md2max=Math.abs(md2[j]); }
			}
			ShowFFT = true;
		} else {
			Toast.makeText(getBaseContext(),"Hide fft",Toast.LENGTH_SHORT).show();
			md2 = md2ori.clone();
			mt2 = mt2ori.clone();
			md2max = md2maxori;
			mmaxi2 = mmaxi2ori;
			ShowFFT = false;
			
		}
		i.invalidate();
	} //end fft
	
	public void mfft_BK(){
		Toast.makeText(getBaseContext(),"You clicked: fft",Toast.LENGTH_SHORT).show();
		
		if (!ShowFFT){
			md2ori = md2.clone();
			mt2ori = mt2.clone();
			md2maxori = md2max;
			//FFT mfft = new FFT(mmaxi2);
			int retN[], retM[];
			retN = new int[1];
			retM = new int[1];
			FFT mfft = new FFT(mmaxi2, retN, retM);
			
			double md2reeel[], md2complex[];
			md2reeel = new double[mmaxi2];
			md2complex = new double[mmaxi2];
			
			mfft.fft( md2reeel, md2complex, md2);
			
			int j;
			for (j=0;j< mmaxi2;j++){ //calc amplitude of complex data
				md2[j] = Math.pow((Math.pow( md2reeel[j],2) + Math.pow(md2complex[j],2)),0.5);
			}
			md2reeel = null; md2complex = null;
			
			md2max = 0;
			for (j=0;j< mmaxi2;j++){
				if (Math.abs(md2[j])>md2max){md2max=Math.abs(md2[j]);}
			}
			ShowFFT = true;
		} else {
			md2 = md2ori.clone();
			mt2 = mt2ori.clone();
			md2max = md2maxori;
			ShowFFT = false;
			
		}
		i.invalidate();
	} //end fft
} //end class
