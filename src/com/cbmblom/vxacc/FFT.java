package com.cbmblom.vxacc;

import android.widget.Toast;

public class FFT {
	
	
	int n, m;

	  // Lookup tables. Only need to recompute when size of FFT changes.
	  double[] cos;
	  double[] sin;

	  public FFT(int n, int[] retN, int[] retM) {
	      //this.n = n;
	      //hier wil ik dat n een int kan zijn van willekeurige grootte
	      //dan m bepalen welke de macht van 2 is
	      //die dan het dichtst onder n komt
	      //vervolgens n bepalen die nog mag, en die vervolgens gebruiken in de analyse
	      //iets met math.floor oid
	      this.m = (int) Math.floor( (Math.log(n) / Math.log(2)));
	      //retN[0] = (int) Math.pow(2, m);
	      retM[0] = this.m;
	      n =(int) Math.pow(2, m);
	      this.n = n;
	      retN[0] = n;

	      // Make sure n is a power of 2
	      if (n != (1 << m))
	          throw new RuntimeException("FFT length must be power of 2");

	      // precompute tables
	      cos = new double[n / 2];
	      sin = new double[n / 2];

	      for (int i = 0; i < n / 2; i++) {
	          cos[i] = Math.cos(-2 * Math.PI * i / n);
	          sin[i] = Math.sin(-2 * Math.PI * i / n);
	      }

	  }

	  public void fft(double[] x, double[] y, double[] xinp) {
	      int i, j, k, n1, n2, a;
	      double c, s, t1, t2;

	      x = xinp.clone();
	      //y = yinp.clone();
	      
	      // Bit-reverse
	      j = 0;
	      n2 = n / 2;
	      for (i = 1; i < n - 1; i++) {
	          n1 = n2;
	          while (j >= n1) {
	              j = j - n1;
	              n1 = n1 / 2;
	          }
	          j = j + n1;

	          if (i < j) {
	              t1 = x[i];
	              x[i] = x[j];
	              x[j] = t1;
	              t1 = y[i];
	              y[i] = y[j];
	              y[j] = t1;
	          }
	      }

	      // FFT
	      n1 = 0;
	      n2 = 1;

	      for (i = 0; i < m; i++) {
	          n1 = n2;
	          n2 = n2 + n2;
	          a = 0;

	          for (j = 0; j < n1; j++) {
	              c = cos[a];
	              s = sin[a];
	              a += 1 << (m - i - 1);

	              for (k = j; k < n; k = k + n2) {
	                  t1 = c * x[k + n1] - s * y[k + n1];
	                  t2 = s * x[k + n1] + c * y[k + n1];
	                  x[k + n1] = x[k] - t1;
	                  y[k + n1] = y[k] - t2;
	                  x[k] = x[k] + t1;
	                  y[k] = y[k] + t2;
	              }
	          }
	      }
	  }

}
