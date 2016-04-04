package com.cbmblom.vxacc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.view.View.OnTouchListener;


public class MyImageView extends ImageView implements OnTouchListener {
	Paint cp;
	boolean fromline = false;
	float mw, mh;
	
	float downx = 0,downy = 0,upx = 0,upy = 0;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		getParent().requestDisallowInterceptTouchEvent(true); //this is required to avoid cancel during ontouch
		upx = event.getX();
		if (event.getAction() == MotionEvent.ACTION_MOVE ){this.invalidate();}
	    return true;
	}
	
		
	public MyImageView(Context context) {
        super(context);
        setOnTouchListener(this);
        cp = new Paint();
        cp.setDither(true);
        cp.setColor(0xFF00CC00);  // alpha.r.g.b
        cp.setStyle(Paint.Style.FILL);
        cp.setStrokeJoin(Paint.Join.ROUND);
        cp.setStrokeCap(Paint.Cap.ROUND);
        cp.setStrokeWidth(2);
        cp.setTextSize(30);
        
        
    }

	
    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        
        float mx,xf, yf,y,x,md2max;
        int mmaxi2, tomax, i, z;
        double mtot = 0;
        String mstr, mstr1;
        Rect bounds = new Rect();
        
        mw = c.getWidth();
        mh = c.getHeight();
        
        // square around graph
        cp.setColor(Color.LTGRAY);
        c.drawLine(0,0,mw,0,cp);
        c.drawLine(mw,0,mw,mh,cp);
        c.drawLine(mw,mh,0,mh,cp);
        c.drawLine(0,mh,0,0,cp);
        
        //hor line in the middle
        float h2 = mh/2;
        if (MainActivity.ShowFFT){h2 = mh;}
        xf = 0; yf = h2;
        c.drawLine(0, yf, mw,yf, cp);				
        
        cp.setColor(Color.GREEN);
        md2max =(float)MainActivity.md2max;
        if (md2max == 0){md2max = 1;}
        mmaxi2 = MainActivity.mmaxi2;
    	tomax = mmaxi2;

    	double t1 = 0, t2 = 0, t3 = 0, mval;
    	int i1 = 0, i2 = 0, i3 = 0;
    	if (MainActivity.ShowFFT){
    		//for (i=1;i<tomax;i++){mtot = MainActivity.mt2[i];}
    		mtot = MainActivity.mt2[tomax-1];
    		tomax /= 2; 
    		for (i=1;i<tomax;i++){
    			mval = MainActivity.md2[i];
    			if (mval > t3){
    				t3 = mval; i3 = i;
    				if (t3 > t2){
    					t3 = t2; i3 = i2; t2 = mval; i2 = i;
    					if (t2 > t1){
        					t2 = t1; i2 = i1; t1 = mval; i1 = i;
        				}
    				}
    			}
    			
    		}
    	}
        for (i=1;i<tomax;i++){
        	x = (float) i / tomax * mw ;
        	y =  (float) (-MainActivity.md2[i] / md2max * h2 + h2);
        	c.drawLine(xf, yf, x, y, cp);
        	xf = x; yf = y;
        }
        
        // vertical line when ontouch
        cp.setColor(Color.YELLOW);
        mx = (float) ((upx- mw/2) * 1.2 + mw/2);
        if(mx < 0){mx = 0;}
        if (mx > mw){mx = mw-1;}
        c.drawLine(mx, 0, mx, mh, cp);
        
        //search value in arrays
        z = (int) (mx / mw * tomax);
        if (z > mmaxi2){z = mmaxi2;}
        float mm = 1e9f;
        if (MainActivity.ShowFFT){
        	mstr = String.format("%.2f", z / mtot * mm ) + " Hz";
        	//mstr = "Mt " + MainActivity.mt2[1]  + " ";
        	//mstr = String.format("%.2f", MainActivity.md2[124] ) + " ";
        	
        	cp.setTextSize(24);
        	mstr1 = String.format("%.2f", i1 / mtot * mm ) + " Hz";		//show frequency
        	cp.getTextBounds(mstr1, 0, mstr1.length(), bounds);
            c.drawText(mstr1,mw-bounds.width(),bounds.height()*3,cp);
            
            mstr1 = String.format("%.2f", i2 / mtot * mm ) + " Hz";		//show frequency
        	cp.getTextBounds(mstr1, 0, mstr1.length(), bounds);
            c.drawText(mstr1,mw-bounds.width(),bounds.height()*4.2f,cp);

            mstr1 = String.format("%.2f", i3/ mtot * mm ) + " Hz";		//show frequency
        	cp.getTextBounds(mstr1, 0, mstr1.length(), bounds);
            c.drawText(mstr1,mw-bounds.width(),bounds.height()*5.4f,cp);

        } else {
        	mstr = String.format("%.2f", MainActivity.md2[z] ) + " m/s2"; 	//show acc
        }
        cp.setTextSize(30);
        cp.getTextBounds(mstr, 0, mstr.length(), bounds);
        c.drawText(mstr,mw-bounds.width(),bounds.height(),cp);
    }
}
