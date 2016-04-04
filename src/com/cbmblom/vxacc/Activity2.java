package com.cbmblom.vxacc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class Activity2 extends Activity  {
	LinearLayout mLinearLayout;	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setTitle("Files by dr.ir. CBM Blom");
		setContentView(R.layout.activity2);
		
		mLinearLayout = (LinearLayout) findViewById(R.id.linear_layout_tags2);

		
		for(int i = 0; i < -20; i++) {
            CheckBox cb = new CheckBox(getApplicationContext());
            cb.setText("I'm dynamic! " + i);
            mLinearLayout.addView(cb);
        }

		final Button buttonC = (Button) findViewById(R.id.button2_3);		//send
	    buttonC.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	//count selected amount
	        	TableLayout _table = (TableLayout) findViewById(R.id._table);
        		int count = 0;
        		int childcount = _table.getChildCount();
        		for (int i=0; i < childcount; i++){
        		      View vv = _table.getChildAt(i);
        		      if (vv instanceof TableRow) {
        		    	  TableRow mr = (TableRow) vv;;
        		    	  //Toast.makeText(getBaseContext(),"do it: tr found " + i,Toast.LENGTH_SHORT).show();
        		    	  int childcount2 = mr.getChildCount();
        		    	  for (int j=0; j < childcount2; j++){
        		    		  View vv2 = mr.getChildAt(j);
                		      if (vv2 instanceof CheckBox) {
		        		    	  CheckBox cb = (CheckBox) vv2;
		        		    	  if (cb.isChecked()){count += 1;}

                		      }
        		    	  }
        		           
        		      }
        		}
	        	if (count != 1){
	        		//AlertDialog.Builder builder = new AlertDialog.Builder(Activity2.this);
		    		//builder.setMessage("Just select 1 file")
		    		//       .setCancelable(false)
		    		//       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    		//           public void onClick(DialogInterface dialog, int id) {
		    		//           }
		    		//       });
		    		//AlertDialog alert = builder.create();
		    		//alert.show();
		    		mSendSelectedMulti();
	        	} else {
	        		mSendSelectedMulti();
	        	}
	        	
	        }
	    });
	    
	    final Button buttonD = (Button) findViewById(R.id.button2_4);		//Load
	    buttonD.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	//count selected amount
	        	TableLayout _table = (TableLayout) findViewById(R.id._table);
        		int count = 0;
        		int childcount = _table.getChildCount();
        		for (int i=0; i < childcount; i++){
        		      View vv = _table.getChildAt(i);
        		      if (vv instanceof TableRow) {
        		    	  TableRow mr = (TableRow) vv;;
        		    	  //Toast.makeText(getBaseContext(),"do it: tr found " + i,Toast.LENGTH_SHORT).show();
        		    	  int childcount2 = mr.getChildCount();
        		    	  for (int j=0; j < childcount2; j++){
        		    		  View vv2 = mr.getChildAt(j);
                		      if (vv2 instanceof CheckBox) {
		        		    	  CheckBox cb = (CheckBox) vv2;
		        		    	  if (cb.isChecked()){
		        		    		//count ++;
		        		    		  String ms;
		        		    		  ms = cb.getText().toString();
		        		    		  File F = new File("/sdcard/" + ms);
		        		    		  if(F.exists()) {
		        							Toast.makeText(getBaseContext(),"try load '" + ms + "'",Toast.LENGTH_SHORT).show();
		        							
		        							//Read text from file
		        							StringBuilder text = new StringBuilder();

		        							try {
		        								//FileReader f;
		        								String line;
		        							    int zmax = 0;
		        							    BufferedReader br; 
		        							    
		        							    br= new BufferedReader(new FileReader(F));
		        							    while ((line = br.readLine()) != null) {
		        							        zmax += 1;
		        							    }
		        							    br.close();
		        							    //we now know max rows to read
		        							    MainActivity.md2 = new double[zmax]; 						//store recorded data
		        							    MainActivity.mt2 = new long[zmax]; 							//stores according time stamps
		        							    
		        							    br= new BufferedReader(new FileReader(F));
		        							    int z = -1;
		        							    //boolean found = false;
		        							    //String mStr = "";
		        							    //long mtest;
		        							    double md2m = -1e20;
		        							    while ((line = br.readLine()) != null) {
		        							        z += 1;
		        							        line = line.replace(",", ".");
		        							        String[] s = line.split(";");
		        							        MainActivity.mt[z] = (long) (Float.parseFloat(s[0])*1e9f); //time part
		        							        MainActivity.md[z] = Float.parseFloat(s[1]); //value part
		        							        if (MainActivity.md[z]>md2m){
		        							        	md2m = MainActivity.md[z];
		        							        }
//		        							        if (!found && z == 10){
//		        							        	mStr = s[0];
//		        							        	mtest =(long) (Float.parseFloat(s[0])*1e9f);
//		        							        	found = true;
//		        							        }

		        							    }
		        							    br.close();
		        							    MainActivity.mt2 = MainActivity.mt.clone();
		        							    MainActivity.md2 = MainActivity.md.clone();
		        							    MainActivity.ShowFFT = false;
		        							    MainActivity.md2max = md2m;
		        							    MainActivity.mcuri = z;

		        							    //Toast.makeText(getBaseContext(),"read lines # " + mStr,Toast.LENGTH_SHORT).show();
		        							    Toast.makeText(getBaseContext(),"read lines # " + z,Toast.LENGTH_SHORT).show();
		        							    //MainActivity.
		        							}
		        							catch (IOException e) {
		        							    //You'll need to add proper error handling here
		        								Toast.makeText(getBaseContext(),"failed load '" + ms + "'",Toast.LENGTH_SHORT).show();
		        							}

		        							j = childcount2;
		        							i = childcount;
		        						}
		        		    		  
		        		    	  }

                		      }
        		    	  }
        		           
        		      }
        		}
	        	//Toast.makeText(getBaseContext(),"do it: " + count,Toast.LENGTH_SHORT).show();
        		Kill(); //go back to main page
        		
	        }
	        
	    });
	    
	    
	    
		final Button buttonA = (Button) findViewById(R.id.button2_1);		//all/nothing
	    buttonA.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	TableLayout _table = (TableLayout) findViewById(R.id._table);
        		int count = 0;
        		boolean chk, found;
        		chk = false;
        		found = false;
        		//i = CheckBox.
	        	
        		int childcount = _table.getChildCount();
        		for (int i=0; i < childcount; i++){
        		      View vv = _table.getChildAt(i);
        		      if (vv instanceof TableRow) {
        		    	  TableRow mr = (TableRow) vv;;
        		    	  //Toast.makeText(getBaseContext(),"do it: tr found " + i,Toast.LENGTH_SHORT).show();
        		    	  int childcount2 = mr.getChildCount();
        		    	  for (int j=0; j < childcount2; j++){
        		    		  View vv2 = mr.getChildAt(j);
                		      if (vv2 instanceof CheckBox) {
		        		    	  CheckBox cb = (CheckBox) vv2;
		        		    	  if (!found){ chk = !cb.isChecked(); found = true;}
		        		    	  cb.setChecked(chk);
                		      }
        		    	  }
        		           
        		      }
        		}
	        	//Toast.makeText(getBaseContext(),"do it: " + count,Toast.LENGTH_SHORT).show();

	        }
	    });

		final Button buttonB = (Button) findViewById(R.id.button2_2);		//delete selected
	    buttonB.setOnClickListener(new View.OnClickListener() {
	        public void onClick(View v) {
	        	AlertDialog.Builder builder = new AlertDialog.Builder(Activity2.this);
	    		builder.setMessage("Delete?")
	    		       .setCancelable(false)
	    		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    		           public void onClick(DialogInterface dialog, int id) {
	    		                //MyActivity.this.finish();
	    		        	   mClearSelected();
	    		           }
	    		       })
	    		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
	    		           public void onClick(DialogInterface dialog, int id) {
	    		                dialog.cancel();
	    		           }
	    		       });
	    		AlertDialog alert = builder.create();
	    		alert.show();
	        	
	        }
	    });
		
		
	    mmakelist();
	}
	@Override
	protected void onResume() {
		super.onResume();

	}
	@Override
	protected void onPause() {
				super.onPause();

	}
	
	public void Kill(){
		finish();

	}
	
	public void mSendSelectedMulti(){
		TableLayout _table = (TableLayout) findViewById(R.id._table);
		int count = 0;
		ArrayList<Uri> listDumpedFileUris = new ArrayList<Uri>();
		
		
		int childcount = _table.getChildCount();
		for (int i=0; i < childcount; i++){
		      View vv = _table.getChildAt(i);
		      if (vv instanceof TableRow) {
		    	  TableRow mr = (TableRow) vv;;
		    	  //Toast.makeText(getBaseContext(),"do it: tr found " + i,Toast.LENGTH_SHORT).show();
		    	  int childcount2 = mr.getChildCount();
		    	  for (int j=0; j < childcount2; j++){
		    		  View vv2 = mr.getChildAt(j);
        		      if (vv2 instanceof CheckBox) {
        		    	  CheckBox cb = (CheckBox) vv2;
        		    	  if (cb.isChecked()){
        		    		  //count ++;
        		    		  String ms;
        		    		  ms = cb.getText().toString();
        		    		  File F = new File("/sdcard/" + ms);
        		    		  if(F.exists()) {
        							Toast.makeText(getBaseContext(),"try send 'mysdfile_" + ms + ".txt'",Toast.LENGTH_SHORT).show();
        							Uri uriFile = Uri.fromFile(F);
        							listDumpedFileUris.add(uriFile);
        							
        						   //Uri U = Uri.fromFile(F);
        						  // Intent mi;
        						   //mi = new Intent(Intent.ACTION_SEND);
        						   //mi.setType("image/png");
        						   //mi.putExtra(Intent.EXTRA_STREAM, U);
        						   //mi.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "cbmblom@gmail.com" });
        						   //String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
        						   //mi.putExtra(Intent.EXTRA_SUBJECT,"VXacc " + currentDateTimeString);
        						   //startActivity(Intent.createChooser(mi,"Email:"));
        						}
        						else
        						{
        							Toast.makeText(getBaseContext(),"no such file 'mysdfile_" + ms + ".txt'",Toast.LENGTH_SHORT).show();
        						}
        		    	  }
        		      }
		    	  }
		           
		      }
		}
		Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setType("image/jpeg");
		intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, listDumpedFileUris);
		startActivity(Intent.createChooser(intent, "Send these files using..."));
		
    	Toast.makeText(getBaseContext(),"done it: " + count,Toast.LENGTH_SHORT).show();
    	//mmakelist();
	}
	
	public void mSendSelected(){
		TableLayout _table = (TableLayout) findViewById(R.id._table);
		int count = 0;
	
		int childcount = _table.getChildCount();
		for (int i=0; i < childcount; i++){
		      View vv = _table.getChildAt(i);
		      if (vv instanceof TableRow) {
		    	  TableRow mr = (TableRow) vv;;
		    	  //Toast.makeText(getBaseContext(),"do it: tr found " + i,Toast.LENGTH_SHORT).show();
		    	  int childcount2 = mr.getChildCount();
		    	  for (int j=0; j < childcount2; j++){
		    		  View vv2 = mr.getChildAt(j);
        		      if (vv2 instanceof CheckBox) {
        		    	  CheckBox cb = (CheckBox) vv2;
        		    	  if (cb.isChecked()){
        		    		  //count ++;
        		    		  String ms;
        		    		  ms = cb.getText().toString();
        		    		  File F = new File("/sdcard/" + ms);
        		    		  if(F.exists()) {
        							Toast.makeText(getBaseContext(),"try send 'mysdfile_" + ms + ".txt'",Toast.LENGTH_SHORT).show();
        						   Uri U = Uri.fromFile(F);
        						   Intent mi;
        						   mi = new Intent(Intent.ACTION_SEND);
        						   mi.setType("image/png");
        						   mi.putExtra(Intent.EXTRA_STREAM, U);
        						   mi.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "cbmblom@gmail.com" });
        						   String currentDateTimeString = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
        						   mi.putExtra(Intent.EXTRA_SUBJECT,"VXacc " + currentDateTimeString);
        						   startActivity(Intent.createChooser(mi,"Email:"));
        						}
        						else
        						{
        							Toast.makeText(getBaseContext(),"no such file 'mysdfile_" + ms + ".txt'",Toast.LENGTH_SHORT).show();
        						}
        		    	  }
        		      }
		    	  }
		           
		      }
		}
    	Toast.makeText(getBaseContext(),"done it: " + count,Toast.LENGTH_SHORT).show();
    	//mmakelist();
	}
	
	public void mClearSelected(){
		TableLayout _table = (TableLayout) findViewById(R.id._table);
		int count = 0;
	
		int childcount = _table.getChildCount();
		for (int i=0; i < childcount; i++){
		      View vv = _table.getChildAt(i);
		      if (vv instanceof TableRow) {
		    	  TableRow mr = (TableRow) vv;;
		    	  //Toast.makeText(getBaseContext(),"do it: tr found " + i,Toast.LENGTH_SHORT).show();
		    	  int childcount2 = mr.getChildCount();
		    	  for (int j=0; j < childcount2; j++){
		    		  View vv2 = mr.getChildAt(j);
        		      if (vv2 instanceof CheckBox) {
        		    	  CheckBox cb = (CheckBox) vv2;
        		    	  if (cb.isChecked()){
        		    		  count ++;
        		    		  File file = new File("/sdcard/" + cb.getText().toString());
        		    		  file.delete();

        		    	  }
        		      }
		    	  }
		           
		      }
		}
    	Toast.makeText(getBaseContext(),"done it: " + count,Toast.LENGTH_SHORT).show();
    	mmakelist();
	}
	
	public void mmakelist(){
		TableLayout _table = (TableLayout) findViewById(R.id._table);
		int childcount = _table.getChildCount();
		int j;
		//clear the table
		for(int i = 0; i < childcount; i++){ 

			_table.removeViewAt(i); 
			childcount = _table.getChildCount();
			i--;
			
		}
		
		TableRow tableRow;
		android.widget.TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
	            ViewGroup.LayoutParams.WRAP_CONTENT,
	            ViewGroup.LayoutParams.WRAP_CONTENT);

		String dirPath;
		dirPath = "/sdcard/";
		
		File f = new File(dirPath); 
		File[] files = f.listFiles();
		Date mdate;
		for(int i=0; i < files.length; i++)	     {
			   File file = files[i];
			   String mf, mn;
			   mf = file.toString();
			   mn = file.getName();
			   if (mf.contains("mysdfile_"))
			   {
				   //allfiles += "\n" + mf ;
				   mdate = new Date(file.lastModified());
				   SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
				   String currentDateandTime = sdf.format(mdate);
				   
				   //
				   tableRow = new TableRow(this);
				   TextView tv = new TextView(getApplicationContext());
				   tv.setText(currentDateandTime);
				   tv.setLayoutParams(layoutParams);
				   tableRow.addView(tv, 0);
				   
				   CheckBox cb = new CheckBox(getApplicationContext());
		           cb.setText(mn);
		           cb.setLayoutParams(layoutParams);
		           tableRow.addView(cb, 0);
		           
		           _table.addView(tableRow, 0);
		           //mLinearLayout.addView(cb);
			   }
	       

	    }
		//Toast.makeText(getBaseContext(),"Delete \n" + allfiles ,Toast.LENGTH_LONG).show();
		
		/*
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Delete?" + allfiles)
		       .setCancelable(false)
		       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                //MyActivity.this.finish();
		        	   deleteallfiles();
		           }
		       })
		       .setNegativeButton("No", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
		*/

		
	}
	
	/*
	public void deleteallfiles(){
		String dirPath, allfiles;
		dirPath = "/sdcard/";
		allfiles = "";
		File f = new File(dirPath); 
		File[] files = f.listFiles();
		for(int i=0; i < files.length; i++)	     {
			   File file = files[i];
			   String mf;
			   mf = file.toString();
			   if (mf.contains("mysdfile_"))
			   {
				   file.delete();
			   }
	    }

		Toast.makeText(getBaseContext(),"files deleted",Toast.LENGTH_SHORT).show();
	}
	*/
}
