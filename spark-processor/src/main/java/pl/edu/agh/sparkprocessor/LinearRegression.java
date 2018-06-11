package pl.edu.agh.sparkprocessor;

import java.util.List;

public class LinearRegression {
    private static final int MAXN = 100000;
    private static final double[] x = new double[MAXN];
    private static final double[] y = new double[MAXN];
    private static final boolean verbose =true;
    static {
        for (int i = 0; i < MAXN; i++) {
            x[i] = i;
        }
    }

    public static String regression() {
        //https://introcs.cs.princeton.edu/java/97data/LinearRegression.java.html
                int n = 0;

                // first pass: read in data, compute xbar and ybar
                double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
                for(int i=0; i<MAXN;i++){
                    sumx  += x[n];
                    sumx2 += x[n] * x[n];
                    sumy  += y[n];
                    n++;
                }
                double xbar = sumx / n;
                double ybar = sumy / n;

                // second pass: compute summary statistics
                double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
                for (int i = 0; i < n; i++) {
                    xxbar += (x[i] - xbar) * (x[i] - xbar);
                    yybar += (y[i] - ybar) * (y[i] - ybar);
                    xybar += (x[i] - xbar) * (y[i] - ybar);
                }
                double beta1 = xybar / xxbar;
                double beta0 = ybar - beta1 * xbar;

                // print results
        String res ="y   = " + beta1 + " * x + " + beta0;

        if(verbose) {
            // analyze results
            int df = n - 2;
            double rss = 0.0;      // residual sum of squares
            double ssr = 0.0;      // regression sum of squares
            for (int i = 0; i < n; i++) {
                double fit = beta1 * x[i] + beta0;
                rss += (fit - y[i]) * (fit - y[i]);
                ssr += (fit - ybar) * (fit - ybar);
            }
            double R2 = ssr / yybar;
            double svar = rss / df;
            double svar1 = svar / xxbar;
            double svar0 = svar / n + xbar * xbar * svar1;
            double svar0_ = svar * sumx2 / (n * xxbar);

            System.out.println("y   = " + beta1 + " * x + " + beta0);

            System.out.println("R^2                 = " + R2);
            System.out.println("std error of beta_1 = " + Math.sqrt(svar1));
            System.out.println("std error of beta_0 = " + Math.sqrt(svar0));
            System.out.println("std error of beta_0 = " + Math.sqrt(svar0_));

            System.out.println("SSTO = " + yybar);
            System.out.println("SSE  = " + rss);
            System.out.println("SSR  = " + ssr);
        }
            return res;
        }




    public static void addNum(double d){
        System.arraycopy(y, 0, y, 1, MAXN - 1);
        y[0]=d;
    }


    public static void main(String[] args){
        LinearRegression r = new LinearRegression();
        for(int i=0;i<MAXN;i++){
            r.y[i]=i;
        }
        System.out.println(r.y[0] +" "+r.y[1]+" "+r.y[2] + ".."+r.y[MAXN-2]+" "+r.y[MAXN-1]);
        System.out.println(r.regression());
        r.addNum(-1);
        System.out.println(r.y[0] +" "+r.y[1]+" "+r.y[2] + ".."+r.y[MAXN-2]+" "+r.y[MAXN-1]);
        System.out.println(r.regression());
    }
}
